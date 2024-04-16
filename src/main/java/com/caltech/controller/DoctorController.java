package com.caltech.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.caltech.config.JwtGeneratorValidator;
import com.caltech.constants.Status;
import com.caltech.exception.DoctorNotFoundException;
import com.caltech.exception.UsernameAlreadyExistException;
import com.caltech.pojo.Doctor;
import com.caltech.service.DoctorService;
import com.caltech.utils.CustomPageable;
import com.caltech.utils.DoctorPage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api/v1/doctors")
public class DoctorController {

	private DoctorService doctorService;
	private JwtGeneratorValidator jwtValidator;
	private static final int DEFAULT_PAGE_SIZE = 5;
	
	@Autowired
	public DoctorController(DoctorService doctorService,JwtGeneratorValidator jwtValidator) {
		this.doctorService = doctorService;
		this.jwtValidator = jwtValidator;
	}
	
    private String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Name cannot be empty";
        } else if (name.trim().length() < 4) {
            return "Name must be at least 4 characters long";
        } else if (!name.matches("^[a-zA-Z ]{4,}$")) {
            return "Name must be at least 4 characters long and contain only letters";
        } else {
            return ""; // No error
        }
    }

    private String validateUsername(String username) {
        if (!username.matches("^D[a-zA-Z0-9]{7,}$")) {
            return "Username must start with an uppercase 'D' followed by at least 7 characters";
        } else {
            return ""; // No error
        }
    }

    private String validatePassword(String password) {
        if (!password.matches("^(?=.*[A-Z])(?=.*[@])(?=.*[0-9])[a-zA-Z0-9@]{8,}$")) {
            return "Password must be at least 8 characters long, contain at least 1 uppercase letter, 1 '@', and 1 number";
        } else {
            return ""; // No error
        }
    }
    
    private String validateStatus(String status) {
        if (status == null) {
            return "Status cannot be null";
        }
        return ""; // Return empty string if validation passes
    }
    
    private String validateForm(String name, String username, String password, String status) {
        // Regular expressions for validation
    	String nameRegex = "^[a-zA-Z ]{4,}$";
        String usernameRegex = "^D[a-zA-Z0-9]{7,}$"; // Username must start with "D" (upper case) and followed by be at least 7 characters and contain only letters and numbers
        String passwordRegex = "^(?=.*[A-Z])(?=.*[@])(?=.*[0-9])[a-zA-Z0-9@]{8,}$"; // Password: At least 1 uppercase, 1 "@", 1 number, At least length 8

        // Validation checks
        StringBuilder errorMessage = new StringBuilder();

        if (name == null || name.trim().isEmpty()) {
            errorMessage.append("Name cannot be empty\n");
        } else if (name.trim().length() < 4) {
            errorMessage.append("Name must be at least 4 characters long\n");
        } else if (!name.matches(nameRegex)) {
            errorMessage.append("Name must contain only letters\n");
        }
        if (!username.matches(usernameRegex)) {
            errorMessage.append("Username must start with an uppercase 'D' followed by at least 7 characters\n");
        }
        if (!password.matches(passwordRegex)) {
            errorMessage.append("Password must be at least 8 characters long, contain at least 1 uppercase letter, 1 '@', and 1 number\n");
        }
        if (status == null) {
            errorMessage.append("Status cannot be null\n");
        }

        return errorMessage.toString();
    }
    
    private int calculatePageNumberForDoctor(Doctor doctor) {
        int pageSize = DEFAULT_PAGE_SIZE;
        int pageNumber = 0;
        boolean doctorFound = false;

        while (!doctorFound) {
            // Fetch doctors for the current page number
            DoctorPage doctorPage = doctorService.findAllDoctors(new DoctorPage(pageNumber, pageSize));
            List<Doctor> doctors = doctorPage.getContent();

            // Check if the doctor is in the current page
            if (doctors.contains(doctor)) {
                doctorFound = true;
            } else {
                // If doctor not found, increment the page number
                pageNumber++;

                // Break the loop if we have reached the last page
                if (pageNumber >= doctorPage.getTotalPages()) {
                    pageNumber = -1; // Indicate doctor not found
                    break;
                }
            }
        }

        return pageNumber;
    }
    

    private String extractTokenFromRequest(HttpServletRequest request) {
        // Get the cookies from the request
        Cookie[] cookies = request.getCookies();
        
        // Check if cookies exist and look for the token cookie
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("token")) {
                    // Found the token cookie, return its value
                    return cookie.getValue();
                }
            }
        }
        
        // Token cookie not found
        return null;
    }
    
    @GetMapping("/home")
    public ModelAndView goToDoctorHomePage(HttpServletRequest request, HttpServletResponse response) {
        log.info("Entered into the /home request");
        ModelAndView mv = new ModelAndView();
        mv.setViewName("doctorHome");
        log.info("Went to doctorHome.jsp page");

        // Extract the token from the request cookie
        String token = extractTokenFromRequest(request);

        if (token != null) {
            // Parse the token to extract the username
            String username = jwtValidator.extractUsername(token);
            mv.addObject("username", username);
            // Set the response status to OK
            response.setStatus(HttpServletResponse.SC_OK);
        }

        if (jwtValidator.isTokenExpired(token)) {
        	mv.setViewName("redirect:");
        	response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
        return mv;
    }
    

}
