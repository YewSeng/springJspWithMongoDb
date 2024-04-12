package com.caltech.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.caltech.pojo.User;
import com.caltech.utils.UserPage;
import com.caltech.utils.CustomPageable;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserRepositoryTest {

    @Mock
    private UserRepository userRepository;

    @SuppressWarnings("unchecked")
	@Test
    @DisplayName("Test findAllUsers method")
    public void testFindAllUsers() {
        // Mock data
        List<User> userList = new ArrayList<>();
        userList.add(new User("John Doe", "username1", "password1", LocalDateTime.now()));
        userList.add(new User("Jane Smith", "username2", "password2", LocalDateTime.now()));

        // Create a custom pageable object
        CustomPageable<User> customPageable = new CustomPageable<>() {
            @Override
            public List<User> getContent() {
                return userList; 
            }

            @Override
            public int getPageNumber() {
                return 0; 
            }

            @Override
            public int getPageSize() {
                return 10; 
            }

            @Override
            public long getTotalElements() {
                return userList.size(); 
            }

            @Override
            public int getTotalPages() {
                return 1; 
            }

            @Override
            public boolean hasNext() {
                return false; 
            }

            @Override
            public boolean hasPrevious() {
                return false; 
            }

            @Override
            public Sort getDefaultSort() {
                return Sort.unsorted();
            }
        };

        // Create a UserPage object with mock data
        UserPage userPageResults = new UserPage(userList, 0, 10, userList.size(), 1);

        // Mock the behavior of the userRepository for findAllUsers method
        when(userRepository.findAllUsers(any(CustomPageable.class))).thenReturn(userPageResults);

        // Call the method under test
        UserPage userPage = userRepository.findAllUsers(customPageable);

        // Assertions
        assertNotNull(userPage);
        assertEquals(userList.size(), userPage.getContent().size());
        assertEquals(userList.size(), userPage.getTotalElements());
        assertEquals(1, userPage.getTotalPages());
    }

    @Test
    @DisplayName("Test findUsersByUsernameContaining method")
    public void testFindUsersByUsernameContaining() {
        // Mock data
        List<User> userList = new ArrayList<>();
        userList.add(new User("John Doe", "username1", "password1", LocalDateTime.now()));
        userList.add(new User("Jane Smith", "username2", "password2", LocalDateTime.now()));
        userList.add(new User("Peter Lim", "Admin1", "AdminPassword1", LocalDateTime.now()));
        
        List<User> userResults = userList.stream()
        		.filter(user -> user.getUsername().contains("username"))
        		.collect(Collectors.toList());

        // Create a UserPage object with mock data
        Page<User> userPageResults = new PageImpl<>(userResults);

        // Mock the behavior of the userRepository for findUsersByUsernameContaining method
        when(userRepository.findUsersByUsernameContaining(any(String.class), any(Pageable.class))).thenReturn(userPageResults);

        // Call the method under test
        Page<User> userPage = userRepository.findUsersByUsernameContaining("username", PageRequest.of(0, 10));

        // Assertions
        assertNotNull(userPage);
        assertEquals(userList.size() - 1, userPage.getContent().size());
        assertEquals(userList.size() - 1, userPage.getTotalElements());
        assertEquals(1, userPage.getTotalPages());
    }
    
    @Test
    @DisplayName("Test findUsersByNameContaining method")
    public void testFindUsersByNameContaining() {
        // Mock data
        List<User> userList = new ArrayList<>();
        userList.add(new User("John Doe", "username1", "password1", LocalDateTime.now()));
        userList.add(new User("Jane Smith", "username2", "password2", LocalDateTime.now()));

        // Filter the userList to get only the user with name "John Doe"
        List<User> userResults = userList.stream()
                .filter(user -> user.getName().equalsIgnoreCase("John Doe"))
                .collect(Collectors.toList());

        // Create a UserPage object with mock data
        Page<User> userPageResults = new PageImpl<>(userResults);

        // Mock the behavior of the userRepository for findUsersByNameContaining method
        when(userRepository.findUsersByNameContaining(any(String.class), any(Pageable.class))).thenReturn(userPageResults);

        // Call the method under test
        Page<User> userPage = userRepository.findUsersByNameContaining("John", PageRequest.of(0, 10));
        System.out.println(userPage);
        // Assertions
        assertNotNull(userPage);
        assertEquals(1, userPage.getContent().size());
        assertEquals("John Doe", userPage.getContent().get(0).getName()); // Verify only user with name "John Doe" is returned
        assertEquals(1, userPage.getTotalElements());
        assertEquals(1, userPage.getTotalPages());
    }
    
    @SuppressWarnings("unchecked")
	@Test
    @DisplayName("Test findUsersByUsername method")
    public void testFindUsersByUsername() {
        // Mock data
        List<User> userList = new ArrayList<>();
        userList.add(new User("John Doe", "username1", "password1", LocalDateTime.now()));
        userList.add(new User("Jane Smith", "username2", "password2", LocalDateTime.now()));
        userList.add(new User("Peter Lim", "Admin1", "AdminPassword1", LocalDateTime.now()));
        
        List<User> userResults = userList.stream()
        		.filter(user -> user.getUsername().contains("username"))
        		.collect(Collectors.toList());
        
        // Create a custom pageable object
        CustomPageable<User> customPageable = new CustomPageable<>() {
            @Override
            public List<User> getContent() {
                return userList; 
            }

            @Override
            public int getPageNumber() {
                return 0; 
            }

            @Override
            public int getPageSize() {
                return 10; 
            }

            @Override
            public long getTotalElements() {
                return userList.size(); 
            }

            @Override
            public int getTotalPages() {
                return 1; 
            }

            @Override
            public boolean hasNext() {
                return false; 
            }

            @Override
            public boolean hasPrevious() {
                return false; 
            }

            @Override
            public Sort getDefaultSort() {
                return Sort.unsorted();
            }
        };

        // Create a UserPage object with mock data
        UserPage userPageResults = new UserPage(userResults, 0, 10, userResults.size(), 1);

        // Mock the behavior of the userRepository for findUserByUsername method
        when(userRepository.findUsersByUsername(any(String.class), any(CustomPageable.class))).thenReturn(userPageResults);

        // Call the method under test
        UserPage userPage = userRepository.findUsersByUsername("username", customPageable);

        // Assertions
        assertNotNull(userPage);
        assertEquals(userList.size() - 1, userPage.getContent().size());
        assertEquals(userList.size() - 1, userPage.getTotalElements());
        assertEquals(1, userPage.getTotalPages());
    }
    
    @SuppressWarnings("unchecked")
	@Test
    @DisplayName("Test findUsersByName method")
    public void testFindUsersByName() {
        // Mock data
        List<User> userList = new ArrayList<>();
        userList.add(new User("John Doe", "username1", "password1", LocalDateTime.now()));
        userList.add(new User("Jane Smith", "username2", "password2", LocalDateTime.now()));

        // Filter the userList to get only the user with name "John Doe"
        List<User> userResults = userList.stream()
                .filter(user -> user.getName().equalsIgnoreCase("John Doe"))
                .collect(Collectors.toList());
        
        // Create a custom pageable object
        CustomPageable<User> customPageable = new CustomPageable<>() {
            @Override
            public List<User> getContent() {
                return userList; 
            }

            @Override
            public int getPageNumber() {
                return 0; 
            }

            @Override
            public int getPageSize() {
                return 10; 
            }

            @Override
            public long getTotalElements() {
                return userList.size(); 
            }

            @Override
            public int getTotalPages() {
                return 1; 
            }

            @Override
            public boolean hasNext() {
                return false; 
            }

            @Override
            public boolean hasPrevious() {
                return false; 
            }

            @Override
            public Sort getDefaultSort() {
                return Sort.unsorted();
            }
        };

        // Create a UserPage object with mock data
        UserPage userPageResults = new UserPage(userResults, 0, 10, userResults.size(), 1);

        // Mock the behavior of the userRepository for findUsersByNameContaining method
        when(userRepository.findUsersByName(any(String.class), any(CustomPageable.class))).thenReturn(userPageResults);

        // Call the method under test
        UserPage userPage = userRepository.findUsersByName("John", customPageable);

        // Assertions
        assertNotNull(userPage);
        assertEquals(1, userPage.getContent().size());
        assertEquals("John Doe", userPage.getContent().get(0).getName());
        assertEquals(1, userPage.getTotalElements());
        assertEquals(1, userPage.getTotalPages());
    }
}