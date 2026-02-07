package com.beerlot.domain.member.service;

import com.beerlot.domain.auth.security.oauth.entity.OAuthUserPrincipal;
import com.beerlot.domain.auth.security.oauth.entity.ProviderType;
import com.beerlot.domain.auth.security.oauth.handler.OAuthUnlinkHandler;
import com.beerlot.domain.member.Member;
import com.beerlot.domain.member.MemberStatus;
import com.beerlot.domain.member.RoleType;
import com.beerlot.domain.member.dto.request.CheckUsernameRequest;
import com.beerlot.domain.member.dto.request.MemberProfileRequest;
import com.beerlot.domain.member.dto.request.MemberRequest;
import com.beerlot.domain.member.dto.request.MemberStatusRequest;
import com.beerlot.domain.member.dto.response.CheckUsernameResponse;
import com.beerlot.domain.member.dto.response.MemberExitResponse;
import com.beerlot.domain.member.dto.response.MemberResponse;
import com.beerlot.domain.member.dto.response.MemberStatusResponse;
import com.beerlot.domain.member.repository.MemberRepository;
import com.beerlot.domain.policy.PolicyType;
import com.beerlot.exception.ConflictException;
import com.beerlot.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    private final OAuth2AuthorizedClientService authorizedClientService;

    private final OAuthUnlinkHandler oAuthUnlinkHandler;

    public Optional<Member> findMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Member findMemberByOauthId(String oauthId) {
        return memberRepository.findByOauthId(oauthId)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.MEMBER__NOT_EXIST.getMessage()));
    }

    public Member createMember(OAuthUserPrincipal oAuthUser) {
        Member member = Member.builder()
                .oauthId(oAuthUser.getId())
                .email(oAuthUser.getEmail())
                .username(oAuthUser.getUsername())
                .imageUrl(oAuthUser.getImageUrl())
                .provider(oAuthUser.getProvider())
                .roles(Set.of(RoleType.GUEST))
                .build();
        return memberRepository.save(member);
    }

    public long countByUsername(String username) {
        return memberRepository.countByUsername(username);
    }

    public void updateEmail(Member member, String email) {
        member.updateEmail(email);
    }

    public void updateStatusToActive(Member member) {
        member.active();
        member.setUsernameUpdatedAtToNow();
    }

    public void signUpMember(Member member, MemberRequest memberRequest) {
        if (member.getRoles().contains(RoleType.MEMBER)) {
            throw new ConflictException(ErrorMessage.MEMBER__ALREADY_SIGNED_UP.getMessage());
        }

        PolicyType.validateAgreeOnRequiredPolicies(memberRequest.getAgreedPolicies());
        member.updateProfile(MemberProfileRequest.builder()
                .statusMessage(memberRequest.getStatusMessage())
                .imageUrl(memberRequest.getImageUrl())
                .build());

        if (countByUsername(memberRequest.getUsername()) != 0) {
            throw new ConflictException(ErrorMessage.MEMBER__USERNAME_ALREADY_EXIST.getMessage());
        }
        member.updateUsername(memberRequest.getUsername());

        member.updateAgreedPolicies(memberRequest.getAgreedPolicies());
        member.addRole(RoleType.MEMBER);
        member.setUsernameUpdatedAtToNow();
    }

    public MemberResponse getProfile(Member member) {
        return MemberResponse.of(member);
    }

    public MemberResponse updateProfile(Member member, MemberProfileRequest memberProfileRequest) {
        if (!member.getUsername().equals(memberProfileRequest.getUsername())) {
            if (!canUpdateUsername(member)) {
                throw new IllegalStateException(ErrorMessage.MEMBER__USERNAME_30DAYS.getMessage());
            }
            if (memberRepository.existsByUsername(memberProfileRequest.getUsername())) {
                throw new ConflictException(ErrorMessage.MEMBER__USERNAME_ALREADY_EXIST.getMessage());
            }
            member.updateUsername(memberProfileRequest.getUsername());
            member.setUsernameUpdatedAtToNow();
        }
        member.updateProfile(memberProfileRequest);
        return getProfile(member);
    }

    public MemberStatusResponse getMemberStatus(MemberStatusRequest memberStatusRequest) {
        Optional<Member> foundMember = memberRepository.findByEmail(memberStatusRequest.getEmail());

        if (foundMember.isEmpty()) {
            return MemberStatusResponse.builder()
                    .status(MemberStatus.NOT_EXIST)
                    .provider(null)
                    .build();
        }

        Member member = foundMember.get();

        return MemberStatusResponse.builder()
                .status(member.getStatus())
                .provider(member.getProvider())
                .build();
    }

    public CheckUsernameResponse checkDuplicateUsername(CheckUsernameRequest checkUsernameRequest) {
        boolean isAlreadyExist = memberRepository.existsByUsername(checkUsernameRequest.getUsername());

        return CheckUsernameResponse.builder()
                .taken(String.valueOf(isAlreadyExist))
                .build();
    }

    public MemberExitResponse exitUser(OAuthUserPrincipal oAuthUser) {
        Optional<Member> maybeMember = memberRepository.findByOauthId(oAuthUser.getOauthId());

        if (maybeMember.isEmpty()) {
            throw new IllegalArgumentException(ErrorMessage.MEMBER__NOT_EXIST.getMessage());
        }

        Member currentMember = maybeMember.get();
        ProviderType provider = currentMember.getProvider();

        currentMember.exit();
        currentMember.setUsernameUpdatedAtToNow();

        exitUserFromProvider(oAuthUser);

        return MemberExitResponse.builder()
                .email(currentMember.getEmail())
                .provider(provider)
                .status(currentMember.getStatus())
                .exitedAt(currentMember.getUsernameUpdatedAt())
                .build();
    }
  
    private boolean canUpdateUsername(Member member) {
        return member.getUsernameUpdatedAt().isBefore(OffsetDateTime.now().minus(Duration.ofDays(30)));
    }

    private void exitUserFromProvider(OAuthUserPrincipal oAuthUser) {
        OAuth2AuthorizedClient authorizedClient =
                authorizedClientService.loadAuthorizedClient(
                        oAuthUser.getProvider().getRegistrationId(),
                        oAuthUser.getName());

        if (authorizedClient == null) {
            throw new IllegalStateException(ErrorMessage.TOKEN__EXPIRED.getMessage());
        }

        OAuth2AccessToken clientAccessToken = authorizedClient.getAccessToken();
        String accessToken = clientAccessToken.getTokenValue();

        boolean isHandled = oAuthUnlinkHandler.unlinkProvider(oAuthUser.getProvider(), accessToken);
        if (!isHandled) {
            throw new IllegalArgumentException(ErrorMessage.MEMBER__FAILED_TO_REVOKE_OAUTH.getMessage());
        }
    }
}
