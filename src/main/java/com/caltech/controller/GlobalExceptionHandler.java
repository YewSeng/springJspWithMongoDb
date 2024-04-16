package com.caltech.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.caltech.exception.AdminNotFoundException;
import com.caltech.exception.BookingNotFoundException;
import com.caltech.exception.DoctorNotFoundException;
import com.caltech.exception.PetNotFoundException;
import com.caltech.exception.UserNotFoundException;
import com.caltech.exception.UsernameAlreadyExistException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFoundException(UserNotFoundException ex) {
        ModelAndView mv = new ModelAndView("userNotFound");
        mv.addObject("errorMessage", ex.getMessage());
        return mv;
    }
    
    @ExceptionHandler(AdminNotFoundException.class)
    public ModelAndView handleAdminNotFoundException(AdminNotFoundException ex) {
        ModelAndView mv = new ModelAndView("adminNotFound");
        mv.addObject("errorMessage", ex.getMessage());
        return mv;
    }
    
    @ExceptionHandler(DoctorNotFoundException.class)
    public ModelAndView handleDoctorNotFoundException(DoctorNotFoundException ex) {
        ModelAndView mv = new ModelAndView("doctorNotFound");
        mv.addObject("errorMessage", ex.getMessage());
        return mv;
    }
    
    @ExceptionHandler(PetNotFoundException.class)
    public ModelAndView handlePetNotFoundException(PetNotFoundException ex) {
        ModelAndView mv = new ModelAndView("petNotFound");
        mv.addObject("errorMessage", ex.getMessage());
        return mv;
    }
    
    @ExceptionHandler(BookingNotFoundException.class)
    public ModelAndView handleBookingNotFoundException(BookingNotFoundException ex) {
        ModelAndView mv = new ModelAndView("bookingNotFound");
        mv.addObject("errorMessage", ex.getMessage());
        return mv;
    }
    
    @ExceptionHandler(UsernameAlreadyExistException.class)
    public ModelAndView handleUsernameAlreadyExistException(HttpServletRequest request, 
            HttpServletResponse response, RedirectAttributes redirectAttributes, UsernameAlreadyExistException ex) {
        log.error("Username is already taken. Please try again!");
        redirectAttributes.addFlashAttribute("errorMessage", "Username is already taken. Please try again!");
        redirectAttributes.addFlashAttribute("name", request.getParameter("name"));
        redirectAttributes.addFlashAttribute("username", request.getParameter("username"));
        redirectAttributes.addFlashAttribute("password", request.getParameter("password"));
        ModelAndView mv = new ModelAndView("redirect:/api/v1/users/createUser");
        response.setStatus(HttpServletResponse.SC_CONFLICT);
        return mv;
    }
    
    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneralException(Exception ex) {
        ModelAndView mv = new ModelAndView("generalError");
        mv.addObject("errorMessage", "An error occurred: " + ex.getMessage());
        return mv;
    }
    
}
