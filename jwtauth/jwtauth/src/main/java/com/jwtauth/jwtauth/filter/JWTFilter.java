package com.jwtauth.jwtauth.filter;

import com.jwtauth.jwtauth.model.UserEntity;
import com.jwtauth.jwtauth.repository.UserRepository;
import com.jwtauth.jwtauth.service.JWTService;
import com.jwtauth.jwtauth.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);

    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final TokenBlacklistService tokenBlacklistService;

    public JWTFilter(JWTService jwtService, UserRepository userRepository, TokenBlacklistService tokenBlacklistService) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");

        if (authorization == null || !authorization.startsWith("Bearer ")) {
            logger.warn("Authorization header is missing or does not start with Bearer");
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authorization.substring(7); // Extract token
        logger.info("Extracted JWT Token: {}", jwtToken);

        if (tokenBlacklistService.isTokenBlacklisted(jwtToken)) {
            logger.warn("Token is blacklisted");
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is blacklisted");
            return;
        }

        String username = jwtService.getUserName(jwtToken);
        logger.info("User from JWT: {}", username);

        if (username == null) {
            logger.warn("Username extracted from JWT is null");
            filterChain.doFilter(request, response);
            return;
        }

        UserEntity userData = userRepository.findByUsername(username).orElse(null);
        if (userData == null) {
            logger.warn("User not found in the database: {}", username);
            filterChain.doFilter(request, response);
            return;
        }

        logger.info("UserEntity retrieved: {}", userData);

        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            logger.info("User already authenticated: {}", username);
            filterChain.doFilter(request, response);
            return;
        }

        UserDetails userDetails = User.builder()
                .username(userData.getUsername())
                .password(userData.getPassword())
                .build();

        logger.info("Successfully authorized user: {}", userDetails.getUsername());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(token);
        logger.info("Authentication set in SecurityContext for user: {}", userDetails.getUsername());

        filterChain.doFilter(request, response);
        logger.info("Filter chain continued for user: {}", userDetails.getUsername());
    }
}
