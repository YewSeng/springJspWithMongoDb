package com.caltech.config;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.caltech.service.DefaultUserService;
import io.jsonwebtoken.ExpiredJwtException;

@Component
public class SuperAdminAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${superadmin.secretKey}")
    private String superAdminKey;
    
    @Autowired
    private DefaultUserService userService;
    
    @Autowired
    private JwtGeneratorValidator jwtValidator;
    
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
            // Token-based authentication logic
            // (omitted for brevity)
        } else if ("/superAdminLogin".equals(request.getRequestURI()) && request.getMethod().equals("POST")) {
            // Super Admin authentication logic
            String providedKey = request.getParameter("superAdminKey");
            if (providedKey != null && providedKey.equals(superAdminKey)) {
                // Authentication successful
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(providedKey, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                // Authentication failed
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Super admin authentication failed");
                return;
            }
        } else {
            // Cookie-based authentication logic
            String tokenFromCookie = extractTokenFromCookie(request);
            if (tokenFromCookie != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails tokenDetails = userService.loadUserByUsername(jwtValidator.extractUsername(tokenFromCookie));
                if (jwtValidator.validateToken(tokenFromCookie, tokenDetails)) {
                    UsernamePasswordAuthenticationToken authentication = jwtValidator.getAuthenticationToken(
                            tokenFromCookie, SecurityContextHolder.getContext().getAuthentication(), tokenDetails);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }
}


