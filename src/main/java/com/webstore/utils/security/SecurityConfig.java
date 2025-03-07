package com.webstore.utils.security;

import com.webstore.entities.User;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    public static User userFromContext() {
        return ((MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUser();
    }

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
        @Bean
        SecurityFilterChain moderatorFilterChain(HttpSecurity http) throws Exception {
            http
                    .securityMatcher("/mod_auth/**", "/management/**")
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers("/management/**").hasRole("MODERATOR")
                            .anyRequest().permitAll()) // или другие роли
                    .formLogin(login -> login
                            .loginPage("/mod_auth")
                            .permitAll()
                            .loginProcessingUrl("/mod_auth/process_login")
                            .defaultSuccessUrl("/management")
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
        @Bean
        SecurityFilterChain wwFilterChain(HttpSecurity http) throws Exception {
            http
                    .securityMatcher("/ww_auth/**", "/warehouse/**")
                    .authorizeHttpRequests(authorize -> authorize
                            .requestMatchers("/warehouse/**").hasRole("WH_WORKER")
                            .anyRequest().permitAll()) // или другие роли
                    .formLogin(login -> login
                            .loginPage("/ww_auth")
                            .permitAll()
                            .loginProcessingUrl("/ww_auth/process_login")
                            .defaultSuccessUrl("/warehouse")
                            .failureUrl("/ww_auth?error=true"))
                    .exceptionHandling(handling -> handling
                            .accessDeniedPage("/wh_access_denied"))
                    .csrf(AbstractHttpConfigurer::disable);
            return http.build();
        }
    }
}
