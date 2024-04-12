package com.caltech.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import com.caltech.pojo.User;

@SpringBootTest
public class UniqueUsernameRepositoryTest {

    @MockBean
    private UniqueUsernameRepository<User> uniqueUsernameRepository;

    @Test
    @DisplayName("Test findByUsername method")
    public void testFindByUsername() {
        // Create a test user
        User mockUser = new User();
        mockUser.setUsername("mockUsername");
        mockUser.setName("mockUser");
        mockUser.setPassword("mockPassword");

        // Mock the behavior of findByUsername method
        when(uniqueUsernameRepository.findByUsername("mockUsername")).thenReturn(Optional.of(mockUser));

        // Perform the findByUsername operation
        Optional<User> optionalUser = uniqueUsernameRepository.findByUsername("mockUsername");

        // Assertions
        assertTrue(optionalUser.isPresent());
        assertEquals("mockUsername", optionalUser.get().getUsername());
        assertEquals("mockUser", optionalUser.get().getName());
        assertEquals("mockPassword", optionalUser.get().getPassword());
    }
}
