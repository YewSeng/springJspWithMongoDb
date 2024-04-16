package com.caltech.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuthenticationService {

	private final AuthenticationManager authenticationManager;
	
    @Value("${superadmin.secretKey}")
    private String superAdminKey;
    
    @Autowired
    public AuthenticationService(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }
    
    // Method to authenticate Admin
    public boolean authenticateAdmin(String username, String password) {
        log.info("Authenticating Admin with Username: {}, Password: {}", username, password);
        // Create authentication token with user details
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        // Authenticate using AuthenticationManager
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            // If authentication is successful, user is an admin
            return authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_ADMIN"));
        } catch (AuthenticationException e) {
            log.error("Admin authentication failed", e);
            return false;
        }
    }

    // Method to authenticate Doctor
    public boolean authenticateDoctor(String username, String password) {
        log.info("Authenticating Doctor with Username: {}, Password: {}", username, password);
        // Create authentication token with user details
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        // Authenticate using AuthenticationManager
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            // If authentication is successful, user is a doctor
            return authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_DOCTOR"));
        } catch (AuthenticationException e) {
            log.error("Doctor authentication failed", e);
            return false;
        }
    }

    // Method to authenticate User
    public boolean authenticateUser(String username, String password) {
        log.info("Authenticating User with Username: {}, Password: {}", username, password);
        // Create authentication token with user details
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        // Authenticate using AuthenticationManager
        try {
            Authentication authentication = authenticationManager.authenticate(authenticationToken);
            // If authentication is successful, user is a regular user
            return authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_USER"));
        } catch (AuthenticationException e) {
            log.error("User authentication failed", e);
            return false;
        }
    }
    
    public boolean authenticateSuperAdmin(String superAdminKey) {
        log.info("Authenticating Super Admin with Super Admin Key: {}", superAdminKey);
        // Check if the provided key matches the predefined Super Admin key
        if (superAdminKey.equals(this.superAdminKey)) {
            // Create authentication token with superadmin details
            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(superAdminKey, null);
            // Authenticate using AuthenticationManager
            try {
                Authentication authentication = authenticationManager.authenticate(authenticationToken);
                // If authentication is successful, check if user has ROLE_SUPERADMIN authority
                return authentication.getAuthorities().stream()
                        .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_SUPERADMIN"));
            } catch (AuthenticationException e) {
                log.error("Super Admin authentication failed", e);
                return false;
            }
        } else {
            // Provided key does not match the predefined Super Admin key
            log.error("Invalid Super Admin key provided");
            return false;
        }
    }
}
