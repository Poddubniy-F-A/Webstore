package com.webstore.config;

import com.webstore.utils.EndpointsURLs;
import lombok.RequiredArgsConstructor;
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
    @Order(1)
    @RequiredArgsConstructor
    public static class ShopConfigurationAdapter {

        private final EndpointsURLs endpointsURLs;

        @Bean
        SecurityFilterChain shopFilterChain(HttpSecurity http) throws Exception {
            http
                    .securityMatcher(
                            endpointsURLs.AUTH_CUSTOMER_MAIN + "/**",
                            endpointsURLs.CATALOG_MAIN + "/**",
                            endpointsURLs.CART_MAIN + "/**",
                            endpointsURLs.FEEDBACKS_MAIN + "/**",
                            endpointsURLs.AUTH_LOGOUT
                    )
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers(
                                    endpointsURLs.CART_MAIN + "/**",
                                    endpointsURLs.FEEDBACKS_MAIN + "/**"
                            ).hasRole("CUSTOMER")
                            .anyRequest().permitAll())
                    .formLogin(login -> login
                            .loginPage(endpointsURLs.AUTH_CUSTOMER_MAIN)
                            .permitAll()
                            .loginProcessingUrl(endpointsURLs.AUTH_CUSTOMER_LOGIN)
                            .defaultSuccessUrl(endpointsURLs.CATALOG_MAIN)
                            .failureUrl(endpointsURLs.AUTH_CUSTOMER_FAILURE))
                    .logout(logout -> logout
                            .logoutSuccessUrl(endpointsURLs.MAIN))
                    .exceptionHandling(handling -> handling
                            .accessDeniedPage(endpointsURLs.ERRORS_ACCESS_DENIED_CUST_SERVICE))
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }
    }

    @Configuration
    @Order(2)
    @RequiredArgsConstructor
    public static class ModeratorConfigurationAdapter {

        private final EndpointsURLs endpointsURLs;

        @Bean
        SecurityFilterChain moderatorFilterChain(HttpSecurity http) throws Exception {
            http
                    .securityMatcher(
                            endpointsURLs.AUTH_MODERATOR_MAIN + "/**",
                            endpointsURLs.MANAGEMENT_MAIN + "/**"
                    )
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers(endpointsURLs.MANAGEMENT_MAIN + "/**").hasRole("MODERATOR")
                            .anyRequest().permitAll())
                    .formLogin(login -> login
                            .loginPage(endpointsURLs.AUTH_MODERATOR_MAIN)
                            .permitAll()
                            .loginProcessingUrl(endpointsURLs.AUTH_MODERATOR_LOGIN)
                            .defaultSuccessUrl(endpointsURLs.MANAGEMENT_MAIN)
                            .failureUrl(endpointsURLs.AUTH_MODERATOR_FAILURE))
                    .exceptionHandling(handling -> handling
                            .accessDeniedPage(endpointsURLs.ERRORS_ACCESS_DENIED_MANAGEMENT))
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }
    }

    @Configuration
    @Order(3)
    @RequiredArgsConstructor
    public static class WWConfigurationAdapter {

        private final EndpointsURLs endpointsURLs;

        @Bean
        SecurityFilterChain wwFilterChain(HttpSecurity http) throws Exception {
            http
                    .securityMatcher(
                            endpointsURLs.AUTH_WH_WORKER_MAIN + "/**",
                            endpointsURLs.WAREHOUSE_MAIN + "/**"
                    )
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers(endpointsURLs.WAREHOUSE_MAIN + "/**").hasRole("WH_WORKER")
                            .anyRequest().permitAll())
                    .formLogin(login -> login
                            .loginPage(endpointsURLs.AUTH_WH_WORKER_MAIN)
                            .permitAll()
                            .loginProcessingUrl(endpointsURLs.AUTH_WH_WORKER_LOGIN)
                            .defaultSuccessUrl(endpointsURLs.WAREHOUSE_MAIN)
                            .failureUrl(endpointsURLs.AUTH_WH_WORKER_FAILURE))
                    .exceptionHandling(handling -> handling
                            .accessDeniedPage(endpointsURLs.ERRORS_ACCESS_DENIED_WAREHOUSE))
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }
    }
}
