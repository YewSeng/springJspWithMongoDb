package com.caltech.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.caltech.service.DefaultUserService;
import io.jsonwebtoken.ExpiredJwtException;
import javax.servlet.http.Cookie;

@Component
public class JwtFilter extends OncePerRequestFilter {

	@Autowired
    private DefaultUserService userService;
	
	@Autowired
    private JwtGeneratorValidator jwtValidator;
	
    @Autowired
    private AuthenticationManager authenticationManager;
       
    @Value("${superadmin.secretKey}")
    private String superAdminKey;
    
    private String extractTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authorizationHeader = request.getHeader("Authorization");
        String refreshTokenHeader = request.getHeader("RefreshToken");

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            String refreshToken = null; // Initialize refreshToken

            if (refreshTokenHeader != null && refreshTokenHeader.startsWith("Bearer ")) {
                refreshToken = refreshTokenHeader.substring(7);
            }

            try {
                String username = jwtValidator.extractUsername(token);

                if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = userService.loadUserByUsername(username);

                    if (jwtValidator.validateToken(token, userDetails)) {
                        // Token is valid, set the authentication
                        UsernamePasswordAuthenticationToken authentication = jwtValidator.getAuthenticationToken(token, SecurityContextHolder.getContext().getAuthentication(), userDetails);
                        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    } else if (jwtValidator.isTokenExpired(token) && refreshToken != null) {
                        System.out.println("JWT TOKEN: " + token);
                        System.out.println("JWT REFRESH TOKEN: " + refreshToken);
                        UserDetails refreshedUserDetails = userService.loadUserByUsername(jwtValidator.extractUsername(refreshToken));
                        UsernamePasswordAuthenticationToken authentication = jwtValidator.getAuthenticationToken(refreshToken, SecurityContextHolder.getContext().getAuthentication(), refreshedUserDetails);
                        // Generate a new access token and refresh token using the refreshed token's authentication details
                        String newToken = jwtValidator.generateToken(authentication);
                        String newRefreshToken = jwtValidator.generateRefreshToken(authentication);
                        System.out.println("JWT NEW TOKEN: " + newToken);
                        System.out.println("JWT NEW REFRESH TOKEN: " + newRefreshToken);
                        // Set the new tokens in the response headers
                        response.setHeader("Authorization", "Bearer " + newToken);
                        response.setHeader("RefreshToken", "Bearer " + newRefreshToken);
                    }
                }
            } catch (ExpiredJwtException ex) {
                // Token has expired
            	response.sendRedirect(request.getContextPath() + "/login?expired=true");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
                return;
            }
        } else if ("/login".equals(request.getRequestURI()) && SecurityContextHolder.getContext().getAuthentication() == null) {
            // If the request is for /login and there is no authentication context, authenticate the user
            // This assumes that the /login endpoint is not protected and authentication is performed here
            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(request.getParameter("username"), request.getParameter("password")));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (AuthenticationException e) {
                // Handle authentication failure
                // You can redirect the user to an error page or return an error response
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
                return;
            }
        } else if ("/superAdminLogin".equals(request.getRequestURI())  && SecurityContextHolder.getContext().getAuthentication() == null)  {
            String providedKey = request.getParameter("superAdminKey");
            if (providedKey != null && providedKey.equals(superAdminKey)) {
                // Load super admin user details using DefaultUserService
				UserDetails userDetails = userService.loadUserByUsername(providedKey);
				// Create authentication token
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
				// Set authentication in SecurityContextHolder
				SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        String tokenFromCookie = extractTokenFromCookie(request);
        if (tokenFromCookie != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails tokenDetails = userService.loadUserByUsername(jwtValidator.extractUsername(tokenFromCookie));
            System.out.println("User Details: " + tokenDetails);
            System.out.println("Validation: " + jwtValidator.validateToken(tokenFromCookie, tokenDetails));
            if (jwtValidator.validateToken(tokenFromCookie, tokenDetails)) {
                UsernamePasswordAuthenticationToken authentication = jwtValidator.getAuthenticationToken(
                        tokenFromCookie, SecurityContextHolder.getContext().getAuthentication(), tokenDetails);
                System.out.println(authentication);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        filterChain.doFilter(request, response);
    }
}