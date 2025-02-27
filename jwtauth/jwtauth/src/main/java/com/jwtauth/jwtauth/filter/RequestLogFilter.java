package com.jwtauth.jwtauth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Enumeration;

@Component
public class RequestLogFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestLogFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (isAsyncDispatch(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // Wrap the request and response to cache their content
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            logger.info("Request Headers -> Content-type : {}", request.getContentType());
            logHeaders(request);

            // Log request details before proceeding with the filter chain
            logger.info("Incoming Request: {} {}", wrappedRequest.getMethod(), wrappedRequest.getRequestURI());

            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {

            logRequestBody(wrappedRequest);
            logResponseBody(wrappedResponse);

            // Ensure the response is written back to the client
            wrappedResponse.copyBodyToResponse();

        }
    }

    private void logHeaders(HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            logger.info("Header -> {} : {}", headerName, headerValue);
        }
    }

    @SneakyThrows
    private void logRequestBody(ContentCachingRequestWrapper request) {
        byte[] content = request.getContentAsByteArray();
        if (content.length > 0) {
            String body = new String(content, request.getCharacterEncoding());
            logger.info("Request Body: {}", body);
        }
    }

    @SneakyThrows
    private void logResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            String body = new String(content, response.getCharacterEncoding());
            logger.info("Response Body: {}", body);
        }
    }
}

