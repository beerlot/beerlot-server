package com.beerlot.domain.auth.security.oauth.handler;

import com.beerlot.config.AppConfig;
import com.beerlot.domain.auth.security.jwt.dto.TokenResponse;
import com.beerlot.domain.auth.security.jwt.entity.RefreshToken;
import com.beerlot.domain.auth.security.jwt.service.TokenService;
import com.beerlot.domain.auth.security.oauth.entity.OAuthUserPrincipal;
import com.beerlot.domain.auth.security.oauth.repository.OAuthAuthorizationRequestCookieRepository;
import com.beerlot.domain.auth.util.CookieUtils;
import com.beerlot.domain.member.RoleType;
import com.beerlot.exception.ErrorMessage;
import com.beerlot.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.Optional;

@RequiredArgsConstructor
@Component
@Slf4j
public class OAuthAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    private final OAuthAuthorizationRequestCookieRepository authorizationRequestRepository;
    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
    private final AppConfig appConfig;
    private final TokenService tokenService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.debug("Did not redirect to %s since response already committed." + targetUrl);
            return;
        }
        //clearAuthenticationAttributes(request, response);
        redirectStrategy.sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        Optional<String> redirectUrl = CookieUtils.getCookie(request, CookieUtils.COOKIE_NAME_REDIRECT_URL)
                .map(Cookie::getValue);

        if (redirectUrl.isPresent() && !isAuthorized(redirectUrl.get())) {
            throw new UnauthorizedException(ErrorMessage.UNAUTHORIZED_REDIRECT_URI.getMessage());
        }

        String targetUrl = redirectUrl.orElse(getDefaultTargetUrl());

        OAuthUserPrincipal userPrincipal = (OAuthUserPrincipal) authentication.getPrincipal();

        TokenResponse tokenResponse = tokenService.generateToken(authentication);

        Optional<RefreshToken> refreshTokenOptional = tokenService.findRefreshTokenByOauthId(userPrincipal.getId());

        Boolean isSignedUp = null;

        if (userPrincipal.getRoles().contains(RoleType.MEMBER)) {
            refreshTokenOptional.get().updateRefreshToken(tokenResponse.getRefreshToken());
            isSignedUp = true;
        } else {
            tokenService.createRefreshToken(new RefreshToken(userPrincipal.getId(), tokenResponse.getRefreshToken()));
            isSignedUp = false;
        }

        return UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("access_token", tokenResponse.getAccessToken())
                .queryParam("refresh_token", tokenResponse.getRefreshToken())
                .queryParam("is_signed_up", isSignedUp)
                .build().toUriString();
    }

    private void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        authorizationRequestRepository.removeAuthorizationRequest(request, response);
    }

    private boolean isAuthorized(String url) {
        URI clientRedirectUri = URI.create(url);
        URI authorizedRedirectedUrl = URI.create(appConfig.getRedirectUrl());

        boolean checkAuthorization = authorizedRedirectedUrl.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                && authorizedRedirectedUrl.getPort() == clientRedirectUri.getPort();

        if (!checkAuthorization) {
            log.error("client redirect URL is not authorized, client Redirect URL : {}, authorized : {}", url, appConfig.getRedirectUrl());
        }

        return checkAuthorization;
    }
}