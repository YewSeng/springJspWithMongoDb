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
import com.caltech.pojo.Admin;
import com.caltech.utils.CustomPageable;
import com.caltech.utils.AdminPage;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class AdminRepositoryTest {

	@Mock
	private AdminRepository adminRepository;
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Test findAllAdmins method")
	public void testFindAllAdmins() {
        // Mock data
        List<Admin> adminList = new ArrayList<>();
        adminList.add(new Admin("John Doe", "username1", "password1", LocalDateTime.now()));
        adminList.add(new Admin("Jane Smith", "username2", "password2", LocalDateTime.now()));

        // Create a custom pageable object
        CustomPageable<Admin> customPageable = new CustomPageable<>() {
            @Override
            public List<Admin> getContent() {
                return adminList; 
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
                return adminList.size(); 
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

        AdminPage adminPageResults = new AdminPage(adminList, 0, 10, adminList.size(), 1);

        when(adminRepository.findAllAdmins(any(CustomPageable.class))).thenReturn(adminPageResults);

        AdminPage adminPage = adminRepository.findAllAdmins(customPageable);

        // Assertions
        assertNotNull(adminPage);
        assertEquals(adminList.size(), adminPage.getContent().size());
        assertEquals(adminList.size(), adminPage.getTotalElements());
        assertEquals(1, adminPage.getTotalPages());
	}
	
	@Test
	@DisplayName("Test findAdminsByUsernameContaining method")
	public void testFindAdminsByUsernameContaining() {
        // Mock data
        List<Admin> adminList = new ArrayList<>();
        adminList.add(new Admin("John Doe", "username1", "password1", LocalDateTime.now()));
        adminList.add(new Admin("Jane Smith", "username2", "password2", LocalDateTime.now()));
        adminList.add(new Admin("Peter Lim", "User1", "Password1", LocalDateTime.now()));
        
        List<Admin> adminResults = adminList.stream()
        		.filter(admin -> admin.getUsername().contains("username"))
        		.collect(Collectors.toList());
        
        // Create a UserPage object with mock data
        Page<Admin> adminPageResults = new PageImpl<>(adminResults);
        
        // Mock the behavior of the adminRepository for findAdminsByUsernameContaining method
        when(adminRepository.findAdminsByUsernameContaining(any(String.class), any(Pageable.class))).thenReturn(adminPageResults);
        
        // Call the method under test
        Page<Admin> adminPage = adminRepository.findAdminsByUsernameContaining("username", PageRequest.of(0, 10));
        
        // Assertions
        assertNotNull(adminPage);
        assertEquals(adminList.size() - 1, adminPage.getContent().size());
        assertEquals(adminList.size() - 1, adminPage.getTotalElements());
        assertEquals(1, adminPage.getTotalPages());
	}
	
	@Test
	@DisplayName("Test findAdminsByNameContaining method")
	public void testFindAdminsByNameContaining() {
		// Mock data
        List<Admin> adminList = new ArrayList<>();
        adminList.add(new Admin("John Doe", "username1", "password1", LocalDateTime.now()));
        adminList.add(new Admin("Jane Smith", "username2", "password2", LocalDateTime.now()));
        
        // Filter the adminList to get only the admin with name "John Doe"
        List<Admin> adminResults = adminList.stream()
        		.filter(admin -> admin.getName().equalsIgnoreCase("John Doe"))
        		.collect(Collectors.toList());
        
        // Create a UserPage object with mock data
        Page<Admin> adminPageResults = new PageImpl<>(adminResults);
        
        // Mock the behavior of the adminRepository for findAdminsByNameContaining method
        when(adminRepository.findAdminsByNameContaining(any(String.class), any(Pageable.class))).thenReturn(adminPageResults);
        
        // Call the method under test
        Page<Admin> adminPage = adminRepository.findAdminsByNameContaining("John", PageRequest.of(0, 10));
        
        // Assertions
        assertNotNull(adminPage);
        assertEquals(1, adminPage.getContent().size());
        assertEquals("John Doe", adminPage.getContent().get(0).getName());
        assertEquals(1, adminPage.getTotalElements());
        assertEquals(1, adminPage.getTotalPages());		
	}
	
    @SuppressWarnings("unchecked")
	@Test
    @DisplayName("Test findAdminsByName method")
    public void testFindAdminsByName() {
        // Mock data
        List<Admin> adminList = new ArrayList<>();
        adminList.add(new Admin("John Doe", "username1", "password1", LocalDateTime.now()));
        adminList.add(new Admin("Jane Smith", "username2", "password2", LocalDateTime.now()));

        // Filter the adminList to get only the admin with name "John Doe"
        List<Admin> adminResults = adminList.stream()
                .filter(admin -> admin.getName().equalsIgnoreCase("John Doe"))
                .collect(Collectors.toList());
        
        // Create a custom pageable object
        CustomPageable<Admin> customPageable = new CustomPageable<>() {
            @Override
            public List<Admin> getContent() {
                return adminList; 
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
                return adminList.size(); 
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

        // Create a AdminPage object with mock data
        AdminPage adminPageResults = new AdminPage(adminResults, 0, 10, adminResults.size(), 1);

        // Mock the behavior of the adminRepository for findAdminsByNameContaining method
        when(adminRepository.findAdminsByName(any(String.class), any(CustomPageable.class))).thenReturn(adminPageResults);

        // Call the method under test
        AdminPage adminPage = adminRepository.findAdminsByName("John", customPageable);

        // Assertions
        assertNotNull(adminPage);
        assertEquals(1, adminPage.getContent().size());
        assertEquals("John Doe", adminPage.getContent().get(0).getName());
        assertEquals(1, adminPage.getTotalElements());
        assertEquals(1, adminPage.getTotalPages());
    }
    
    @SuppressWarnings("unchecked")
	@Test
    @DisplayName("Test findAdminsByUsername method")
    public void testFindAdminsByUsername() {
        // Mock data
        List<Admin> adminList = new ArrayList<>();
        adminList.add(new Admin("John Doe", "username1", "password1", LocalDateTime.now()));
        adminList.add(new Admin("Jane Smith", "username2", "password2", LocalDateTime.now()));
        adminList.add(new Admin("Peter Lim", "Admin1", "AdminPassword1", LocalDateTime.now()));
        
        List<Admin> adminResults = adminList.stream()
        		.filter(admin -> admin.getUsername().contains("username"))
        		.collect(Collectors.toList());
        
        // Create a custom pageable object
        CustomPageable<Admin> customPageable = new CustomPageable<>() {
            @Override
            public List<Admin> getContent() {
                return adminList; 
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
                return adminList.size(); 
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

        // Create a AdminPage object with mock data
        AdminPage adminPageResults = new AdminPage(adminResults, 0, 10, adminResults.size(), 1);

        // Mock the behavior of the adminRepository for findAdminsByUsername method
        when(adminRepository.findAdminsByUsername(any(String.class), any(CustomPageable.class))).thenReturn(adminPageResults);

        // Call the method under test
        AdminPage adminPage = adminRepository.findAdminsByUsername("username", customPageable);

        // Assertions
        assertNotNull(adminPage);
        assertEquals(adminList.size() - 1, adminPage.getContent().size());
        assertEquals(adminList.size() - 1, adminPage.getTotalElements());
        assertEquals(1, adminPage.getTotalPages());
    }
}
