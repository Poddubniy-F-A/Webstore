package com.webstore.config;

import com.webstore.model.Role;
import com.webstore.security.MyUserDetails;
import com.webstore.utils.EndpointsURLs;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;

import static com.webstore.model.Role.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Configuration
    public static class MyConfigurationAdapter {

        private final EndpointsURLs endpointsURLs;
        private final Map<Role, HashSet<String>> rolesResourcesRootsURLs;
        private final Map<Role, String> rolesDefaultURLs;

        public MyConfigurationAdapter(EndpointsURLs endpointsURLs) {
            this.endpointsURLs = endpointsURLs;
            rolesResourcesRootsURLs = Map.of(
                    CUST, new HashSet<>(Arrays.asList(endpointsURLs.CART_MAIN, endpointsURLs.FEEDBACKS_MAIN)),
                    MOD, new HashSet<>(Collections.singletonList(endpointsURLs.MANAGEMENT_MAIN)),
                    WW, new HashSet<>(Collections.singletonList(endpointsURLs.WAREHOUSE_MAIN))
            );
            rolesDefaultURLs = Map.of(
                    CUST, endpointsURLs.CATALOG_MAIN,
                    MOD, endpointsURLs.MANAGEMENT_MAIN,
                    WW, endpointsURLs.WAREHOUSE_MAIN
            );
        }

        @Bean
        SecurityFilterChain myFilterChain(HttpSecurity http) throws Exception {
            http
                    .authorizeHttpRequests(authorize -> {
                                rolesResourcesRootsURLs.forEach((role, resourcesRoots) ->
                                        resourcesRoots.forEach(resourceRoot ->
                                                authorize
                                                        .requestMatchers(resourceRoot + "/**")
                                                        .hasRole(role.toString())
                                        )
                                );
                                authorize.anyRequest().permitAll();
                            }
                    )
                    .formLogin(login -> login
                            .loginPage(endpointsURLs.AUTH_MAIN)
                            .permitAll()
                            .loginProcessingUrl(endpointsURLs.AUTH_LOGIN)
                            .successHandler(new AuthenticationSuccessHandler() {
                                final RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();
                                final RequestCache requestCache = new HttpSessionRequestCache();

                                @Override
                                public void onAuthenticationSuccess(
                                        HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication
                                ) throws IOException {
                                    SavedRequest savedRequest = requestCache.getRequest(request, response);
                                    if (savedRequest != null) {
                                        String targetURL = savedRequest.getRedirectUrl();
                                        String targetURLPath = URI.create(targetURL).getPath();
                                        if (rolesResourcesRootsURLs.values().stream().anyMatch(
                                                urls -> urls.stream().anyMatch(targetURLPath::startsWith)
                                        )) {
                                            clearAuthenticationAttributes(request);
                                            redirectStrategy.sendRedirect(request, response, targetURL);
                                        } else {
                                            requestCache.removeRequest(request, response);
                                            sendDefaultRedirect(request, response, authentication);
                                            clearAuthenticationAttributes(request);
                                        }
                                    } else {
                                        sendDefaultRedirect(request, response, authentication);
                                        clearAuthenticationAttributes(request);
                                    }
                                }

                                private void clearAuthenticationAttributes(HttpServletRequest request) {
                                    HttpSession session = request.getSession(false);
                                    if (session != null) {
                                        session.removeAttribute("SPRING_SECURITY_LAST_EXCEPTION");
                                    }
                                }

                                private void sendDefaultRedirect(
                                        HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication
                                ) throws IOException {
                                    if (!response.isCommitted()) {
                                        redirectStrategy.sendRedirect(request, response, rolesDefaultURLs.get(
                                                ((MyUserDetails) authentication.getPrincipal()).getUser().getRole()
                                        ));
                                    }
                                }
                            })
                            .failureUrl(endpointsURLs.AUTH_FAILURE)
                    )
                    .logout(logout -> logout
                            .logoutSuccessUrl(endpointsURLs.MAIN)
                    )
                    .exceptionHandling(handling -> handling
                            .accessDeniedPage(endpointsURLs.ACCESS_DENIED)
                    )
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }
    }
}
