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

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Configuration
    @Order(1)
    public static class CustomerConfigurationAdapter {
        @Bean
        SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
            http
                    .securityMatcher("/logout", "/cust_auth/**", "/catalog/**", "/customer/**")
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers("/customer/**").hasRole("CUSTOMER")
                            .anyRequest().permitAll())
                    .formLogin(login -> login
                            .loginPage("/cust_auth/enter")
                            .permitAll()
                            .loginProcessingUrl("/cust_auth/process_login")
                            .defaultSuccessUrl("/catalog")
                            .failureUrl("/cust_auth/enter?error=true"))
                    .logout(logout -> logout
                            .logoutSuccessUrl("/index"))
                    .exceptionHandling(handling -> handling
                            .accessDeniedPage("/cat_access_denied"))
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }
    }

    @Configuration
    @Order(2)
    public static class ModeratorConfigurationAdapter {
        @Value("${app.endpoints.management.main}")
        private String managementRoot;

        @Bean
        SecurityFilterChain moderatorFilterChain(HttpSecurity http) throws Exception {
            http
                    .securityMatcher("/mod_auth/**", managementRoot + "/**")
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers(managementRoot + "/**").hasRole("MODERATOR")
                            .anyRequest().permitAll()) // или другие роли
                    .formLogin(login -> login
                            .loginPage("/mod_auth")
                            .permitAll()
                            .loginProcessingUrl("/mod_auth/process_login")
                            .defaultSuccessUrl(managementRoot)
                            .failureUrl("/mod_auth?error=true"))
                    .exceptionHandling(handling -> handling
                            .accessDeniedPage("/man_access_denied"))
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }
    }

    @Configuration
    @Order(3)
    public static class wwConfigurationAdapter {
        @Value("${app.endpoints.warehouse.main}")
        private String warehouseRoot;

        @Bean
        SecurityFilterChain wwFilterChain(HttpSecurity http) throws Exception {
            http
                    .securityMatcher("/ww_auth/**", warehouseRoot + "/**")
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers(warehouseRoot + "/**").hasRole("WH_WORKER")
                            .anyRequest().permitAll()) // или другие роли
                    .formLogin(login -> login
                            .loginPage("/ww_auth")
                            .permitAll()
                            .loginProcessingUrl("/ww_auth/process_login")
                            .defaultSuccessUrl(warehouseRoot)
                            .failureUrl("/ww_auth?error=true"))
                    .exceptionHandling(handling -> handling
                            .accessDeniedPage("/wh_access_denied"))
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }
    }
}
