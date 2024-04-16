package com.caltech.config;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebListener
public class TokenCleanupListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        HttpServletRequest request = (HttpServletRequest) sce.getServletContext().getAttribute("request");
        HttpServletResponse response = (HttpServletResponse) sce.getServletContext().getAttribute("response");
        
        if (request != null && response != null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    // Check if the cookie is a token cookie and if it has expired
                    if (("token".equals(cookie.getName()) || "refreshToken".equals(cookie.getName())) && cookie.getMaxAge() <= 0) {
                        // Clear the expired token cookie from the client side
                        cookie.setValue(null);
                        cookie.setMaxAge(0);
                        cookie.setPath("/");
                        response.addCookie(cookie);
                    }
                }
            }
        }
    }

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
	    HttpServletRequest request = (HttpServletRequest) sce.getServletContext().getAttribute("request");
	    HttpServletResponse response = (HttpServletResponse) sce.getServletContext().getAttribute("response");

	    if (request != null && response != null) {
	        // Perform token cleanup logic during application shutdown for tokens stored in cookies
	        Cookie[] cookies = request.getCookies();
	        if (cookies != null) {
	            for (Cookie cookie : cookies) {
	                // Check if the cookie is a token cookie and if it has expired
	                if (("token".equals(cookie.getName()) || "refreshToken".equals(cookie.getName())) && cookie.getMaxAge() <= 0) {
	                    // Clear the expired token cookie from the client side
	                    cookie.setValue(null);
	                    cookie.setMaxAge(0);
	                    cookie.setPath("/");
	                    response.addCookie(cookie);
	                }
	            }
	        }
	    }
	}

}