package com.caltech;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringBootMongoIntegrationApplication {
    
    @Autowired
    private HttpServletResponse response;
    
	public static void main(String[] args) {
		SpringApplication.run(SpringBootMongoIntegrationApplication.class, args);
	}

    @PreDestroy
    public void onDestroy() throws Exception {
        // Invalidate token cookie
        Cookie tokenCookie = new Cookie("token", null);
        tokenCookie.setMaxAge(0); // Expire immediately
        tokenCookie.setPath("/"); // Set cookie path
        response.addCookie(tokenCookie);

        // Invalidate refreshToken cookie
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setMaxAge(0); // Expire immediately
        refreshTokenCookie.setPath("/"); // Set cookie path
        response.addCookie(refreshTokenCookie);
    }
}
