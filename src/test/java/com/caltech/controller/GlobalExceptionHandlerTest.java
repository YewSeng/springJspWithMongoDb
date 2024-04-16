package com.caltech.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;
import com.caltech.exception.AdminNotFoundException;
import com.caltech.exception.BookingNotFoundException;
import com.caltech.exception.DoctorNotFoundException;
import com.caltech.exception.PetNotFoundException;
import com.caltech.exception.UserNotFoundException;

@ExtendWith(MockitoExtension.class)
public class GlobalExceptionHandlerTest {

    @Test
    @DisplayName("Test handleUserNotFoundException method")
    public void testHandleUserNotFoundException() {
        // Create an instance of UserNotFoundException
        UserNotFoundException ex = new UserNotFoundException("User not found");

        // Call the handleUserNotFoundException method
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ModelAndView mv = handler.handleUserNotFoundException(ex);

        // Verify the ModelAndView
        assertEquals("userNotFound", mv.getViewName());
        assertEquals("User not found", mv.getModel().get("errorMessage"));
    }

    @Test
    @DisplayName("Test handleAdminNotFoundException method")
    public void testHandleAdminNotFoundException() {
        // Create an instance of AdminNotFoundException
        AdminNotFoundException ex = new AdminNotFoundException("Admin not found");

        // Call the handleAdminNotFoundException method
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ModelAndView mv = handler.handleAdminNotFoundException(ex);

        // Verify the ModelAndView
        assertEquals("adminNotFound", mv.getViewName());
        assertEquals("Admin not found", mv.getModel().get("errorMessage"));
    }
    @Test
    @DisplayName("Test handleDoctorNotFoundException method")
    public void testHandleDoctorNotFoundException() {
        // Create an instance of DoctorNotFoundException
    	DoctorNotFoundException ex = new DoctorNotFoundException("Doctor not found");

        // Call the handleDoctorNotFoundException method
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ModelAndView mv = handler.handleDoctorNotFoundException(ex);

        // Verify the ModelAndView
        assertEquals("doctorNotFound", mv.getViewName());
        assertEquals("Doctor not found", mv.getModel().get("errorMessage"));
    }
    
    @Test
    @DisplayName("Test handlePetNotFoundException method")
    public void testHandlePetNotFoundException() {
        // Create an instance of PetNotFoundException
    	PetNotFoundException ex = new PetNotFoundException("Pet not found");

        // Call the handlePetNotFoundException method
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ModelAndView mv = handler.handlePetNotFoundException(ex);

        // Verify the ModelAndView
        assertEquals("petNotFound", mv.getViewName());
        assertEquals("Pet not found", mv.getModel().get("errorMessage"));
    }
    
    @Test
    @DisplayName("Test handleBookingNotFoundException method")
    public void testHandleBookingNotFoundException() {
        // Create an instance of BookingNotFoundException
    	BookingNotFoundException ex = new BookingNotFoundException("Booking not found");

        // Call the handleBookingNotFoundException method
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ModelAndView mv = handler.handleBookingNotFoundException(ex);

        // Verify the ModelAndView
        assertEquals("bookingNotFound", mv.getViewName());
        assertEquals("Booking not found", mv.getModel().get("errorMessage"));
    }
    
    @Test
    @DisplayName("Test handleGeneralException method")
    public void testHandleGeneralException() {
        // Create an instance of Exception
        Exception ex = new Exception("General error");

        // Call the handleGeneralException method
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ModelAndView mv = handler.handleGeneralException(ex);

        // Verify the ModelAndView
        assertEquals("generalError", mv.getViewName());
        assertEquals("An error occurred: General error", mv.getModel().get("errorMessage"));
    }
}
