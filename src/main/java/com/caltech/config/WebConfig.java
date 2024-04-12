package com.caltech.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean<HiddenHttpMethodFilter> hiddenHttpMethodFilterRegistration() {
        FilterRegistrationBean<HiddenHttpMethodFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new HiddenHttpMethodFilter());
        registration.addUrlPatterns("/*"); // Set URL patterns for the filter
        return registration;
    }
}
