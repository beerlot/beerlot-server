package com.beerlot.config;

import com.beerlot.domain.auth.security.oauth.filter.ExceptionHandlerFilter;
import com.beerlot.domain.auth.security.oauth.filter.OAuthAuthenticationFilter;
import com.beerlot.domain.auth.security.oauth.filter.TokenAuthenticationEntryPoint;
import com.beerlot.domain.auth.security.oauth.handler.OAuthAuthenticationFailureHandler;
import com.beerlot.domain.auth.security.oauth.handler.OAuthAuthenticationSuccessHandler;
import com.beerlot.domain.auth.security.oauth.repository.OAuthAuthorizationRequestCookieRepository;
import com.beerlot.domain.auth.security.oauth.service.OAuthService;
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
    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final OAuthAuthorizationRequestCookieRepository authorizationRequestRepository;
    private final OAuthAuthenticationSuccessHandler authenticationSuccessHandler;
    private final OAuthAuthenticationFailureHandler authenticationFailureHandler;
    private final TokenAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http    .addFilterBefore(oAuthAuthenticationFilter, CorsFilter.class)
                .addFilterBefore(exceptionHandlerFilter, OAuthAuthenticationFilter.class);

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
                    .baseUri("/api/v1/auth/authorize")
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

        http
                .authorizeRequests()
                    .antMatchers("swagger/**","/swagger-ui/**","/swagger-ui.html","/webjars/**","/swagger-resources/**","/configuration/**","/v3/api-docs/**", "/docs").permitAll()

                    // ===== Permit All ===== //
                    .antMatchers(HttpMethod.POST, "/api/v1/members/check-username").permitAll()

                    // ===== Permit Role Guest ===== //
                    .antMatchers(HttpMethod.PATCH, "/api/v1/auth/signup").hasRole("GUEST")
                    .antMatchers("/api/v1/files/profile").hasRole("GUEST")

                    // ===== Permit Role Member ===== //
                    .antMatchers( "/api/v1/beers/**/likes").hasRole("MEMBER")
                    .antMatchers( "/api/v1/beers/recommend").hasRole("MEMBER")
                    .antMatchers("/api/v1/beers/**/reviews/me").hasRole("MEMBER")
                    .antMatchers("/api/v1/reviews/**/likes").hasRole("MEMBER")
                    .antMatchers(HttpMethod.POST, "/api/v1/reviews/**").hasRole("MEMBER")
                    .antMatchers(HttpMethod.PATCH, "/api/v1/reviews/**").hasRole("MEMBER")
                    .antMatchers(HttpMethod.DELETE, "/api/v1/reviews/**").hasRole("MEMBER")
                    .antMatchers("/api/v1/members/**").hasRole("MEMBER")
                    .antMatchers("/api/v1/files/**").hasRole("MEMBER")

                    // ===== Permit All ===== //
                    .antMatchers(HttpMethod.GET, "/api/v1/policies/**").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/v1/beers/**", "/api/v1/reviews/**").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/v1/category/**", "/api/v1/glassware/**").permitAll()
                    .antMatchers(HttpMethod.GET, "/api/v1/breweries/**").permitAll()
                    .antMatchers(HttpMethod.POST, "/api/v1/members/status").permitAll()

                    .anyRequest().authenticated();


        return http.build();
    }
}
