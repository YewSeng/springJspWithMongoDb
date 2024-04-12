package com.caltech.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class BcryptServiceTest {

    @InjectMocks
    private BcryptService bcryptService;

    @Test
    @DisplayName("Test hashAndVerifyPassword method")
    public void testHashAndVerifyPassword() {
        // Arrange
        String password = "testPassword";

        // Act
        String hashedPassword = bcryptService.hashPassword(password);

        // Assert
        assertTrue(bcryptService.verifyPassword(password, hashedPassword), "Password verification failed");
    }
}
