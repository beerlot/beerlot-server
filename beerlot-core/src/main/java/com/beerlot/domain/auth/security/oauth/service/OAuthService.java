package com.beerlot.domain.auth.security.oauth.service;

import com.beerlot.domain.auth.security.oauth.entity.OAuthUserPrincipal;
import com.beerlot.domain.auth.security.oauth.entity.ProviderType;
import com.beerlot.domain.member.Member;
import com.beerlot.domain.member.service.MemberService;
import com.beerlot.exception.ConflictException;
import com.beerlot.exception.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class OAuthService extends DefaultOAuth2UserService implements UserDetailsService {

    private final MemberService memberService;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        // 1: Get provider id from UserRequest
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 2: Get oauth user information attributes
        OAuth2User oAuth2User = super.loadUser(userRequest);
        Map<String, Object> attributes = oAuth2User.getAttributes();
        String nameAttributeKey = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        // 3: Get member profile out of attributes
        OAuthUserPrincipal oAuthUserPrincipal = ProviderType.getOAuthUser(registrationId, attributes, nameAttributeKey);

        // 4: Check if registered user
        try {
            Member member = memberService.findMemberByOauthId(oAuthUserPrincipal.getId());
            if (!member.isActive()) {
                // if member exited before, change member status to active
                memberService.updateStatusToActive(member);
            }

            memberService.updateEmail(member, oAuthUserPrincipal.getEmail());
            return OAuthUserPrincipal.of(member);
        } catch (NoSuchElementException e) {
            // if email exists, a user already signed up with other provider
            Optional<Member> maybeMemberByEmail = memberService.findMemberByEmail(oAuthUserPrincipal.getEmail());
            if (maybeMemberByEmail.isPresent()) {
                throw new ConflictException(ErrorMessage.MEMBER__ALREADY_SIGNED_UP.getMessage());
            }

            oAuthUserPrincipal.setUsername("USER_" + System.nanoTime());

            memberService.createMember(oAuthUserPrincipal);
            return oAuthUserPrincipal;
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberService.findMemberByEmail(email)
                .orElseThrow(() -> new NoSuchElementException(ErrorMessage.MEMBER__NOT_EXIST.getMessage()));
        return OAuthUserPrincipal.of(member);
    }
}
