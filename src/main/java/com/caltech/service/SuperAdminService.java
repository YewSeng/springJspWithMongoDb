package com.caltech.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
@Service
public class SuperAdminService {

	@Value("${superadmin.secretKey}")
    private String superAdminKey;

    public Boolean verifySuperAdminKey(String key) {
        log.info("Super Admin Key Entered: {}", key);
        log.info("Correct super admin key: {}", superAdminKey);
        return key != null && key.trim().equals(superAdminKey.trim());
    }
}

