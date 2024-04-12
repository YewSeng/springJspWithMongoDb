package com.caltech.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

@Service
public class IpAddressLockoutService {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCKOUT_DURATION = 10 * 60 * 1000; // 10 minutes in milliseconds

    private Map<String, Integer> failedAttemptsMap = new ConcurrentHashMap<>();
    private Map<String, Long> lockoutMap = new ConcurrentHashMap<>();

    public boolean isIpAddressLockedOut(String ipAddress) {
        Long lockoutTime = lockoutMap.get(ipAddress);
        return lockoutTime != null && lockoutTime + LOCKOUT_DURATION > System.currentTimeMillis();
    }

    public boolean incrementFailedAttempts(String ipAddress) {
        int failedAttempts = failedAttemptsMap.getOrDefault(ipAddress, 0) + 1;
        failedAttemptsMap.put(ipAddress, failedAttempts);
        if (failedAttempts >= MAX_FAILED_ATTEMPTS) {
            lockoutMap.put(ipAddress, System.currentTimeMillis());
            return true; // IP address locked out
        }
        return false; // IP address not locked out yet
    }

    public void resetFailedAttempts(String ipAddress) {
        failedAttemptsMap.remove(ipAddress);
    }
    
    public void setIpAdressLockout(String ipAddress) {
        lockoutMap.put(ipAddress, System.currentTimeMillis());
    }

    public int getInvalidAttempts(String ipAddress) {
        return failedAttemptsMap.getOrDefault(ipAddress, 0);
    }
}


