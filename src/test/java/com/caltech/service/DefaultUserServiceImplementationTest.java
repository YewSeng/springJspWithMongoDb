package com.caltech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.caltech.pojo.Admin;
import com.caltech.pojo.Doctor;
import com.caltech.pojo.User;
import com.caltech.repository.AdminRepository;
import com.caltech.repository.DoctorRepository;
import com.caltech.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class DefaultUserServiceImplementationTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private DoctorRepository doctorRepository;
    
    @Mock
    private AdminRepository adminRepository;
    
    @Mock
    private Environment environment; 
    
    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    
    @InjectMocks
    private DefaultUserServiceImplementation userService;

    @BeforeEach
    void setUp() {
        userService.setSuperAdminSecretKey("superSecretKey");
    }

    @Test
    @DisplayName("Test loadUserByUsername for SuperAdmin")
    public void testLoadUserByUsernameSuperAdmin() {
        // Arrange
        String superAdminSecretKey = "superSecretKey";
        
        // Act
        UserDetails userDetails = userService.loadUserByUsername(superAdminSecretKey);
        
        // Assert
        assertNotNull(userDetails);
        assertTrue(userDetails instanceof org.springframework.security.core.userdetails.User);
        assertEquals(superAdminSecretKey, userDetails.getUsername());
        assertEquals("ROLE_SUPERADMIN", userDetails.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    @DisplayName("Test loadUserByUsername for User")
    public void testLoadUserByUsernameUser() {
        // Arrange
        String username = "User";
        String password = "userpassword";
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        // Mocking User scenario
        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = userService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().containsAll(
                AuthorityUtils.createAuthorityList("ROLE_USER")));
    }
    
    @Test
    @DisplayName("Test loadUserByUsername for Doctor")
    public void testLoadUserByUsernameDoctor() {
        // Arrange
        String username = "Doctor";
        String password = "doctorpassword";
        Doctor doctor = new Doctor();
        doctor.setUsername(username);
        doctor.setPassword(password);

        // Mocking Doctor scenario
        when(doctorRepository.findByUsername(username))
                .thenReturn(Optional.of(doctor));

        // Act
        UserDetails userDetails = userService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().containsAll(
                AuthorityUtils.createAuthorityList("ROLE_DOCTOR")));
    }
    
    @Test
    @DisplayName("Test loadUserByUsername for Admin")
    public void testLoadUserByUsernameAdmin() {
        // Arrange
        String username = "Admin";
        String password = "adminpassword";
        Admin admin = new Admin();
        admin.setUsername(username);
        admin.setPassword(password);

        // Mocking Admin scenario
        when(adminRepository.findByUsername(username))
                .thenReturn(Optional.of(admin));

        // Act
        UserDetails userDetails = userService.loadUserByUsername(username);

        // Assert
        assertNotNull(userDetails);
        assertEquals(username, userDetails.getUsername());
        assertEquals(password, userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().containsAll(
                AuthorityUtils.createAuthorityList("ROLE_ADMIN")));
    }

    @Test
    @DisplayName("Test loadUserByUsername throws UsernameNotFoundException")
    public void testLoadUserByUsernameUsernameNotFoundException() {
        // Arrange
        String username = "unknownUser";

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername(username);
        });
    }
}
