package com.caltech.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import com.caltech.config.JwtGeneratorValidator;
import com.caltech.exception.AdminNotFoundException;
import com.caltech.exception.UsernameAlreadyExistException;
import com.caltech.pojo.Admin;
import com.caltech.service.AdminService;
import com.caltech.utils.CustomPageable;
import com.caltech.utils.AdminPage;

@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
    private AdminService adminService;

    @Mock
    private JwtGeneratorValidator jwtValidator;
    
    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test goToCreateAdminPage method - Success")
    public void testGoToCreateAdminPage() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        // Invoke the controller method
        ModelAndView mav = adminController.goToCreateAdminPage(request, response);
        
        // Additional assertions
        assertEquals("createAdmin", mav.getViewName());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
    
    @Test
    @DisplayName("Test createAdmin method - Success")
    public void testCreateAdminSuccess() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock Admin data
        String name = "John Doe";
        String username = "AdminTest1";
        String password = "Newpassword@1";

        // Mock AdminService behavior
        when(adminService.verifyExistingUsername(anyString())).thenReturn(false);

        // Invoke the controller method
        ModelAndView mav = adminController.createAdmin(name, username, password, request, response, redirectAttributes);

        // Verify behavior
        verify(adminService, times(1)).createAdmin(any(Admin.class));
        assertEquals("redirect:/api/v1/admins/getAllAdmins?page=0&size=5", mav.getViewName());

        // Additional assertions
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Test createAdmin method - Username already exists")
    public void testCreateAdminUsernameAlreadyExists() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock admin data
        String name = "John Doe";
        String username = "AdminTest1";
        String password = "Newpassword@1";
        
        // Mock AdminService behavior to return true (username already exists)
        AdminService adminService = mock(AdminService.class);
        when(adminService.verifyExistingUsername(anyString())).thenReturn(true);

        // Invoke the controller method
        AdminController adminController = new AdminController(adminService, jwtValidator);
        ModelAndView mav = adminController.createAdmin(name, username, password, request, response, redirectAttributes);

        // Verify behavior
        verify(adminService, never()).createAdmin(any(Admin.class)); // Ensure createAdmin is not called
        assertEquals("redirect:/api/v1/admins/createAdmin", mav.getViewName());
        
        // Verify error message is set correctly
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Username is already taken. Please try again!");

        // Verify status code of the response
        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
    }
    
    @Test
    @DisplayName("Test createAdmin method - IllegalArgumentException")
    public void testCreateAdminIllegalArgumentException() {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap(); // Use RedirectAttributesModelMap
        
        // Mock admin data
        String name = "";
        String username = "AdminTest1";
        String password = "Newpassword@1";
        
        // Mock AdminService behavior
        AdminService adminService = mock(AdminService.class);

        // Invoke the controller method
        AdminController adminController = new AdminController(adminService, jwtValidator);
        ModelAndView mav = adminController.createAdmin(name, username, password, request, response, redirectAttributes);

        // Verify behavior
        verify(adminService, never()).createAdmin(any(Admin.class)); 
        assertEquals("redirect:/api/v1/admins/createAdmin", mav.getViewName());
        
        // Verify error message is set correctly
        assertEquals("Name cannot be empty", redirectAttributes.getFlashAttributes().get("errorMessage").toString().trim());

        // Verify status code of the response
        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        
        // Verify nameError, usernameError, and passwordError are set correctly
        assertEquals("Name cannot be empty", redirectAttributes.getFlashAttributes().get("nameError"));
        assertEquals("", redirectAttributes.getFlashAttributes().get("usernameError"));
        assertEquals("", redirectAttributes.getFlashAttributes().get("passwordError"));
        assertEquals("", redirectAttributes.getFlashAttributes().get("name"));
        assertEquals("AdminTest1", redirectAttributes.getFlashAttributes().get("username"));
        assertEquals("Newpassword@1", redirectAttributes.getFlashAttributes().get("password"));
    }
    
    @SuppressWarnings("unchecked")
	@Test
    @DisplayName("Test getAllAdmins method")
    public void testGetAllAdmins() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        // Create mock admin data
        Admin admin1 = new Admin();
        admin1.setName("Admin 1");
        Admin admin2 = new Admin();
        admin2.setName("Admin 2");
        List<Admin> adminList = Arrays.asList(admin1, admin2);

        // Mock AdminService behavior
        when(adminService.findAllAdmins(any(CustomPageable.class))).thenReturn(new AdminPage(adminList, 0, 10, adminList.size(), 1));

        // Call the controller method
        ModelAndView mav = adminController.getAllAdmins(0, 10, request, response);

        // Verify behavior
        assertEquals("viewAdmins", mav.getViewName());
        assertEquals(adminList, mav.getModel().get("admins"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }
    
    @SuppressWarnings("unchecked")
	@Test
    @DisplayName("Test filterAdmins method")
    public void testFilterAdmins() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock admin data
        Admin admin1 = new Admin();
        admin1.setName("Admin 1");
        Admin admin2 = new Admin();
        admin2.setName("Admin 2");
        List<Admin> adminList = Arrays.asList(admin1, admin2);

        // Mock AdminService behavior
        when(adminService.findAdminsByName(any(), any(CustomPageable.class)))
            .thenReturn(new AdminPage(adminList, 0, 10, adminList.size(), 1));

        // Call the controller method
        ModelAndView mav = adminController.filterAdmins("name", "John", 0, 10, request, response);

        // Verify behavior
        assertEquals("viewAdmins", mav.getViewName());
        assertEquals(adminList, mav.getModel().get("admins"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }
    
    @Test
    @DisplayName("Test filterAdmins method - Name Search")
    public void testFilterAdminsNameSearch() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock admin data
        Admin admin1 = new Admin();
        admin1.setName("John Doe");
        Admin admin2 = new Admin();
        admin2.setName("Jane Smith");
        List<Admin> adminList = Arrays.asList(admin1, admin2);

        // Mock AdminService behavior for name search
        when(adminService.findAdminsByName(any(), any(CustomPageable.class)))
            .thenReturn(new AdminPage(adminList, 0, 10, adminList.size(), 1));

        // Call the controller method for name search
        ModelAndView mav = adminController.filterAdmins("name", "John", 0, 10, request, response);

        // Verify behavior for name search
        assertEquals("viewAdmins", mav.getViewName());
        assertEquals(adminList, mav.getModel().get("admins"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }

    @Test
    @DisplayName("Test filterAdmins method - Username Search")
    public void testFilterAdminsUsernameSearch() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock admin data
        Admin admin1 = new Admin();
        admin1.setUsername("john123");
        Admin admin2 = new Admin();
        admin2.setUsername("jane456");
        List<Admin> adminList = Arrays.asList(admin1, admin2);

        // Mock AdminService behavior for username search
        when(adminService.findAdminsByUsername(any(), any(CustomPageable.class)))
            .thenReturn(new AdminPage(adminList, 0, 10, adminList.size(), 1));

        // Call the controller method for username search
        ModelAndView mav = adminController.filterAdmins("username", "john", 0, 10, request, response);

        // Verify behavior for username search
        assertEquals("viewAdmins", mav.getViewName());
        assertEquals(adminList, mav.getModel().get("admins"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }

    @Test
    @DisplayName("Test filterAdmins method - No Search Term")
    public void testFilterAdminsNoSearchTerm() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Call the controller method with empty search term
        ModelAndView mav = adminController.filterAdmins("name", "", 0, 10, request, response);

        // Verify behavior when no search term is provided
        assertEquals("redirect:/api/v1/admins/getAllAdmins", mav.getViewName());
    }

    @Test
    @DisplayName("Test filterAdmins method - Default Search")
    public void testFilterAdminsDefaultSearch() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock admin data
        Admin admin1 = new Admin();
        admin1.setName("John Doe");
        Admin admin2 = new Admin();
        admin2.setName("Jane Smith");
        List<Admin> adminList = Arrays.asList(admin1, admin2);

        // Mock AdminService behavior for default search
        when(adminService.findAllAdmins(any(CustomPageable.class)))
            .thenReturn(new AdminPage(adminList, 0, 10, adminList.size(), 1));

        // Call the controller method for default search
        ModelAndView mav = adminController.filterAdmins("other", "term", 0, 10, request, response);

        // Verify behavior for default search
        assertEquals("viewAdmins", mav.getViewName());
        assertEquals(adminList, mav.getModel().get("admins"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }
    @Test
    @DisplayName("Test goToUpdateAdminPage method - Admin found")
    public void testGoToUpdateAdminPageAdminFound() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Mock admin data
        Admin admin = new Admin();
        admin.setName("John Doe");
        admin.setAdminId(ObjectId.get());

        // Mock AdminService behavior
        when(adminService.findAdminById(any())).thenReturn(Optional.of(admin));

        // Call the controller method
        ModelAndView mav = adminController.goToUpdateAdminPage(admin.getAdminId(), request, response);

        // Verify behavior
        assertEquals("editAdmin", mav.getViewName());
        assertTrue(mav.getModel().containsKey("admin"));
        assertEquals(admin, mav.getModel().get("admin"));
    }

    @Test
    @DisplayName("Test goToUpdateAdminPage method - Admin not found")
    public void testGoToUpdateAdminPageAdminNotFound() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Mock AdminService behavior
        when(adminService.findAdminById(any())).thenReturn(Optional.empty());

        // Call the controller method
        ModelAndView mav = adminController.goToUpdateAdminPage(ObjectId.get(), request, response);

        // Verify behavior
        assertEquals("adminNotFound", mav.getViewName());
        assertTrue(mav.getModel().containsKey("errorMessage"));
        assertTrue(mav.getModel().get("errorMessage").toString().contains("Admin not found with adminId:"));
    }
    
    @Test
    @DisplayName("Test updateAdmin method - Success")
    public void testUpdateAdminSuccess() throws AdminNotFoundException, UsernameAlreadyExistException {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock admin data
        ObjectId adminId = ObjectId.get();
        String name = "John Doe";
        String username = "AdminTest2";
        String password = "Newpassword@1";

        // Mock AdminService behavior
        when(adminService.updateAdmin(any(), any())).thenReturn(new Admin());

        // Call the controller method
        ModelAndView mav = adminController.updateAdmin(adminId, name, username, password, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/admins/getAllAdmins?page=0&size=5", mav.getViewName());
        verify(redirectAttributes).addFlashAttribute("successMessage", "Admin with admin id " + adminId + " is successfully updated.");
        verify(response).setStatus(HttpServletResponse.SC_OK); // Ensure response status is set to OK
    }

    @Test
    @DisplayName("Test updateAdmin method - AdminNotFoundException")
    public void testUpdateAdminAdminNotFoundException() throws AdminNotFoundException, UsernameAlreadyExistException {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock admin data
        ObjectId adminId = ObjectId.get();
        String name = "John Doe";
        String username = "AdminTest2";
        String password = "Newpassword@1";

        // Mock AdminService behavior to throw AdminNotFoundException
        when(adminService.updateAdmin(any(), any())).thenThrow(new AdminNotFoundException("Admin not found"));

        // Call the controller method
        ModelAndView mav = adminController.updateAdmin(adminId, name, username, password, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("adminNotFound", mav.getViewName());
        assertEquals("Admin not found", mav.getModel().get("errorMessage"));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Test updateAdmin method - Normal Exception")
    public void testUpdateAdminNormalException() throws AdminNotFoundException, UsernameAlreadyExistException {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock admin data
        ObjectId adminId = ObjectId.get();
        String name = "John Doe";
        String username = "AdminTest2";
        String password = "Newpassword@1";

        // Mock AdminService behavior to throw a normal exception
        when(adminService.updateAdmin(any(), any())).thenThrow(new RuntimeException("Some unexpected error occurred"));

        // Call the controller method
        ModelAndView mav = adminController.updateAdmin(adminId, name, username, password, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("generalError", mav.getViewName());
        assertEquals("An error occurred while updating admin", mav.getModel().get("errorMessage"));
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    
    @Test
    @DisplayName("Test deleteAdmin method - Successful Deletion")
    public void testDeleteAdminSuccess() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock adminId
        ObjectId adminId = ObjectId.get();

        // Mock the returning of the Admin
        when(adminService.findAdminById(adminId)).thenReturn(Optional.of(new Admin()));

        // Set the referer header in the request mock
        when(request.getHeader("referer")).thenReturn("/api/v1/admins/getAllAdmins?page=1&size=5");

        // Call the controller method
        ModelAndView mav = adminController.deleteAdmin(adminId, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/admins/getAllAdmins?page=1&size=5", mav.getViewName());
        verify(redirectAttributes).addFlashAttribute("successMessage", "Admin deleted successfully.");
        verify(response).setStatus(HttpServletResponse.SC_OK); // Verify that setStatus is invoked with SC_OK
        verify(adminService).deleteAdmin(adminId);
        verify(redirectAttributes, never()).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Test deleteAdmin method - Admin Not Found")
    public void testDeleteAdminNotFound() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock adminId
        ObjectId adminId = ObjectId.get();
        
        // Set the referer header in the request mock
        when(request.getHeader("referer")).thenReturn("/api/v1/admins/getAllAdmins?page=1&size=5");

        // Mock adminService to return Optional.empty() for the admin
        when(adminService.findAdminById(adminId)).thenReturn(Optional.empty());
        
        // Call the controller method
        ModelAndView mav = adminController.deleteAdmin(adminId, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/admins/getAllAdmins?page=1&size=5", mav.getViewName());
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Admin not found with adminId: " + adminId);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(adminService, never()).deleteAdmin(adminId);
        verify(redirectAttributes, never()).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Test deleteAdmin method - Exception")
    public void testDeleteAdminException() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock adminId
        ObjectId adminId = ObjectId.get();

        // Mock the returning of the Admin
        when(adminService.findAdminById(adminId)).thenReturn(Optional.of(new Admin()));

        // Stub the referer header
        when(request.getHeader("referer")).thenReturn("/api/v1/admins/getAllAdmins?page=1&size=5");

        // Mock AdminService behavior to throw an exception with the correct message
        doThrow(new RuntimeException("Admin not found with adminId: " + adminId)).when(adminService).deleteAdmin(adminId);

        // Call the controller method
        ModelAndView mav = adminController.deleteAdmin(adminId, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/admins/getAllAdmins?page=1&size=5", mav.getViewName());
        verify(redirectAttributes).addFlashAttribute("errorMessage", "An error occurred while deleting admin: Admin not found with adminId: " + adminId);
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        
        // Verify that the deleteAdmin method is invoked with the correct adminId
        verify(adminService).deleteAdmin(adminId);
    }
}
