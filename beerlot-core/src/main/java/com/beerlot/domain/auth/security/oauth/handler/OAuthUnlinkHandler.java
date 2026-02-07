package com.beerlot.domain.auth.security.oauth.handler;

import com.beerlot.domain.auth.security.oauth.entity.ProviderType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuthUnlinkHandler {

    @Value("${spring.security.oauth2.client.registration.naver.client-id}")
    private String naverClientId;

    @Value("${spring.security.oauth2.client.registration.naver.client-secret}")
    private String naverClientSecret;

    private final RestTemplate restTemplate;

    public boolean unlinkProvider(ProviderType type, String accessToken) {
        if (type.equals(ProviderType.KAKAO)) {
            return unlinkKakao(accessToken);
        } else if (type.equals(ProviderType.NAVER)) {
            return unlinkNaver(accessToken);
        } else if (type.equals(ProviderType.GOOGLE)) {
            return unlinkGoogle(accessToken);
        } else {
            throw new IllegalArgumentException("Unsupported Provider Type");
        }
    }

    private boolean unlinkKakao(String accessToken) {
        String kakaoUnlinkUrl = "https://kapi.kakao.com/v1/user/unlink";

        HttpHeaders headers = new HttpHeaders();
        headers.put(HttpHeaders.CONTENT_TYPE, List.of(MediaType.APPLICATION_FORM_URLENCODED_VALUE));
        headers.put(HttpHeaders.AUTHORIZATION, List.of("Bearer " + accessToken));

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.postForEntity(kakaoUnlinkUrl, httpEntity, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Failed to unlink kakao : {}", response);
        }

        return true;
    }


    private boolean unlinkNaver(String accessToken) {
        String naverUnlinkUrl = "https://nid.naver.com/oauth2.0/token";

        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString(naverUnlinkUrl)
                .queryParam("grant_type", "delete")
                .queryParam("client_id", naverClientId)
                .queryParam("client_secret", naverClientSecret)
                .queryParam("access_token", accessToken)
                .queryParam("service_provider", "NAVER")
                .build();

        ResponseEntity<String> response = restTemplate.getForEntity(uriComponents.toUri(), String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Failed to unlink naver : {}", response);
        }

        return true;
    }

    private boolean unlinkGoogle(String accessToken) {
        String googleUnlinkUrl = "https://accounts.google.com/o/oauth2/revoke";

        UriComponents uriComponents = UriComponentsBuilder
                .fromUriString(googleUnlinkUrl)
                .queryParam("token", accessToken)
                .build();

        ResponseEntity<String> response = restTemplate.getForEntity(uriComponents.toUri(), String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Failed to unlink google : {}", response);
        }

        return true;
    }

}
