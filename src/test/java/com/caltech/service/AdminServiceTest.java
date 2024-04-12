package com.caltech.service;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.caltech.exception.AdminNotFoundException;
import com.caltech.exception.UsernameAlreadyExistException;
import com.caltech.pojo.Admin;
import com.caltech.repository.AdminRepository;
import com.caltech.utils.AdminPage;
import com.caltech.utils.CustomPageable;

@ExtendWith(MockitoExtension.class)
public class AdminServiceTest {

	@Mock
	private AdminRepository adminRepository;
	
    @Mock
    private BcryptService bcryptService;
    
	@InjectMocks
	private AdminService adminService;
	
	@Test
	@DisplayName("Test findAdminById method")
	public void testFindAdminById() {
		ObjectId adminId = new ObjectId();
		Admin admin = new Admin("John", "john_doe", "password", LocalDateTime.now());
		admin.setAdminId(adminId);
		when(adminRepository.findById(adminId)).thenReturn(Optional.of(admin));
		Optional<Admin> foundAdminOptional = adminService.findAdminById(adminId);
		assertTrue(foundAdminOptional.isPresent());
		assertEquals(admin, foundAdminOptional.get());
	}
	
	@Test
	@DisplayName("Test findAdminByUsername method")
	public void testFindAdminByUsername() {
		String username = "username";
		Admin admin = new Admin("John", "username", "password", LocalDateTime.now());
		when(adminRepository.findByUsername(username)).thenReturn(Optional.of(admin));
		Optional<Admin> foundAdminOptional = adminService.findAdminByUsername(username);
		assertTrue(foundAdminOptional.isPresent());
		assertEquals(admin, foundAdminOptional.get());
	}
	
	@Test
	@DisplayName("Test findAllAdmins method")
	public void testFindAllAdmins() {
		List<Admin> adminList = new ArrayList<>();
		adminList.add(new Admin("John", "john_doe", "password", LocalDateTime.now()));
		adminList.add(new Admin("Alice", "alice_smith", "password123", LocalDateTime.now()));
		AdminPage adminPage = new AdminPage(adminList, 0, 10, adminList.size(), 1);
		CustomPageable<Admin> customPageable = new AdminPage(0,10);
		when(adminRepository.findAllAdmins(customPageable)).thenReturn(adminPage);
		AdminPage foundAdminPage = adminService.findAllAdmins(customPageable);
		assertEquals(adminPage, foundAdminPage);
		assertEquals(adminList.size(), foundAdminPage.getContent().size());
	}
	
	@Test
	@DisplayName("Test findAdminsByUsername method")
	public void testFindAdminsByUsername() {
		String username = "john_doe";
		List<Admin> adminList = new ArrayList<>();
		adminList.add(new Admin("John", "john_doe", "password", LocalDateTime.now()));
		adminList.add(new Admin("Alice", "alice_smith", "password123", LocalDateTime.now()));
		List<Admin> adminResults = adminList.stream()
				.filter(admin -> admin.getUsername().contains(username))
				.collect(Collectors.toList());
		AdminPage adminPage = new AdminPage(adminResults, 0, 10, adminResults.size(), 1);
		CustomPageable<Admin> customPageable = new AdminPage(0,10);
		when(adminRepository.findAdminsByUsername(username, customPageable)).thenReturn(adminPage);
		AdminPage foundAdminPage = adminService.findAdminsByUsername(username, customPageable);
		assertEquals(adminPage, foundAdminPage);
		assertEquals(1, foundAdminPage.getContent().size());
	}
	
	@Test
	@DisplayName("Test findAdminsByName method")
	public void testFindAdminsByName() {
		String name = "John";
		List<Admin> adminList = new ArrayList<>();
		adminList.add(new Admin("John", "john_doe", "password", LocalDateTime.now()));
		adminList.add(new Admin("Alice", "alice_smith", "password123", LocalDateTime.now()));
		adminList.add(new Admin("Johnny", "johnny", "password", LocalDateTime.now()));
		List<Admin> adminResults = adminList.stream()
				.filter(admin -> admin.getName().contains(name))
				.collect(Collectors.toList());
		AdminPage adminPage = new AdminPage(adminResults, 0, 10, adminResults.size(), 1);
		CustomPageable<Admin> customPageable = new AdminPage(0,10);
		when(adminRepository.findAdminsByName(name, customPageable)).thenReturn(adminPage);
		AdminPage foundAdminPage = adminService.findAdminsByName(name, customPageable);
		assertEquals(adminPage, foundAdminPage);
		assertEquals(2, foundAdminPage.getContent().size());
	}
	
    @Test
    @DisplayName("Test verifyExistingUsername method")
	public void testVerifyExistingUsername() {
    	String username = "john_doe";
		Admin admin1 = new Admin("John", "john_doe", "password", LocalDateTime.now());
		Admin admin2 = new Admin("Alice", "alice_smith", "password123", LocalDateTime.now());
		List<Admin> adminList = new ArrayList<>();
		adminList.add(admin1);
		adminList.add(admin2);
		when(adminRepository.findByUsername(username)).thenReturn(Optional.of(admin1));
		boolean isUsernameTaken = adminService.verifyExistingUsername(username);
		assertTrue(isUsernameTaken);
    }
    
    @Test
    @DisplayName("Test authenticateAdmin method - Correct Password")
    public void testAuthenticateAdminWithCorrectPassword() {
        String username = "john_doe";
        String password = "password";
        Admin admin = new Admin("John", "john_doe", bcryptService.hashPassword(password), LocalDateTime.now());
        when(adminRepository.findByUsername(username)).thenReturn(Optional.of(admin));
        when(bcryptService.verifyPassword(password, admin.getPassword())).thenReturn(true);
        boolean isAdmin = adminService.authenticateAdmin(username, password);
        assertTrue(isAdmin);
    }
    
    @Test
    @DisplayName("Test authenticateAdmin method - Incorrect Password")
    public void testAuthenticateAdminWithIncorrectPassword() {
        String username = "john_doe";
        String password = "wrongPassword";
        Admin admin = new Admin("John", "john_doe", bcryptService.hashPassword(password), LocalDateTime.now());
        when(adminRepository.findByUsername(username)).thenReturn(Optional.of(admin));
        when(bcryptService.verifyPassword(password, admin.getPassword())).thenReturn(false);
        boolean isAdmin = adminService.authenticateAdmin(username, password);
        assertFalse(isAdmin);
    }
    
    @Test
    @DisplayName("Test createAdmin method")
    public void testCreateAdmin() {
    	Admin admin = new Admin("John", "john_doe", "password", LocalDateTime.now());
    	Admin hashedAdmin = new Admin(admin.getName(), admin.getUsername(), bcryptService.hashPassword(admin.getPassword()), admin.getRegistrationDate());
    	when(adminRepository.save(admin)).thenReturn(hashedAdmin);
    	Admin createdAdmin = adminService.createAdmin(admin);
    	assertEquals(hashedAdmin.getPassword(), createdAdmin.getPassword());
    }
    
    @Test
    @DisplayName("Test updateAdmin method")
    public void testUpdateAdmin() throws AdminNotFoundException, UsernameAlreadyExistException {
    	ObjectId adminId = new ObjectId();
    	Admin existingAdmin = new Admin("John", "john_doe", "password", LocalDateTime.now());
    	existingAdmin.setAdminId(adminId);
    	Admin updatedAdmin = new Admin("Peter Lim", "Admin1", "password", LocalDateTime.now());
    	updatedAdmin.setPassword(bcryptService.hashPassword(updatedAdmin.getPassword()));
    	when(adminRepository.findById(adminId)).thenReturn(Optional.of(existingAdmin));
    	when(adminRepository.findByUsername(updatedAdmin.getUsername())).thenReturn(Optional.empty());
    	when(adminRepository.save(existingAdmin)).thenReturn(updatedAdmin);
    	Admin returnedAdmin = adminService.updateAdmin(adminId, updatedAdmin);
    	when(bcryptService.verifyPassword(updatedAdmin.getPassword(), returnedAdmin.getPassword())).thenReturn(true);
    	assertEquals(updatedAdmin, returnedAdmin);
    	assertEquals(updatedAdmin.getPassword(), returnedAdmin.getPassword());
    	assertTrue(bcryptService.verifyPassword(updatedAdmin.getPassword(), returnedAdmin.getPassword()));
    }
    
    @Test
    @DisplayName("Test updateAdmin method - Admin Not Found")
    public void testUpdateAdminNotFound() throws AdminNotFoundException, UsernameAlreadyExistException {
    	ObjectId adminId = new ObjectId();
    	Admin updatedAdmin = new Admin("Peter Lim", "Admin1", "password", LocalDateTime.now());
    	when(adminRepository.findById(adminId)).thenReturn(Optional.empty());
    	AdminNotFoundException exception = assertThrows(AdminNotFoundException.class, () -> {
    		adminService.updateAdmin(adminId, updatedAdmin);
    	});
    	assertNotNull(exception);	    	
    }
    
    @Test
    @DisplayName("Test updateAdmin method - Username Already Exists")
    public void testUpdateAdminUsernameAlreadyExists() throws AdminNotFoundException, UsernameAlreadyExistException {
    	ObjectId adminId = new ObjectId();
    	Admin existingAdmin = new Admin("John", "john_doe", "password", LocalDateTime.now());
    	existingAdmin.setAdminId(adminId);
    	Admin updatedAdmin = new Admin("Peter Lim", "Admin1", "password", LocalDateTime.now());
    	when(adminRepository.findById(adminId)).thenReturn(Optional.of(existingAdmin));
    	when(adminRepository.findByUsername(updatedAdmin.getUsername())).thenReturn(Optional.of(new Admin()));
        UsernameAlreadyExistException exception = assertThrows(UsernameAlreadyExistException.class, () -> {
            adminService.updateAdmin(adminId, updatedAdmin);
        });   
        assertNotNull(exception); 			
    }
    
    @Test
    @DisplayName("Test deleteAdmin method")
    public void testDeleteAdmin() throws AdminNotFoundException {
    	ObjectId adminId = new ObjectId();
    	Admin admin = new Admin("John", "john_doe", "password", LocalDateTime.now());
    	admin.setAdminId(adminId);
    	when(adminRepository.findById(adminId)).thenReturn(Optional.of(admin));
    	adminService.deleteAdmin(adminId);
    	verify(adminRepository, times(1)).deleteById(adminId);   	
    }
    
    @Test
    @DisplayName("Test deleteAdmin method - Admin Id Not Found")
    public void testDeleteAdminNotFound() throws AdminNotFoundException {
    	ObjectId adminId = new ObjectId();
    	when(adminRepository.findById(adminId)).thenReturn(Optional.empty());
    	AdminNotFoundException exception = assertThrows(AdminNotFoundException.class, () -> {
            adminService.deleteAdmin(adminId);
        });   
        assertNotNull(exception); 		
    	verify(adminRepository, never()).deleteById(adminId);   	
    }
}
