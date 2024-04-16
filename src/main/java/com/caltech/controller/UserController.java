package com.caltech.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
import com.caltech.constants.Species;
import com.caltech.constants.Status;
import com.caltech.exception.UserNotFoundException;
import com.caltech.exception.UsernameAlreadyExistException;
import com.caltech.pojo.Pet;
import com.caltech.pojo.User;
import com.caltech.service.BookingService;
import com.caltech.service.PetService;
import com.caltech.service.UserService;
import com.caltech.utils.UserPage;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;

import com.caltech.utils.CustomPageable;
import com.caltech.utils.PetPage;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api/v1/users")
public class UserController {
	
	private UserService userService;
	private PetService petService;
	private BookingService bookingService;
	private JwtGeneratorValidator jwtValidator;
	private static final int DEFAULT_PAGE_SIZE = 5;
	
    @Value("${jwt.secretKey}")
    private String SECRET;
    
    @Autowired
    public UserController(UserService userService, PetService petService, BookingService bookingService, JwtGeneratorValidator jwtValidator) {
        this.userService = userService;
        this.petService = petService;
        this.bookingService = bookingService;
        this.jwtValidator = jwtValidator;
    }
    
    private String validatePetName(String petName) {
        if (petName == null || petName.trim().isEmpty()) {
            return "Pet Name cannot be empty";
        } else if (petName.trim().length() < 4) {
            return "Pet Name must be at least 4 characters long";
        } else if (!petName.matches("^[a-zA-Z ]{4,}$")) {
            return "Pet Name must be at least 4 characters long and contain only letters";
        } else {
            return ""; // No error
        }
    }

    private String validatePetAge(String petAge) {
        if (petAge == null || petAge.trim().isEmpty()) {
            return "Age cannot be empty";
        }
        if (!petAge.matches("^\\d+(\\.\\d+)?$")) {
            return "Age must contain only digits (allowed decimal)";
        }
        double age = Double.parseDouble(petAge);
        if (age < 0) {
            return "Age cannot be negative";
        } else if (age == 0) {
        	return "Age cannot be 0";
        } else if (age > 20) {
        	return "Age cannot be above 20";
        }
        return "";
    }
    
    private String validatePetType(String petType) {
    	if (petType == null || petType.trim().isEmpty()) {
    		return "Pet Type cannot be null";
    	}
    	return "";
    }
    
    private String validateUserId(String userId) {
    	if (userId == null || userId.trim().isEmpty()) {
    		return "User Id cannot be null";
    	}
    	return "";
    }
    
    private String validatePetForm(String petName, String petType, String petAge, String userId) {
        // Regular expressions for validation
    	String petNameRegex = "^[a-zA-Z ]{4,}$";
    	String petAgeRegex = "^\\d+(\\.\\d+)?$";

        // Validation checks
        StringBuilder errorMessage = new StringBuilder();

        if (petName == null || petName.trim().isEmpty()) {
            errorMessage.append("Pet Name cannot be empty\n");
        } else if (petName.trim().length() < 4) {
            errorMessage.append("Pet Name must be at least 4 characters long\n");
        } else if (!petName.matches(petNameRegex)) {
            errorMessage.append("Pet Name must contain only letters\n");
        }
        if (petAge == null || petAge.trim().isEmpty()) {
            errorMessage.append("Pet Age cannot be null\n");
        } else if (!petAge.matches(petAgeRegex)) {
        	errorMessage.append("Pet Age must contain only digits (allowed decimal)\n");
        }
        double age = Double.parseDouble(petAge);
        if (age < 0) {
        	errorMessage.append("Pet Age cannot be negative\n");
        } else if (age == 0) {
        	errorMessage.append("Pet Age cannot be 0\n");
        } else if (age > 20) {
        	errorMessage.append("Pet Age cannot be above 20\n");
        }
        if (userId == null || userId.trim().isEmpty()) {
        	errorMessage.append("User Id cannot be null\n");
        }
        if (petType == null || petType.trim().isEmpty()) {
            errorMessage.append("Pet Type cannot be null\n");
        }

        return errorMessage.toString();
    }
    
    private int calculatePageNumberForPetOwners(ObjectId userId, Pet pet) {
        int pageSize = DEFAULT_PAGE_SIZE;
        int pageNumber = 0;
        boolean petFound = false;

        while (!petFound) {
            // Fetch pets for the current page number
            PetPage petPage = petService.findPetsByUserId(userId, new PetPage(pageNumber, pageSize));
            List<Pet> pets = petPage.getContent();

            // Check if the pet is in the current page
            if (pets.contains(pet)) {
                petFound = true;
            } else {
                // If pet not found, increment the page number
                pageNumber++;

                // Break the loop if we have reached the last page
                if (pageNumber >= petPage.getTotalPages()) {
                    pageNumber = -1; // Indicate pet not found
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

    private boolean isUser(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            try {
                // Parse the token to extract claims
                Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
                
                // Check if the token contains the "role" claim and its value is "ROLE_USER"
                if (claims.containsKey("role")) {
                    String role = (String) claims.get("role");
                    log.info("Role: {}", role);
                    return role.equals("ROLE_USER");
                }
            } catch (JwtException e) {
                // Handle JWT parsing exceptions
                log.error("Error parsing JWT token: {}", e.getMessage());
                return false;
            }
        }
        return false;
    }
    
    @GetMapping("/home")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ModelAndView goToUserHomePage(HttpServletRequest request, HttpServletResponse response) {
        log.info("Entered into the /home request");
        ModelAndView mv = new ModelAndView();
        
        // Check if the user is authorized
        if (!isUser(request)) {
            // Invalidate session and clear tokens
            request.getSession().invalidate();
            response.setHeader("Authorization", "");
            // Redirect to index page with error message
            mv.setViewName("redirect:");
            mv.addObject("errorMessage", "Unauthorized access. Please login again.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return mv;
        }        

        // Extract the token from the request cookie
        String token = extractTokenFromRequest(request);
        log.info("Extracted token from request: {}", token);
        mv.setViewName("userHome");
        log.info("Went to userHome.jsp page");

        if (token != null) {
            // Parse the token to extract the username
            String username = jwtValidator.extractUsername(token);
            mv.addObject("username", username);
            Optional<User> userOptional = userService.findUserByUsername(username);
            if (userOptional.isPresent()) {
            	User user = userOptional.get();
            	ObjectId userId = user.getUserId();
            	mv.addObject("userId", userId);
            }
        }

        // Set the response status to OK
        response.setStatus(HttpServletResponse.SC_OK);
        return mv;
    }

	@GetMapping("/createPet/{userId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ModelAndView goToCreatePetPage(@PathVariable ObjectId userId, HttpServletRequest request, HttpServletResponse response) {
		log.info("Entered into the /createPet/{userId} request");
		ModelAndView mv = new ModelAndView();
		mv.setViewName("createPet");
		log.info("Went to createPet.jsp page");
	    // Get flash attributes and add them to the model
	    Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
	    if (flashMap != null) {	        
	        mv.addObject("petName", flashMap.get("petName"));
	        mv.addObject("petAge", flashMap.get("petAge"));
	        mv.addObject("userId", flashMap.get("userId"));
	    }	    
	    // Add Status enum values to the model
	    mv.addObject("petType", Species.values());
	    response.setStatus(HttpServletResponse.SC_OK);
		return mv;
    }
    
	@GetMapping("/registerPet/{userId}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
	public ModelAndView createPet(@RequestParam String petName,
									@RequestParam String petType,
									@RequestParam String petAge,
									@RequestParam String userId,
									HttpServletRequest request,
									HttpServletResponse response,
									RedirectAttributes redirectAttributes) {
	    log.info("Entered into the /registerPet/{userId} request");
	    ModelAndView mv = new ModelAndView();
	    String petNameError = "";
	    String petTypeError = "";
	    String petAgeError = "";
	    String userIdError = "";
	    int pageNumber = 0;
	    
	    return mv;
	}
}