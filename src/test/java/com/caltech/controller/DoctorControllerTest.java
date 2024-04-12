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
import com.caltech.service.DoctorService;
import com.caltech.utils.CustomPageable;
import com.caltech.utils.DoctorPage;

@ExtendWith(MockitoExtension.class)
public class DoctorControllerTest {

    @Mock
	private DoctorService doctorService;
	
    @Mock
    private JwtGeneratorValidator jwtValidator;
    
    @InjectMocks
	private DoctorController doctorController;
    
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
        ModelAndView mav = doctorController.goToCreateDoctorPage(request, response);
        
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
        ModelAndView mav = doctorController.createDoctor(name, username, password, status, request, response, redirectAttributes);

        // Verify behavior
        verify(doctorService, times(1)).createDoctor(any(Doctor.class));
        assertEquals("redirect:/api/v1/doctors/getAllDoctors?page=0&size=5", mav.getViewName());

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
        DoctorController doctorController = new DoctorController(doctorService, jwtValidator);
        ModelAndView mav = doctorController.createDoctor(name, username, password, status, request, response, redirectAttributes);

        // Verify behavior
        verify(doctorService, never()).createDoctor(any(Doctor.class)); // Ensure createDoctor is not called
        assertEquals("redirect:/api/v1/doctors/createDoctor", mav.getViewName());
        
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
        DoctorController doctorController = new DoctorController(doctorService, jwtValidator);
        ModelAndView mav = doctorController.createDoctor(name, username, password, status, request, response, redirectAttributes);

        // Verify behavior
        verify(doctorService, never()).createDoctor(any(Doctor.class)); 
        assertEquals("redirect:/api/v1/doctors/createDoctor", mav.getViewName());
        
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
        ModelAndView mav = doctorController.getAllDoctors(0, 10, request, response);

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
        ModelAndView mav = doctorController.filterDoctors("name", "Doctor", 0, 10, request, response);

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
        ModelAndView mav = doctorController.filterDoctors("name", "Doctor", 0, 10, request, response);

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
        ModelAndView mav = doctorController.filterDoctors("username", "john", 0, 10, request, response);

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
        ModelAndView mav = doctorController.filterDoctors("status", "BUSY", 0, 10, request, response);

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
        ModelAndView mav = doctorController.filterDoctors("name", "", 0, 10, request, response);

        // Verify behavior when no search term is provided
        assertEquals("redirect:/api/v1/doctors/getAllDoctors", mav.getViewName());
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
        ModelAndView mav = doctorController.filterDoctors("other", "term", 0, 10, request, response);

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
        ModelAndView mav = doctorController.goToUpdateDoctorPage(doctor.getDoctorId(), request, response);

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
        ModelAndView mav = doctorController.goToUpdateDoctorPage(ObjectId.get(), request, response);

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
        ModelAndView mav = doctorController.updateDoctor(doctorId, name, username, password, status, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/doctors/getAllDoctors?page=0&size=5", mav.getViewName());
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
        ModelAndView mav = doctorController.updateDoctor(doctorId, name, username, password, status, request, response, redirectAttributes);

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
        ModelAndView mav = doctorController.updateDoctor(doctorId, name, username, password, status, request, response, redirectAttributes);

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
        when(request.getHeader("referer")).thenReturn("/api/v1/doctors/getAllDoctors?page=1&size=5");

        // Call the controller method
        ModelAndView mav = doctorController.deleteDoctor(doctorId, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/doctors/getAllDoctors?page=1&size=5", mav.getViewName());
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
        when(request.getHeader("referer")).thenReturn("/api/v1/doctors/getAllDoctors?page=1&size=5");

        // Mock doctorService to return Optional.empty() for the doctor
        when(doctorService.findDoctorById(doctorId)).thenReturn(Optional.empty());
        
        // Call the controller method
        ModelAndView mav = doctorController.deleteDoctor(doctorId, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/doctors/getAllDoctors?page=1&size=5", mav.getViewName());
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
        when(request.getHeader("referer")).thenReturn("/api/v1/doctors/getAllDoctors?page=1&size=5");

        // Mock the returning of the Doctor
        when(doctorService.findDoctorById(doctorId)).thenReturn(Optional.of(new Doctor()));

        // Mock DoctorService behavior to throw an exception with the correct message
        doThrow(new RuntimeException("Doctor not found with doctorId: " + doctorId)).when(doctorService).deleteDoctor(doctorId);

        // Call the controller method
        ModelAndView mav = doctorController.deleteDoctor(doctorId, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/doctors/getAllDoctors?page=1&size=5", mav.getViewName());
        verify(redirectAttributes).addFlashAttribute("errorMessage", "An error occurred while deleting doctor: Doctor not found with doctorId: " + doctorId);
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        
        // Verify that the deleteDoctor method is invoked with the correct doctorId
        verify(doctorService).deleteDoctor(doctorId);
    }
}
