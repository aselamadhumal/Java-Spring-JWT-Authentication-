package com.jwtauth.jwtauth.service;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
//@Slf4j(topic = "API_LOG")
public class ApiConnector {

//    private static final Logger logger = LoggerFactory.getLogger(ApiConnector.class);
    private static final Logger logger = (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("API_LOG");

    @Value("${core.bank.base.url}")
    private String coreBankBaseUrl;

    private final WebClient webClient;

    public ApiConnector(WebClient webClient) {
        this.webClient = webClient;
    }

    public String geCoreBank(String url) {
        return getMapping(coreBankBaseUrl + url);
    }

    public String createCoreBank(String url, Object requestBody) {
        return postMapping(coreBankBaseUrl + url, requestBody);
    }

    public String updateCoreBank(String url, Object requestBody) {
        return putMapping(coreBankBaseUrl + url, requestBody);
    }

    public String deleteCoreBank(String url) {
        return deleteMapping(coreBankBaseUrl + url);
    }

    private String deleteMapping(String url) {
        logger.info("DELETE request to URL: {}", url);
        try {
            return webClient.delete()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            logger.error("Error during DELETE request: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            logger.error("An error occurred during DELETE request: {}", e.getMessage());
            return null;
        }
    }

    private String putMapping(String url, Object requestBody) {
        logger.info("PUT request to URL: {}", url);
        try {
            return webClient.put()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            logger.error("Error during PUT request: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            logger.error("An error occurred during PUT request: {}", e.getMessage());
            return null;
        }
    }

    private String postMapping(String url, Object requestBody) {
        logger.info("POST request to URL: {}", url);
        try {
            return webClient.post()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(BodyInserters.fromValue(requestBody))
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            logger.error("Error during POST request: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            logger.error("An error occurred during POST request: {}", e.getMessage());
            return null;
        }
    }

    private String getMapping(String url) {
        logger.info("GET request to URL: {}", url);
        try {
            return webClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        } catch (WebClientResponseException e) {
            logger.error("Error during GET request: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            return null;
        } catch (Exception e) {
            logger.error("An error occurred during GET request: {}", e.getMessage());
            return null;
        }
    }
}

