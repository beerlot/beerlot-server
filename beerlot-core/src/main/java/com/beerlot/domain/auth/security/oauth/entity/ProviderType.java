package com.beerlot.domain.auth.security.oauth.entity;

import com.beerlot.domain.member.RoleType;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Getter
public enum ProviderType {
    GOOGLE("google", (attributes) -> {
        OAuthUserPrincipal userPrincipal = OAuthUserPrincipal.builder()
                .id(attributes.get("sub").toString())
                .email(attributes.get("email").toString())
                .username(attributes.get("name").toString())
                .imageUrl(attributes.get("picture").toString())
                .provider("google")
                .roles(Set.of(RoleType.GUEST))
                .build();
        return userPrincipal;
    }),

    NAVER("naver", (attributes) -> {
        String email = attributes.get("email").toString();

        OAuthUserPrincipal userPrincipal = OAuthUserPrincipal.builder()
                .id(attributes.get("id").toString())
                .email(email)
                .username(attributes.containsKey("name") ? attributes.get("name").toString() : email.split("@")[0])
                .imageUrl(attributes.get("profile_image").toString())
                .provider("naver")
                .roles(Set.of(RoleType.GUEST))
                .build();
        return userPrincipal;
    }),

    KAKAO("kakao", (attributes) -> {
        Map<String, Object> account = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> profile = (Map<String, Object>) account.get("profile");

        String email = account.get("email").toString();
        String username = account.containsKey("name") ? account.get("name").toString() : email.split("@")[0];
        String imageUrl = profile.containsKey("profile_image_url") ?
                profile.get("profile_image_url").toString() : null;

        OAuthUserPrincipal userPrincipal = OAuthUserPrincipal.builder()
                .id(attributes.get("id").toString())
                .email(email)
                .username(username)
                .imageUrl(imageUrl)
                .provider("kakao")
                .roles(Set.of(RoleType.GUEST))
                .build();
        return userPrincipal;
    });

    private final String registrationId;
    private final Function<Map<String, Object>, OAuthUserPrincipal> oAuthUserPrincipal;

    ProviderType(String registrationId, Function<Map<String, Object>, OAuthUserPrincipal> oAuthUserPrincipal) {
        this.registrationId = registrationId;
        this.oAuthUserPrincipal = oAuthUserPrincipal;
    }

    public static OAuthUserPrincipal getOAuthUser(String registrationId, Map<String, Object> attributes, String nameAttributeKey) {
        if (registrationId.equals("naver")) {
            attributes = (Map<String, Object>) attributes.get(nameAttributeKey);
        }

        return Arrays.stream(values())
                .filter(provider -> registrationId.equals(provider.registrationId))
                .findFirst()
                .orElseThrow(IllegalArgumentException::new)
                .oAuthUserPrincipal.apply(attributes);
    }

}
