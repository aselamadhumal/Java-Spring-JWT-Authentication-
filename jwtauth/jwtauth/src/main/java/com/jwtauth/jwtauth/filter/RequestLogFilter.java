package com.jwtauth.jwtauth.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
//import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Enumeration;
import ch.qos.logback.classic.Logger;

@Component
public class RequestLogFilter extends OncePerRequestFilter {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(java.util.logging.Logger.GLOBAL_LOGGER_NAME);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Wrap the request to read the body multiple times
        BufferRequestWrapper wrappedRequest = new BufferRequestWrapper(request);

        // Wrap the response to cache the response body
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);

        try {
            // Log incoming request details
            logger.info("Incoming Request: {} {}", wrappedRequest.getMethod(), wrappedRequest.getRequestURI());
            logHeaders(wrappedRequest);

            // Log request body only if the content type is appropriate
            if (shouldLogRequestBody(wrappedRequest)) {
                logRequestBody(wrappedRequest);
            }

            // Proceed with the filter chain using the wrapped request and response
            filterChain.doFilter(wrappedRequest, wrappedResponse);

        } finally {
            // Log response details
            logResponseBody(wrappedResponse);

            // Copy the response content to the original response
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

    private boolean shouldLogRequestBody(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null && (contentType.startsWith("application/json") || contentType.startsWith("application/xml"));
    }

    private void logRequestBody(BufferRequestWrapper request) {
        String body = request.getBody();
        if (body != null && !body.isEmpty()) {
            // Log only the first 500 characters
            logger.info("Request Body: {}", body.length() > 500 ? body.substring(0, 500) : body);
        }
    }

    @SneakyThrows
    private void logResponseBody(ContentCachingResponseWrapper response) {
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0) {
            String body = new String(content, response.getCharacterEncoding());
            // Log only the first 500 characters of the response body
            logger.info("Response Body: {}", body.length() > 500 ? body.substring(0, 500) : body);
        }
    }
}