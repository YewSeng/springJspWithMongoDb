package com.caltech.service;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
@TestPropertySource("classpath:application.properties") 
public class SuperAdminServiceTest {

    @Autowired
    private SuperAdminService superAdminService;
    
    @Value("${superadmin.secretKey}")
    private String superAdminKey;

    @Test
    @DisplayName("Test verifySuperAdminKey method - Correct Key")
    public void testVerifySuperAdminKey() {
        String key = superAdminKey;

        boolean isCorrectSuperAdminKey = superAdminService.verifySuperAdminKey(key);

        assertTrue(isCorrectSuperAdminKey);
    }
    
    @Test
    @DisplayName("Test verifySuperAdminKey method - Incorrect Key")
    public void testVerifySuperAdminKeyWithInvalidKey() {
        String key = "wrongKey";

        boolean isCorrectSuperAdminKey = superAdminService.verifySuperAdminKey(key);

        assertFalse(isCorrectSuperAdminKey);
    }
}

