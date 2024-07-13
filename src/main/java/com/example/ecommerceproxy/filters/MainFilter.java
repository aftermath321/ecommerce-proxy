package com.example.ecommerceproxy.filters;

import com.example.ecommerceproxy.methods.ForwardHeader;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

public class MainFilter implements Filter {

    private static final String SECURITY_URL = "http://localhost:8082/";
    private static final String JWT_TOKEN = "Bearer 1234"; // Static JWT token

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        HttpHeaders headers = ForwardHeader.forwardHeaders(httpRequest);
        headers.add("Authorization", JWT_TOKEN);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.exchange(
                    SECURITY_URL + httpRequest.getRequestURI(),
                    HttpMethod.GET,
                    httpEntity,
                    String.class
            );

            HttpStatus statusCode = (HttpStatus) responseEntity.getStatusCode();
            if (statusCode == HttpStatus.OK) {
                filterChain.doFilter(request, response);
            } else {
                httpResponse.sendError(statusCode.value(), "Proxy request failed: " + statusCode.getReasonPhrase());
            }
        } catch (RestClientException e) {
            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            httpResponse.getWriter().write("Error during proxy request: " + e.getMessage());

        }


    }

}
