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
import java.util.stream.Collectors;
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
import com.caltech.constants.Status;
import com.caltech.exception.DoctorNotFoundException;
import com.caltech.exception.UserNotFoundException;
import com.caltech.exception.UsernameAlreadyExistException;
import com.caltech.pojo.Doctor;
import com.caltech.pojo.User;
import com.caltech.service.DoctorService;
import com.caltech.service.UserService;
import com.caltech.utils.CustomPageable;
import com.caltech.utils.DoctorPage;
import com.caltech.utils.UserPage;


@ExtendWith(MockitoExtension.class)
public class AdminControllerTest {

    @Mock
	private DoctorService doctorService;

    @Mock
	private UserService userService;
    
    @Mock
    private JwtGeneratorValidator jwtValidator;
    
    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test goToCreateDoctorPage method - Success")
    public void testGoToCreateDoctorPage() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        // Invoke the controller method
        ModelAndView mav = adminController.goToCreateDoctorPage(request, response);
        
        // Additional assertions
        assertEquals("createDoctor", mav.getViewName());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
    
    @Test
    @DisplayName("Test createDoctor method - Success")
    public void testCreateDoctorSuccess() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock doctor data
        String name = "John Doe";
        String username = "DoctorTest1";
        String password = "Newpassword@1";
		String status = String.valueOf(Status.AVAILABLE);

        // Mock DoctorService behavior
        when(doctorService.verifyExistingUsername(anyString())).thenReturn(false);

        // Invoke the controller method
        ModelAndView mav = adminController.createDoctor(name, username, password, status, request, response, redirectAttributes);

        // Verify behavior
        verify(doctorService, times(1)).createDoctor(any(Doctor.class));
        assertEquals("redirect:/api/v1/admins/getAllDoctors?page=0&size=5", mav.getViewName());

        // Additional assertions
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
    
    @Test
    @DisplayName("Test createDoctor method - Username already exists")
    public void testCreateDoctorUsernameAlreadyExists() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock doctor data
        String name = "John Doe";
        String username = "DoctorTest1";
        String password = "Newpassword@1";
		String status = String.valueOf(Status.AVAILABLE);
        
        // Mock DoctorService behavior to return true (username already exists)
        DoctorService doctorService = mock(DoctorService.class);
        when(doctorService.verifyExistingUsername(anyString())).thenReturn(true);

        // Invoke the controller method
        AdminController adminController = new AdminController(doctorService, userService, jwtValidator);
        ModelAndView mav = adminController.createDoctor(name, username, password, status, request, response, redirectAttributes);

        // Verify behavior
        verify(doctorService, never()).createDoctor(any(Doctor.class)); // Ensure createDoctor is not called
        assertEquals("redirect:/api/v1/admins/createDoctor", mav.getViewName());
        
        // Verify error message is set correctly
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Username is already taken. Please try again!");

        // Verify status code of the response
        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
    }
    
    @Test
    @DisplayName("Test createDoctor method - IllegalArgumentException")
    public void testCreateDoctorIllegalArgumentException() {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap(); // Use RedirectAttributesModelMap
        
        // Mock user data
        String name = "";
        String username = "DoctorTest1";
        String password = "Newpassword@1";
		String status = String.valueOf(Status.AVAILABLE);
        
        // Mock DoctorService behavior
        DoctorService doctorService = mock(DoctorService.class);

        // Invoke the controller method
        AdminController adminController = new AdminController(doctorService, userService, jwtValidator);
        ModelAndView mav = adminController.createDoctor(name, username, password, status, request, response, redirectAttributes);

        // Verify behavior
        verify(doctorService, never()).createDoctor(any(Doctor.class)); 
        assertEquals("redirect:/api/v1/admins/createDoctor", mav.getViewName());
        
        // Verify error message is set correctly
        assertEquals("Name cannot be empty", redirectAttributes.getFlashAttributes().get("errorMessage").toString().trim());

        // Verify status code of the response
        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        
        // Verify nameError, usernameError, and passwordError are set correctly
        assertEquals("Name cannot be empty", redirectAttributes.getFlashAttributes().get("nameError"));
        assertEquals("", redirectAttributes.getFlashAttributes().get("usernameError"));
        assertEquals("", redirectAttributes.getFlashAttributes().get("passwordError"));
        assertEquals("", redirectAttributes.getFlashAttributes().get("name"));
        assertEquals("DoctorTest1", redirectAttributes.getFlashAttributes().get("username"));
        assertEquals("Newpassword@1", redirectAttributes.getFlashAttributes().get("password"));
    }
    
    @SuppressWarnings("unchecked")
	@Test
    @DisplayName("Test getAllDoctors method")
    public void testGetAllDoctors() {
        // Create mock doctor data
        Doctor doctor1 = new Doctor();
        doctor1.setName("Doctor 1");
        Doctor doctor2 = new Doctor();
        doctor2.setName("Doctor 2");
        List<Doctor> doctorList = Arrays.asList(doctor1, doctor2);

        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        // Mock DoctorService behavior
        when(doctorService.findAllDoctors(any(CustomPageable.class))).thenReturn(new DoctorPage(doctorList, 0, 10, doctorList.size(), 1));

        // Call the controller method
        ModelAndView mav = adminController.getAllDoctors(0, 10, request, response);

        // Verify behavior
        assertEquals("viewDoctors", mav.getViewName());
        assertEquals(doctorList, mav.getModel().get("doctors"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }
    
	@Test
    @DisplayName("Test filterDoctors method")
    public void testFilterDoctors() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock doctor data
        Doctor doctor1 = new Doctor();
        doctor1.setName("Doctor 1");
        Doctor doctor2 = new Doctor();
        doctor2.setName("Doctor 2");
        List<Doctor> doctorList = Arrays.asList(doctor1, doctor2);

        // Mock DoctorService behavior
        when(doctorService.findDoctorsByName(any(), any(CustomPageable.class)))
            .thenReturn(new DoctorPage(doctorList, 0, 10, doctorList.size(), 1));

        // Call the controller method
        ModelAndView mav = adminController.filterDoctors("name", "Doctor", 0, 10, request, response);

        // Verify behavior
        assertEquals("viewDoctors", mav.getViewName());
        assertEquals(doctorList, mav.getModel().get("doctors"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }
	
    @Test
    @DisplayName("Test filterDoctors method - Name Search")
    public void testFilterDoctorsNameSearch() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock doctor data
        Doctor doctor1 = new Doctor();
        doctor1.setName("Doctor 1");
        Doctor doctor2 = new Doctor();
        doctor2.setName("Doctor 2");
        List<Doctor> doctorList = Arrays.asList(doctor1, doctor2);

        // Mock DoctorService behavior for name search
        when(doctorService.findDoctorsByName(any(), any(CustomPageable.class)))
            .thenReturn(new DoctorPage(doctorList, 0, 10, doctorList.size(), 1));

        // Call the controller method for name search
        ModelAndView mav = adminController.filterDoctors("name", "Doctor", 0, 10, request, response);

        // Verify behavior for name search
        assertEquals("viewDoctors", mav.getViewName());
        assertEquals(doctorList, mav.getModel().get("doctors"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }
    
    @Test
    @DisplayName("Test filterDoctors method - Username Search")
    public void testFilterDoctorsUsernameSearch() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock doctor data
        Doctor doctor1 = new Doctor();
        doctor1.setUsername("Doctor 1");
        Doctor doctor2 = new Doctor();
        doctor2.setUsername("Doctor 2");
        List<Doctor> doctorList = Arrays.asList(doctor1, doctor2);
		
        // Mock DoctorService behavior for username search
        when(doctorService.findDoctorsByUsername(any(), any(CustomPageable.class)))
            .thenReturn(new DoctorPage(doctorList, 0, 10, doctorList.size(), 1));

        // Call the controller method for username search
        ModelAndView mav = adminController.filterDoctors("username", "john", 0, 10, request, response);

        // Verify behavior for username search
        assertEquals("viewDoctors", mav.getViewName());
        assertEquals(doctorList, mav.getModel().get("doctors"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }
    
    @Test
    @DisplayName("Test filterDoctors method - Status Search")
    public void testFilterDoctorsStatusSearch() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock doctor data
        Doctor doctor1 = new Doctor();
        doctor1.setStatus(Status.AVAILABLE);
        Doctor doctor2 = new Doctor();
        doctor2.setStatus(Status.AVAILABLE);
        Doctor doctor3 = new Doctor();
        doctor3.setStatus(Status.BUSY);
        List<Doctor> doctorList = Arrays.asList(doctor1, doctor2, doctor3);
		
        List<Doctor> doctorResults = doctorList.stream()
        		.filter(doctor -> doctor.getStatus().equals(Status.BUSY))
        		.collect(Collectors.toList());
        
        // Mock DoctorService behavior for status search
        when(doctorService.findDoctorsByStatus(any(), any(CustomPageable.class)))
            .thenReturn(new DoctorPage(doctorResults, 0, 10, doctorResults.size(), 1));

        // Call the controller method for status search
        ModelAndView mav = adminController.filterDoctors("status", "BUSY", 0, 10, request, response);

        // Verify behavior for username search
        assertEquals("viewDoctors", mav.getViewName());
        assertEquals(doctorResults, mav.getModel().get("doctors"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(1L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }
    
    @Test
    @DisplayName("Test filterDoctors method - No Search Term")
    public void testFilterDoctorsNoSearchTerm() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Call the controller method with empty search term
        ModelAndView mav = adminController.filterDoctors("name", "", 0, 10, request, response);

        // Verify behavior when no search term is provided
        assertEquals("redirect:/api/v1/admins/getAllDoctors", mav.getViewName());
    }
    
    @Test
    @DisplayName("Test filterDoctors method - Default Search")
    public void testFilterDoctorsDefaultSearch() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock doctor data
        Doctor doctor1 = new Doctor();
        doctor1.setName("Doctor 1");
        Doctor doctor2 = new Doctor();
        doctor2.setName("Doctor 2");
        List<Doctor> doctorList = Arrays.asList(doctor1, doctor2);

        // Mock DoctorService behavior for default search
        when(doctorService.findAllDoctors(any(CustomPageable.class)))
            .thenReturn(new DoctorPage(doctorList, 0, 10, doctorList.size(), 1));

        // Call the controller method for default search
        ModelAndView mav = adminController.filterDoctors("other", "term", 0, 10, request, response);

        // Verify behavior for default search
        assertEquals("viewDoctors", mav.getViewName());
        assertEquals(doctorList, mav.getModel().get("doctors"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }
    
    @Test
    @DisplayName("Test goToUpdateDoctorPage method - Doctor found")
    public void testGoToUpdateDoctorPageDoctorFound() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Mock doctor data
        Doctor doctor = new Doctor();
        doctor.setName("John Doe");
        doctor.setDoctorId(ObjectId.get());

        // Mock DoctorService behavior
        when(doctorService.findDoctorById(any())).thenReturn(Optional.of(doctor));

        // Call the controller method
        ModelAndView mav = adminController.goToUpdateDoctorPage(doctor.getDoctorId(), request, response);

        // Verify behavior
        assertEquals("editDoctor", mav.getViewName());
        assertTrue(mav.getModel().containsKey("doctor"));
        assertEquals(doctor, mav.getModel().get("doctor"));
    }
    
    @Test
    @DisplayName("Test goToUpdateDoctorPage method - Doctor not found")
    public void testGoToUpdateDoctorPageDoctorNotFound() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Mock DoctorService behavior
        when(doctorService.findDoctorById(any())).thenReturn(Optional.empty());

        // Call the controller method
        ModelAndView mav = adminController.goToUpdateDoctorPage(ObjectId.get(), request, response);

        // Verify behavior
        assertEquals("doctorNotFound", mav.getViewName());
        assertTrue(mav.getModel().containsKey("errorMessage"));
        assertTrue(mav.getModel().get("errorMessage").toString().contains("Doctor not found with doctorId:"));
    }
    
    @Test
    @DisplayName("Test updateDoctor method - Success")
    public void testUpdateDoctorSuccess() throws DoctorNotFoundException, UsernameAlreadyExistException {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock doctor data
        ObjectId doctorId = ObjectId.get();
        String name = "John Doe";
        String username = "DoctorTest2";
        String password = "Newpassword@1";
		String status = String.valueOf(Status.AVAILABLE);

        // Mock DoctorService behavior
        when(doctorService.updateDoctor(any(), any())).thenReturn(new Doctor());

        // Call the controller method
        ModelAndView mav = adminController.updateDoctor(doctorId, name, username, password, status, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/admins/getAllDoctors?page=0&size=5", mav.getViewName());
        verify(redirectAttributes).addFlashAttribute("successMessage", "Doctor with doctor id " + doctorId + " is successfully updated.");
        verify(response).setStatus(HttpServletResponse.SC_OK); // Ensure response status is set to OK
    }
    
    @Test
    @DisplayName("Test updateDoctor method - DoctorNotFoundException")
    public void testUpdateDoctorDoctorNotFoundException() throws DoctorNotFoundException, UsernameAlreadyExistException {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock doctor data
        ObjectId doctorId = ObjectId.get();
        String name = "John Doe";
        String username = "DoctorTest2";
        String password = "Newpassword@1";
		String status = String.valueOf(Status.AVAILABLE);

        // Mock DoctorService behavior to throw DoctorNotFoundException
        when(doctorService.updateDoctor(any(), any())).thenThrow(new DoctorNotFoundException("Doctor not found"));

        // Call the controller method
        ModelAndView mav = adminController.updateDoctor(doctorId, name, username, password, status, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("doctorNotFound", mav.getViewName());
        assertEquals("Doctor not found", mav.getModel().get("errorMessage"));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }
    
    @Test
    @DisplayName("Test updateDoctor method - Normal Exception")
    public void testUpdateDoctorNormalException() throws DoctorNotFoundException, UsernameAlreadyExistException {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock doctor data
        ObjectId doctorId = ObjectId.get();
        String name = "John Doe";
        String username = "DoctorTest2";
        String password = "Newpassword@1";
		String status = String.valueOf(Status.AVAILABLE);

        // Mock DoctorService behavior to throw a normal exception
        when(doctorService.updateDoctor(any(), any())).thenThrow(new RuntimeException("Some unexpected error occurred"));

        // Call the controller method
        ModelAndView mav = adminController.updateDoctor(doctorId, name, username, password, status, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("generalError", mav.getViewName());
        assertEquals("An error occurred while updating doctor", mav.getModel().get("errorMessage"));
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    
    @Test
    @DisplayName("Test deleteDoctor method - Successful Deletion")
    public void testDeleteDoctorSuccess() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock doctorId
        ObjectId doctorId = ObjectId.get();

        // Mock the returning of the Doctor
        when(doctorService.findDoctorById(doctorId)).thenReturn(Optional.of(new Doctor()));

        // Set the referer header in the request mock
        when(request.getHeader("referer")).thenReturn("/api/v1/admins/getAllDoctors?page=1&size=5");

        // Call the controller method
        ModelAndView mav = adminController.deleteDoctor(doctorId, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/admins/getAllDoctors?page=1&size=5", mav.getViewName());
        verify(redirectAttributes).addFlashAttribute("successMessage", "Doctor deleted successfully.");
        verify(response).setStatus(HttpServletResponse.SC_OK); // Verify that setStatus is invoked with SC_OK
        verify(doctorService).deleteDoctor(doctorId);
        verify(redirectAttributes, never()).addFlashAttribute(eq("errorMessage"), anyString());
    }
    
    @Test
    @DisplayName("Test deleteDoctor method - Doctor Not Found")
    public void testDeleteDoctorNotFound() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock doctorId
        ObjectId doctorId = ObjectId.get();
        
        // Set the referer header in the request mock
        when(request.getHeader("referer")).thenReturn("/api/v1/admins/getAllDoctors?page=1&size=5");

        // Mock doctorService to return Optional.empty() for the doctor
        when(doctorService.findDoctorById(doctorId)).thenReturn(Optional.empty());
        
        // Call the controller method
        ModelAndView mav = adminController.deleteDoctor(doctorId, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/admins/getAllDoctors?page=1&size=5", mav.getViewName());
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Doctor not found with doctorId: " + doctorId);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(doctorService, never()).deleteDoctor(doctorId);
        verify(redirectAttributes, never()).addFlashAttribute(eq("successMessage"), anyString());
    }
    
    @Test
    @DisplayName("Test deleteDoctor method - Exception")
    public void testDeleteDoctorException() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock doctorId
        ObjectId doctorId = ObjectId.get();
        
        // Set the referer header in the request mock
        when(request.getHeader("referer")).thenReturn("/api/v1/admins/getAllDoctors?page=1&size=5");

        // Mock the returning of the Doctor
        when(doctorService.findDoctorById(doctorId)).thenReturn(Optional.of(new Doctor()));

        // Mock DoctorService behavior to throw an exception with the correct message
        doThrow(new RuntimeException("Doctor not found with doctorId: " + doctorId)).when(doctorService).deleteDoctor(doctorId);

        // Call the controller method
        ModelAndView mav = adminController.deleteDoctor(doctorId, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/admins/getAllDoctors?page=1&size=5", mav.getViewName());
        verify(redirectAttributes).addFlashAttribute("errorMessage", "An error occurred while deleting doctor: Doctor not found with doctorId: " + doctorId);
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        
        // Verify that the deleteDoctor method is invoked with the correct doctorId
        verify(doctorService).deleteDoctor(doctorId);
    }
    
    @Test
    @DisplayName("Test goToCreateUserPage method - Success")
    public void testGoToCreateUserPage() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        // Invoke the controller method
        ModelAndView mav = adminController.goToCreateUserPage(request, response);
        
        // Additional assertions
        assertEquals("createUser", mav.getViewName());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
    
    @Test
    @DisplayName("Test createUser method - Success")
    public void testCreateUserSuccess() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock user data
        String name = "John Doe";
        String username = "UserTest1";
        String password = "Newpassword@1";

        // Mock UserService behavior
        when(userService.verifyExistingUsername(anyString())).thenReturn(false);

        // Invoke the controller method
        ModelAndView mav = adminController.createUser(name, username, password, request, response, redirectAttributes);

        // Verify behavior
        verify(userService, times(1)).createUser(any(User.class));
        assertEquals("redirect:/api/v1/admins/getAllUsers?page=0&size=5", mav.getViewName());

        // Additional assertions
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Test createUser method - Username already exists")
    public void testCreateUserUsernameAlreadyExists() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock user data
        String name = "John Doe";
        String username = "UserTest1";
        String password = "Newpassword@1";
        
        // Mock UserService behavior to return true (username already exists)
        UserService userService = mock(UserService.class);
        when(userService.verifyExistingUsername(anyString())).thenReturn(true);

        // Invoke the controller method
        AdminController adminController = new AdminController(doctorService, userService, jwtValidator);
        ModelAndView mav = adminController.createUser(name, username, password, request, response, redirectAttributes);

        // Verify behavior
        verify(userService, never()).createUser(any(User.class)); // Ensure createUser is not called
        assertEquals("redirect:/api/v1/admins/createUser", mav.getViewName());
        
        // Verify error message is set correctly
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Username is already taken. Please try again!");

        // Verify status code of the response
        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
    }
    
    @Test
    @DisplayName("Test createUser method - IllegalArgumentException")
    public void testCreateUserIllegalArgumentException() {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap(); // Use RedirectAttributesModelMap
        
        // Mock user data
        String name = "";
        String username = "UserTest1";
        String password = "Newpassword@1";
        
        // Mock UserService behavior
        UserService userService = mock(UserService.class);

        // Invoke the controller method
        AdminController adminController = new AdminController(doctorService, userService, jwtValidator);
        ModelAndView mav = adminController.createUser(name, username, password, request, response, redirectAttributes);

        // Verify behavior
        verify(userService, never()).createUser(any(User.class)); 
        assertEquals("redirect:/api/v1/admins/createUser", mav.getViewName());
        
        // Verify error message is set correctly
        assertEquals("Name cannot be empty", redirectAttributes.getFlashAttributes().get("errorMessage").toString().trim());

        // Verify status code of the response
        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        
        // Verify nameError, usernameError, and passwordError are set correctly
        assertEquals("Name cannot be empty", redirectAttributes.getFlashAttributes().get("nameError"));
        assertEquals("", redirectAttributes.getFlashAttributes().get("usernameError"));
        assertEquals("", redirectAttributes.getFlashAttributes().get("passwordError"));
        assertEquals("", redirectAttributes.getFlashAttributes().get("name"));
        assertEquals("UserTest1", redirectAttributes.getFlashAttributes().get("username"));
        assertEquals("Newpassword@1", redirectAttributes.getFlashAttributes().get("password"));
    }
    
    @SuppressWarnings("unchecked")
	@Test
    @DisplayName("Test getAllUsers method")
    public void testGetAllUsers() {
        // Create mock user data
        User user1 = new User();
        user1.setName("User 1");
        User user2 = new User();
        user2.setName("User 2");
        List<User> userList = Arrays.asList(user1, user2);

        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        // Mock UserService behavior
        when(userService.findAllUsers(any(CustomPageable.class))).thenReturn(new UserPage(userList, 0, 10, userList.size(), 1));

        // Call the controller method
        ModelAndView mav = adminController.getAllUsers(0, 10, request, response);

        // Verify behavior
        assertEquals("viewUsers", mav.getViewName());
        assertEquals(userList, mav.getModel().get("users"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }

    
    @SuppressWarnings("unchecked")
	@Test
    @DisplayName("Test filterUsers method")
    public void testFilterUsers() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock user data
        User user1 = new User();
        user1.setName("User 1");
        User user2 = new User();
        user2.setName("User 2");
        List<User> userList = Arrays.asList(user1, user2);

        // Mock UserService behavior
        when(userService.findUsersByName(any(), any(CustomPageable.class)))
            .thenReturn(new UserPage(userList, 0, 10, userList.size(), 1));

        // Call the controller method
        ModelAndView mav = adminController.filterUsers("name", "John", 0, 10, request, response);

        // Verify behavior
        assertEquals("viewUsers", mav.getViewName());
        assertEquals(userList, mav.getModel().get("users"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }
    
    @Test
    @DisplayName("Test filterUsers method - Name Search")
    public void testFilterUsersNameSearch() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock user data
        User user1 = new User();
        user1.setName("John Doe");
        User user2 = new User();
        user2.setName("Jane Smith");
        List<User> userList = Arrays.asList(user1, user2);

        // Mock UserService behavior for name search
        when(userService.findUsersByName(any(), any(CustomPageable.class)))
            .thenReturn(new UserPage(userList, 0, 10, userList.size(), 1));

        // Call the controller method for name search
        ModelAndView mav = adminController.filterUsers("name", "John", 0, 10, request, response);

        // Verify behavior for name search
        assertEquals("viewUsers", mav.getViewName());
        assertEquals(userList, mav.getModel().get("users"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }

    @Test
    @DisplayName("Test filterUsers method - Username Search")
    public void testFilterUsersUsernameSearch() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock user data
        User user1 = new User();
        user1.setUsername("john123");
        User user2 = new User();
        user2.setUsername("jane456");
        List<User> userList = Arrays.asList(user1, user2);

        // Mock UserService behavior for username search
        when(userService.findUsersByUsername(any(), any(CustomPageable.class)))
            .thenReturn(new UserPage(userList, 0, 10, userList.size(), 1));

        // Call the controller method for username search
        ModelAndView mav = adminController.filterUsers("username", "john", 0, 10, request, response);

        // Verify behavior for username search
        assertEquals("viewUsers", mav.getViewName());
        assertEquals(userList, mav.getModel().get("users"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }

    @Test
    @DisplayName("Test filterUsers method - No Search Term")
    public void testFilterUsersNoSearchTerm() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Call the controller method with empty search term
        ModelAndView mav = adminController.filterUsers("name", "", 0, 10, request, response);

        // Verify behavior when no search term is provided
        assertEquals("redirect:/api/v1/admins/getAllUsers", mav.getViewName());
    }

    @Test
    @DisplayName("Test filterUsers method - Default Search")
    public void testFilterUsersDefaultSearch() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock user data
        User user1 = new User();
        user1.setName("John Doe");
        User user2 = new User();
        user2.setName("Jane Smith");
        List<User> userList = Arrays.asList(user1, user2);

        // Mock UserService behavior for default search
        when(userService.findAllUsers(any(CustomPageable.class)))
            .thenReturn(new UserPage(userList, 0, 10, userList.size(), 1));

        // Call the controller method for default search
        ModelAndView mav = adminController.filterUsers("other", "term", 0, 10, request, response);

        // Verify behavior for default search
        assertEquals("viewUsers", mav.getViewName());
        assertEquals(userList, mav.getModel().get("users"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }
    
    @Test
    @DisplayName("Test goToUpdateUserPage method - User found")
    public void testGoToUpdateUserPageUserFound() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Mock user data
        User user = new User();
        user.setName("John Doe");
        user.setUserId(ObjectId.get());

        // Mock UserService behavior
        when(userService.findUserById(any())).thenReturn(Optional.of(user));

        // Call the controller method
        ModelAndView mav = adminController.goToUpdateUserPage(user.getUserId(), request, response);

        // Verify behavior
        assertEquals("editUser", mav.getViewName());
        assertTrue(mav.getModel().containsKey("user"));
        assertEquals(user, mav.getModel().get("user"));
    }

    @Test
    @DisplayName("Test goToUpdateUserPage method - User not found")
    public void testGoToUpdateUserPageUserNotFound() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Mock UserService behavior
        when(userService.findUserById(any())).thenReturn(Optional.empty());

        // Call the controller method
        ModelAndView mav = adminController.goToUpdateUserPage(ObjectId.get(), request, response);

        // Verify behavior
        assertEquals("userNotFound", mav.getViewName());
        assertTrue(mav.getModel().containsKey("errorMessage"));
        assertTrue(mav.getModel().get("errorMessage").toString().contains("User not found with userId:"));
    }
    
    @Test
    @DisplayName("Test updateUser method - Success")
    public void testUpdateUserSuccess() throws UserNotFoundException, UsernameAlreadyExistException {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock user data
        ObjectId userId = ObjectId.get();
        String name = "John Doe";
        String username = "UserTest2";
        String password = "Newpassword@1";

        // Mock UserService behavior
        when(userService.updateUser(any(), any())).thenReturn(new User());

        // Call the controller method
        ModelAndView mav = adminController.updateUser(userId, name, username, password, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/admins/getAllUsers?page=0&size=5", mav.getViewName());
        verify(redirectAttributes).addFlashAttribute("successMessage", "User with user id " + userId + " is successfully updated.");
        verify(response).setStatus(HttpServletResponse.SC_OK); // Ensure response status is set to OK
    }

    @Test
    @DisplayName("Test updateUser method - UserNotFoundException")
    public void testUpdateUserUserNotFoundException() throws UserNotFoundException, UsernameAlreadyExistException {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock user data
        ObjectId userId = ObjectId.get();
        String name = "John Doe";
        String username = "UserTest2";
        String password = "Newpassword@1";

        // Mock UserService behavior to throw UserNotFoundException
        when(userService.updateUser(any(), any())).thenThrow(new UserNotFoundException("User not found"));

        // Call the controller method
        ModelAndView mav = adminController.updateUser(userId, name, username, password, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("userNotFound", mav.getViewName());
        assertEquals("User not found", mav.getModel().get("errorMessage"));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Test updateUser method - Normal Exception")
    public void testUpdateUserNormalException() throws UserNotFoundException, UsernameAlreadyExistException {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock user data
        ObjectId userId = ObjectId.get();
        String name = "John Doe";
        String username = "UserTest2";
        String password = "Newpassword@1";

        // Mock UserService behavior to throw a normal exception
        when(userService.updateUser(any(), any())).thenThrow(new RuntimeException("Some unexpected error occurred"));

        // Call the controller method
        ModelAndView mav = adminController.updateUser(userId, name, username, password, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("generalError", mav.getViewName());
        assertEquals("An error occurred while updating user", mav.getModel().get("errorMessage"));
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    
    @Test
    @DisplayName("Test deleteUser method - Successful Deletion")
    public void testDeleteUserSuccess() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock userId
        ObjectId userId = ObjectId.get();

        // Mock the returning of the User
        when(userService.findUserById(userId)).thenReturn(Optional.of(new User()));

        // Set the referer header in the request mock
        when(request.getHeader("referer")).thenReturn("/api/v1/admins/getAllUsers?page=1&size=5");

        // Call the controller method
        ModelAndView mav = adminController.deleteUser(userId, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/admins/getAllUsers?page=1&size=5", mav.getViewName());
        verify(redirectAttributes).addFlashAttribute("successMessage", "User deleted successfully.");
        verify(response).setStatus(HttpServletResponse.SC_OK); // Verify that setStatus is invoked with SC_OK
        verify(userService).deleteUser(userId);
        verify(redirectAttributes, never()).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Test deleteUser method - User Not Found")
    public void testDeleteUserNotFound() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock userId
        ObjectId userId = ObjectId.get();
        
        // Set the referer header in the request mock
        when(request.getHeader("referer")).thenReturn("/api/v1/admins/getAllUsers?page=1&size=5");

        // Mock userService to return Optional.empty() for the user
        when(userService.findUserById(userId)).thenReturn(Optional.empty());
        
        // Call the controller method
        ModelAndView mav = adminController.deleteUser(userId, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/admins/getAllUsers?page=1&size=5", mav.getViewName());
        verify(redirectAttributes).addFlashAttribute("errorMessage", "User not found with userId: " + userId);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(userService, never()).deleteUser(userId);
        verify(redirectAttributes, never()).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Test deleteUser method - Exception")
    public void testDeleteUserException() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock userId
        ObjectId userId = ObjectId.get();

        // Mock the returning of the User
        when(userService.findUserById(userId)).thenReturn(Optional.of(new User()));

        // Stub the referer header
        when(request.getHeader("referer")).thenReturn("/api/v1/admins/getAllUsers?page=1&size=5");

        // Mock UserService behavior to throw an exception with the correct message
        doThrow(new RuntimeException("User not found with userId: " + userId)).when(userService).deleteUser(userId);

        // Call the controller method
        ModelAndView mav = adminController.deleteUser(userId, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/admins/getAllUsers?page=1&size=5", mav.getViewName());
        verify(redirectAttributes).addFlashAttribute("errorMessage", "An error occurred while deleting user: User not found with userId: " + userId);
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        
        // Verify that the deleteUser method is invoked with the correct userId
        verify(userService).deleteUser(userId);
    }
}
