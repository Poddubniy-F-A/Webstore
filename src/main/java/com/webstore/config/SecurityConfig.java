package com.webstore.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@Configuration
@EnableWebSecurity
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
    @Order(1)
    public static class CustomerConfigurationAdapter {
        @Value("${app.endpoints.catalog.main}")
        private String catalogRootUrl;
        @Value("${app.endpoints.cart.main}")
        private String cartRootUrl;
        @Value("${app.endpoints.feedbacks.main}")
        private String feedbacksRootUrl;

        @Value("${app.endpoints.auth.customer.main}")
        private String authUrl;
        @Value("${app.endpoints.auth.customer.login}")
        private String loginUrl;

        @Value("${app.endpoints.auth.logout}")
        private String logoutUrl;
        @Value("${app.endpoints.main}")
        private String logoutSuccessUrl;

        @Value("${app.endpoints.errors.access_denied.cust_service}")
        private String accessDeniedUrl;

        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .securityMatcher(
                            authUrl + "/**",
                            catalogRootUrl + "/**",
                            cartRootUrl + "/**",
                            feedbacksRootUrl + "/**",
                            logoutUrl
                    )
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers(cartRootUrl + "/**", feedbacksRootUrl + "/**").hasRole("CUSTOMER")
                            .anyRequest().permitAll())
                    .formLogin(login -> login
                            .loginPage(authUrl)
                            .permitAll()
                            .loginProcessingUrl(loginUrl)
                            .defaultSuccessUrl(catalogRootUrl)
                            .failureUrl(authUrl + "?error=true"))
                    .logout(logout -> logout
                            .logoutSuccessUrl(logoutSuccessUrl))
                    .exceptionHandling(handling -> handling
                            .accessDeniedPage(accessDeniedUrl))
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }
    }

    @Configuration
    @Order(2)
    public static class ModeratorConfigurationAdapter {
        @Value("${app.endpoints.management.main}")
        private String rootUrl;

        @Value("${app.endpoints.auth.moderator.main}")
        private String authUrl;
        @Value("${app.endpoints.auth.moderator.login}")
        private String loginUrl;

        @Value("${app.endpoints.errors.access_denied.management}")
        private String accessDeniedUrl;

        @Bean
        SecurityFilterChain moderatorFilterChain(HttpSecurity http) throws Exception {
            http
                    .securityMatcher(authUrl + "/**", rootUrl + "/**")
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers(rootUrl + "/**").hasRole("MODERATOR")
                            .anyRequest().permitAll())
                    .formLogin(login -> login
                            .loginPage(authUrl)
                            .permitAll()
                            .loginProcessingUrl(loginUrl)
                            .defaultSuccessUrl(rootUrl)
                            .failureUrl(authUrl + "?error=true"))
                    .exceptionHandling(handling -> handling
                            .accessDeniedPage(accessDeniedUrl))
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }
    }

    @Configuration
    @Order(3)
    public static class wwConfigurationAdapter {
        @Value("${app.endpoints.warehouse.main}")
        private String rootUrl;

        @Value("${app.endpoints.auth.wh_worker.main}")
        private String authUrl;
        @Value("${app.endpoints.auth.wh_worker.login}")
        private String loginUrl;

        @Value("${app.endpoints.errors.access_denied.warehouse}")
        private String accessDeniedUrl;

        @Bean
        SecurityFilterChain wwFilterChain(HttpSecurity http) throws Exception {
            http
                    .securityMatcher(authUrl + "/**", rootUrl + "/**")
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers(rootUrl + "/**").hasRole("WH_WORKER")
                            .anyRequest().permitAll())
                    .formLogin(login -> login
                            .loginPage(authUrl)
                            .permitAll()
                            .loginProcessingUrl(loginUrl)
                            .defaultSuccessUrl(rootUrl)
                            .failureUrl(authUrl + "?error=true"))
                    .exceptionHandling(handling -> handling
                            .accessDeniedPage(accessDeniedUrl))
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }
    }
}
