package com.jwtauth.jwtauth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jwtauth.jwtauth.dto.DefaultResponse;
import com.jwtauth.jwtauth.entity.users.UserEntity;
import com.jwtauth.jwtauth.repository.UserRepository;
import com.jwtauth.jwtauth.service.JWTService;
import com.jwtauth.jwtauth.utils.ResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
public class JWTFilter extends OncePerRequestFilter {

    private static final String MDC_UID_KEY = "userId";
    private final JWTService jwtService;
    private final UserRepository userRepository;

    public JWTFilter(JWTService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            log.info("Entering JWTFilter:doFilterInternal for request URI: {}", request.getRequestURI());

            boolean refreshToken = skipRefreshToken(request);

            if (refreshToken) {
                log.info("Skipping JWT filter for refresh token path: {}", request.getServletPath());
                filterChain.doFilter(request, response);
                return;
            }

            String header = request.getHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                log.info("No JWT token found in Authorization header");
                filterChain.doFilter(request, response);
                return;
            }

            String token = header.substring(7);
            log.info("Extracted JWT token from Authorization header");

            String username = jwtService.extractUsername(token);
            log.info("Extracted username from token: {}", username);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                Optional<UserEntity> userDetails = userRepository.findByUsername(username);

                if (userDetails.isPresent()) {
                    log.info("User found in database: {}", username);

                    if (jwtService.isTokenValid(token, String.valueOf(userDetails.get()))) {
                        log.info("JWT token is valid for user: {}", username);

                        UsernamePasswordAuthenticationToken authentication = getAuthentication(request, userDetails.get());
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        MDC.put(MDC_UID_KEY, username);
                        log.info("Security context set with authentication for user: {}", username);
                    } else {
                        log.warn("JWT token is not valid for user: {}", username);
                    }
                } else {
                    log.warn("User not found in database: {}", username);
                }
            }

            filterChain.doFilter(request, response);
        } catch (SignatureException e) {
            log.error("JWTFilter:[doFilterInternal] -> JWT signature verification failed", e);
            DefaultResponse defaultResponse = DefaultResponse.builder().code(ResponseUtil.JWT_TOKEN_VALIDATE_ERROR_CODE).title(ResponseUtil.FAILED).message(ResponseUtil.INVALID_CREDENTIAL).build();
            generateErrorResponse(response, defaultResponse);
        } catch (ExpiredJwtException e) {
            log.error("JWTFilter:[doFilterInternal] -> JWT expired", e);
            DefaultResponse defaultResponse = DefaultResponse.builder().code(ResponseUtil.JWT_TOKEN_EXPIRED_ERROR_CODE).title(ResponseUtil.FAILED).message(ResponseUtil.INVALID_CREDENTIAL).build();
            generateErrorResponse(response, defaultResponse);
        } catch (Exception e) {
            log.error("JWTFilter:[doFilterInternal] -> General exception occurred: {}", e.getMessage(), e);
            DefaultResponse defaultResponse = DefaultResponse.builder().code(ResponseUtil.JWT_TOKEN_VALIDATE_ERROR_CODE).title(ResponseUtil.FAILED).message(ResponseUtil.INVALID_CREDENTIAL).build();
            generateErrorResponse(response, defaultResponse);
        } finally {
            MDC.remove(MDC_UID_KEY);
            request.removeAttribute("JWTRequestFilter.FILTERED");
            log.info("Exiting JWTFilter:doFilterInternal for request URI: {}", request.getRequestURI());
        }
    }


    public void generateErrorResponse(HttpServletResponse response, DefaultResponse defaultResponse) throws IOException {
        log.info("Generating error response with default unauthorized status");
        generateErrorResponse(response, defaultResponse, HttpServletResponse.SC_UNAUTHORIZED);
    }

    private void generateErrorResponse(HttpServletResponse response, DefaultResponse defaultResponse, int httpStatus) throws IOException {
        log.info("Generating error response with HTTP status: {}", httpStatus);
        PrintWriter writer = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.setStatus(httpStatus);
        writer.print(new ObjectMapper().writeValueAsString(defaultResponse));
        writer.flush();
        log.info("Error response written to output stream");
    }

    private boolean skipRefreshToken(HttpServletRequest httpServletRequest) {
        String[] regs = {"/user/refresh-token"};
        for (String pathExpr : regs) {
            Matcher matcher = Pattern.compile(pathExpr).matcher(httpServletRequest.getServletPath());
            if (matcher.find()) {
                log.info("Request matched refresh token path: {}", httpServletRequest.getServletPath());
                return true;
            }
        }
        log.info("Request did not match any refresh token paths: {}", httpServletRequest.getServletPath());
        return false;
    }

    private UsernamePasswordAuthenticationToken getAuthentication(
            HttpServletRequest request,
            UserEntity userData) {

        String logPrefix = "getAuthentication -> ";

        try {
            log.info("{}Creating authentication token for user: {}", logPrefix, userData.getUsername());

            UserDetails userDetails = User.builder()
                    .username(userData.getUsername())
                    .password(userData.getPassword())
                    .build();

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

            authenticationToken.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

            request.setAttribute("user", userData);

            log.info("{}Authentication token created and set in request", logPrefix);
            return authenticationToken;

        } catch (Exception e) {
            log.error("{}Error creating authentication token", logPrefix, e);
            return null;
        }
    }

}