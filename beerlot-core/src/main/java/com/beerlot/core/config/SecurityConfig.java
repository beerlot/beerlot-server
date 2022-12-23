package com.beerlot.core.config;

import com.beerlot.core.domain.auth.security.oauth.filter.TokenAuthenticationEntryPoint;
import com.beerlot.core.domain.auth.security.oauth.handler.OAuthAuthenticationFailureHandler;
import com.beerlot.core.domain.auth.security.oauth.handler.OAuthAuthenticationSuccessHandler;
import com.beerlot.core.domain.auth.security.oauth.filter.OAuthAuthenticationFilter;
import com.beerlot.core.domain.auth.security.oauth.repository.OAuthAuthorizationRequestCookieRepository;
import com.beerlot.core.domain.auth.security.oauth.service.OAuthService;
import com.beerlot.core.domain.member.RoleType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.CorsFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final OAuthService oAuthService;
    private final OAuthAuthenticationFilter oAuthAuthenticationFilter;
    private final OAuthAuthorizationRequestCookieRepository authorizationRequestRepository;
    private final OAuthAuthenticationSuccessHandler authenticationSuccessHandler;
    private final OAuthAuthenticationFailureHandler authenticationFailureHandler;
    private final TokenAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http    .addFilterBefore(oAuthAuthenticationFilter, CorsFilter.class);

        http
                .csrf().disable()
                .cors()
                .and()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .formLogin().disable();
        http
                .oauth2Login()
                .authorizationEndpoint()
                    .baseUri("/api/v1/auth/authorization")
                    .authorizationRequestRepository(authorizationRequestRepository)
                .and()
                .redirectionEndpoint()
                    .baseUri("/login/oauth2/code/*")
                .and()
                .userInfoEndpoint()
                    .userService(oAuthService)
                .and()
                .successHandler(authenticationSuccessHandler)
                .failureHandler(authenticationFailureHandler)
                .and()
                .exceptionHandling()
                    .authenticationEntryPoint(authenticationEntryPoint);
                //.and()
                //.redirectionEndpoint()
                    //.baseUri("/api/v1/auth/callback/*");

        http
                .authorizeRequests()
                    .antMatchers(HttpMethod.POST, "/api/v1/members/signup").hasAnyAuthority(String.valueOf(RoleType.ROLE_GUEST))
                    .antMatchers(HttpMethod.GET, "/api/v1/**/beers/**").permitAll()
                    .anyRequest().authenticated();
                //.and()
                //.oauth2ResourceServer().jwt()


        return http.build();
    }

}
