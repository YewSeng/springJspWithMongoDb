package com.caltech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class IpAddressLockoutServiceTest {

	@InjectMocks
    private IpAddressLockoutService ipAddressLockoutService;

    @BeforeEach
    void setUp() {
        ipAddressLockoutService = new IpAddressLockoutService();
    }

    @Test
    @DisplayName("Test isIpAddressLockedOut when not locked out")
    public void testIsIpAddressLockedOutNotLockedOut() {
        // Arrange
        String ipAddress = "192.168.1.1";

        // Act
        boolean lockedOut = ipAddressLockoutService.isIpAddressLockedOut(ipAddress);

        // Assert
        assertFalse(lockedOut);
    }

    @Test
    @DisplayName("Test isIpAddressLockedOut when locked out")
    public void testIsIpAddressLockedOutLockedOut() {
        // Arrange
        String ipAddress = "192.168.1.2";
        for (int i = 0; i < 5; i++) {
        	ipAddressLockoutService.incrementFailedAttempts(ipAddress); // Increment attempts to reach maximum
        }

        // Act
        boolean lockedOut = ipAddressLockoutService.isIpAddressLockedOut(ipAddress);

        // Assert
        assertTrue(lockedOut);
    }

    @Test
    @DisplayName("Test incrementFailedAttempts when not locked out")
    public void testIncrementFailedAttemptsNotLockedOut() {
        // Arrange
        String ipAddress = "192.168.1.3";

        // Act
        boolean lockedOut = ipAddressLockoutService.incrementFailedAttempts(ipAddress);

        // Assert
        assertFalse(lockedOut);
    }

    @Test
    @DisplayName("Test incrementFailedAttempts when locked out")
    public void testIncrementFailedAttemptsLockedOut() {
        // Arrange
        String ipAddress = "192.168.1.4";
        // Simulate reaching maximum failed attempts
        for (int i = 0; i < 5; i++) {
            ipAddressLockoutService.incrementFailedAttempts(ipAddress);
        }

        // Act
        boolean lockedOut = ipAddressLockoutService.incrementFailedAttempts(ipAddress);

        // Assert
        assertTrue(lockedOut);
    }

    @Test
    @DisplayName("Test resetFailedAttempts")
    public void testResetFailedAttempts() {
        // Arrange
        String ipAddress = "192.168.1.5";
        // Increment failed attempts
        ipAddressLockoutService.incrementFailedAttempts(ipAddress);

        // Act
        ipAddressLockoutService.resetFailedAttempts(ipAddress);
        boolean lockedOut = ipAddressLockoutService.isIpAddressLockedOut(ipAddress);

        // Assert
        assertFalse(lockedOut);
    }
    
    @Test
    @DisplayName("Test setIpAddressLockout")
    public void testSetIpAddressLockout() {
        String ipAddress = "192.168.1.1";
        ipAddressLockoutService.setIpAdressLockout(ipAddress);
        assertTrue(ipAddressLockoutService.isIpAddressLockedOut(ipAddress));
    }

    @Test
    @DisplayName("Test getInvalidAttempts")
    public void testGetInvalidAttempts() {
        String ipAddress = "192.168.1.2";
        assertEquals(0, ipAddressLockoutService.getInvalidAttempts(ipAddress));

        // Increment attempts
        ipAddressLockoutService.incrementFailedAttempts(ipAddress);
        assertEquals(1, ipAddressLockoutService.getInvalidAttempts(ipAddress));
    }
}