package com.example.ecommerceproxy.filters;

import com.example.ecommerceproxy.methods.ForwardHeader;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Objects;

public class RoutingFilter implements Filter {

    private static final String CORE_APP_URL = "http://localhost:8080";
    private static final Logger logger = LoggerFactory.getLogger(RoutingFilter.class);


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String forwardUrl = buildForwardUrl(httpRequest);
        HttpHeaders headers = ForwardHeader.forwardHeaders(httpRequest);


//        Pobieranie body i headers z requesta wprost

        StringBuilder requestBody = new StringBuilder();
        try (BufferedReader reader = httpRequest.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBody.append(line);
            }
        }

        headers.setContentLength(requestBody.toString().getBytes().length);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> entity = new HttpEntity<>(requestBody.toString(), headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity;

        try {
            responseEntity = restTemplate.exchange(forwardUrl, HttpMethod.valueOf(httpRequest.getMethod()), entity, String.class);

            httpResponse.setStatus(responseEntity.getStatusCode().value());
            httpResponse.setContentType("application/json");
            httpResponse.getWriter().write(Objects.requireNonNull(responseEntity.getBody()));
        } catch (Exception e) {
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpResponse.getWriter().write("Error forwarding request: " + e.getMessage());
        }

    }


    private String buildForwardUrl(HttpServletRequest request) {
        StringBuilder forwardUrl = new StringBuilder(CORE_APP_URL).append(request.getRequestURI());

        String queryString = request.getQueryString();
        if (queryString != null) {
            forwardUrl.append("?").append(queryString);
        }

        return forwardUrl.toString();
    }

}
