package com.example.ecommerceproxy.methods;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;

import java.util.Enumeration;

public class ForwardHeader {

    public static HttpHeaders forwardHeaders(HttpServletRequest httpServletRequest){

        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headersName = httpServletRequest.getHeaderNames();
        while(headersName.hasMoreElements()){
            String headerName = headersName.nextElement();
            String headerValue = httpServletRequest.getHeader(headerName);
            headers.add(headerName, headerValue);
        }
        return headers;
    }

}
