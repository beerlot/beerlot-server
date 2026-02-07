package com.beerlot.domain.member;

import com.beerlot.domain.auth.security.oauth.entity.ProviderType;
import com.beerlot.domain.common.entity.CreateAndUpdateDateTime;
import com.beerlot.domain.member.dto.request.MemberProfileRequest;
import com.beerlot.domain.policy.PolicyType;
import com.beerlot.domain.policy.PolicyTypeConverter;
import com.beerlot.domain.review.Review;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@Entity
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Getter
@Table(name = "member")
public class Member extends CreateAndUpdateDateTime {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "oauth_id", nullable = false, unique = true)
    private String oauthId;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "username_updated_at")
    private OffsetDateTime usernameUpdatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private ProviderType provider;

    @Convert(converter = RoleTypeConverter.class)
    @Column(name = "roles", nullable = false)
    private Set<RoleType> roles;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @ColumnDefault(value = "'ACTIVE'")
    private MemberStatus status;

    @Convert(converter = PolicyTypeConverter.class)
    @Column(name = "agreed_policies")
    private Set<PolicyType> agreedPolicies;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "status_message")
    private String statusMessage;

    @OneToMany(mappedBy = "member")
    private List<Review> reviews = new ArrayList<>();

    @Builder
    public Member(String oauthId, String email, String username, ProviderType provider, Set<RoleType> roles, String imageUrl,
                  String statusMessage, OffsetDateTime createdAt, OffsetDateTime updatedAt, OffsetDateTime usernameUpdatedAt) {
        this.oauthId = oauthId;
        this.email = email;
        this.username = username;
        this.provider = provider;
        this.roles = roles;
        this.imageUrl = imageUrl;
        this.statusMessage = statusMessage;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.usernameUpdatedAt = usernameUpdatedAt;
        this.status = MemberStatus.ACTIVE;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateAgreedPolicies(Set<PolicyType> agreedPolicies) {
        this.agreedPolicies = agreedPolicies;
    }

    public void updateProfile(MemberProfileRequest memberProfileRequest) {
        this.statusMessage = memberProfileRequest.getStatusMessage();
        this.imageUrl = memberProfileRequest.getImageUrl();
    }

    public void updateUsername(String username) {
        this.username = username;
    }

    public void addRole(RoleType role) {
        roles.add(role);
    }

    public void setUsernameUpdatedAtToNow() {
        this.usernameUpdatedAt = OffsetDateTime.now();
    }

    public void active() {
        this.status = MemberStatus.ACTIVE;
    }

    public void exit() {
        this.status = MemberStatus.IN_ACTIVE;
    }

    public boolean isActive() {
        return this.status == MemberStatus.ACTIVE;
    }
}

