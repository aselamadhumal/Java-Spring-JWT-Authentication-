package com.jwtauth.jwtauth.config;

import com.jwtauth.jwtauth.filter.JWTFilter;
import com.jwtauth.jwtauth.repository.UserRepository;
import com.jwtauth.jwtauth.service.MyUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    private final UserRepository userRepository;
    private final JWTFilter jwtFilter;

    public SecurityConfig(UserRepository userRepository, JWTFilter jwtFilter) {
        this.userRepository = userRepository;
        this.jwtFilter = jwtFilter;
        logger.info("SecurityConfig initialized with UserRepository and JWTFilter");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        logger.info("Configuring security filter chain...");

        return httpSecurity
                .csrf(AbstractHttpConfigurer::disable) // Disable CSRF for APIs
                .sessionManagement(session -> {
                    logger.info("Setting session management to STATELESS");
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                }) // Stateless session
                .authorizeHttpRequests(auth -> {
                    logger.info("Configuring endpoint access rules...");
                    auth.requestMatchers("/api/v1/auth/login", "/api/v1/auth/register","/api/v1/auth/refresh","/api/v1/auth/logout","api/transactions/process","api/transactions/enc","api/transactions/dec").permitAll();
                    logger.info("Public endpoints allowed: /api/v1/auth/login, /api/v1/auth/register");
                    auth.anyRequest().authenticated();
                })
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .authenticationProvider(authenticationProvider())
                //.httpBasic(Customizer.withDefaults())
                .build();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        logger.info("Creating DaoAuthenticationProvider...");
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        logger.info("DaoAuthenticationProvider initialized with custom UserDetailsService and PasswordEncoder");
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("Initializing BCryptPasswordEncoder with strength 12");
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        logger.info("Initializing custom UserDetailsService...");
        return new MyUserDetailsService(userRepository);
    }


    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configureration) throws Exception {
        return configureration.getAuthenticationManager();

    }


}
