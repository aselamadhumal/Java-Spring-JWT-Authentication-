package com.jwtauth.jwtauth.filter;

import com.jwtauth.jwtauth.model.UserEntity;
import com.jwtauth.jwtauth.repository.UserRepository;
import com.jwtauth.jwtauth.service.JWTService;
import jakarta.persistence.NamedEntityGraph;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
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
public class JWTFilter extends OncePerRequestFilter{

    private final JWTService jwtService;
    private final UserRepository userRepository;

    public JWTFilter(JWTService jwtService, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
    }


    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,@NonNull FilterChain filterChain) throws ServletException, IOException {


        String authorization = request.getHeader("Authorization");

        if(authorization == null || !authorization.startsWith("Bearer ")) {
            logger.warn("Authorization header is missing or does not start with Bearer");
            filterChain.doFilter(request, response);
            return;
        }

        String jwtToken = authorization.split("Bearer ")[1];

        String username = jwtService.getUserName(jwtToken);
        logger.info("User:"+username);
        if(username == null) {
            filterChain.doFilter(request, response);
            return;
        }

        UserEntity userData = userRepository.findByUsername(username).orElse(null);
        logger.info("UserEntity:"+userData.toString());
        logger.info("test:"+UUID.randomUUID());
        if(userData == null) {
            filterChain.doFilter(request, response);
            return;
        }

        //when username is not null

        //when someone is the authorization
        if(SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        /*if (userData == null) {
            filterChain.doFilter(request, response);
            return;  // Ensure the rest of the filter is not processed
        }*/

        //when authorized
        UserDetails userDetails = User.builder()
                .username(userData.getUsername())
                .password(userData.getPassword())
                .build();

        logger.info("successfully authorized:"+userDetails.getUsername());

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        logger.info("successfully authorized2:"+userDetails.getUsername());

        token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        //after making token that token put the context
        SecurityContextHolder.getContext().setAuthentication(token);

        System.out.println(jwtToken);
        filterChain.doFilter(request, response);
        logger.info("successfully authorized3:"+userDetails.getUsername());
    }
}

