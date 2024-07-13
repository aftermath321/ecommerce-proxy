package com.example.ecommerceproxy;

import com.example.ecommerceproxy.filters.MainFilter;
import com.example.ecommerceproxy.filters.RoutingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ProxyConfig {


   @Bean
   public FilterRegistrationBean<MainFilter> mainFilterRegistration(){
      FilterRegistrationBean<MainFilter> registration = new FilterRegistrationBean<>();
      registration.setFilter(new MainFilter());
      registration.addUrlPatterns("/*");
      registration.setName("MainFilter");
      registration.setOrder(1);
      return registration;

   }

   @Bean
   public FilterRegistrationBean<RoutingFilter> routingFilterRegistration(){
      FilterRegistrationBean<RoutingFilter> registration = new FilterRegistrationBean<>();
      registration.setFilter(new RoutingFilter());
      registration.addUrlPatterns("/*");
      registration.setName("RoutingFilter");
      registration.setOrder(2);
      return registration;
   }

}
