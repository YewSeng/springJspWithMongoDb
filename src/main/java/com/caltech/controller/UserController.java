package com.caltech.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.caltech.exception.UserNotFoundException;
import com.caltech.exception.UsernameAlreadyExistException;
import com.caltech.pojo.User;
import com.caltech.service.UserService;
import com.caltech.utils.UserPage;
import com.caltech.utils.CustomPageable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api/v1/users")
public class UserController {
	
	private UserService userService;
	private JwtGeneratorValidator jwtValidator;
	private static final int DEFAULT_PAGE_SIZE = 5;
	
    @Autowired
    public UserController(UserService userService, JwtGeneratorValidator jwtValidator) {
        this.userService = userService;
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
        if (!username.matches("^U[a-zA-Z0-9]{7,}$")) {
            return "Username must start with an uppercase 'U' followed by at least 7 characters";
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
    
    private String validateForm(String name, String username, String password) {
        // Regular expressions for validation
    	String nameRegex = "^[a-zA-Z ]{4,}$";
        String usernameRegex = "^U[a-zA-Z0-9]{7,}$"; // Username must start with "U" (upper case) and followed by be at least 7 characters and contain only letters and numbers
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
            errorMessage.append("Username must start with an uppercase 'U' followed by at least 7 characters\n");
        }
        if (!password.matches(passwordRegex)) {
            errorMessage.append("Password must be at least 8 characters long, contain at least 1 uppercase letter, 1 '@', and 1 number\n");
        }

        return errorMessage.toString();
    }
    
    private int calculatePageNumberForUser(User user) {
        int pageSize = DEFAULT_PAGE_SIZE;
        int pageNumber = 0;
        boolean userFound = false;

        while (!userFound) {
            // Fetch users for the current page number
            UserPage userPage = userService.findAllUsers(new UserPage(pageNumber, pageSize));
            List<User> users = userPage.getContent();

            // Check if the user is in the current page
            if (users.contains(user)) {
                userFound = true;
            } else {
                // If user not found, increment the page number
                pageNumber++;

                // Break the loop if we have reached the last page
                if (pageNumber >= userPage.getTotalPages()) {
                    pageNumber = -1; // Indicate user not found
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
    public ModelAndView goToUserHomePage(HttpServletRequest request, HttpServletResponse response) {
        log.info("Entered into the /home request");
        ModelAndView mv = new ModelAndView();
        mv.setViewName("userHome");
        log.info("Went to userHome.jsp page");

        // Extract the token from the request cookie
        String token = extractTokenFromRequest(request);

        if (token != null) {
            // Parse the token to extract the username
            String username = jwtValidator.extractUsername(token);
            mv.addObject("username", username);
        }

        // Set the response status to OK
        response.setStatus(HttpServletResponse.SC_OK);
        return mv;
    }

    
	@GetMapping("/createUser")
	public ModelAndView goToCreateUserPage(HttpServletRequest request, HttpServletResponse response) {
		log.info("Entered into the /createUser request");
		ModelAndView mv = new ModelAndView();
		mv.setViewName("createUser");
		log.info("Went to createUser.jsp page");
	    // Get flash attributes and add them to the model
	    Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
	    if (flashMap != null) {
	        mv.addObject("name", flashMap.get("name"));
	        mv.addObject("username", flashMap.get("username"));
	        mv.addObject("password", flashMap.get("password"));
	    }
	    response.setStatus(HttpServletResponse.SC_OK);
		return mv;
	}
    
	@PostMapping("/registerUser")
	public ModelAndView createUser(@RequestParam String name,
	                                @RequestParam String username,
	                                @RequestParam String password,
	                                HttpServletRequest request,
	                                HttpServletResponse response,
	                                RedirectAttributes redirectAttributes) {
	    log.info("Entered into the /registerUser request");
	    ModelAndView mv = new ModelAndView();
	    String nameError = "";
	    String usernameError = "";
	    String passwordError = "";
	    int pageNumber = 0;
	    try {
	        // Perform form validation
	        nameError = validateName(name);
	        usernameError = validateUsername(username);
	        passwordError = validatePassword(password);

	        String errorMessage = validateForm(name, username, password);
	        if (!errorMessage.isEmpty()) {
	            throw new IllegalArgumentException(errorMessage);
	        }

	        if (!userService.verifyExistingUsername(username)) {
	            User user = new User();
	            user.setUserId(new ObjectId());
	            user.setName(name);
	            user.setUsername(username);
	            user.setPassword(password);

	            userService.createUser(user);
	            Optional<User> createdUserOptional = userService.findUserByUsername(username);
	            if (createdUserOptional.isPresent()) {
	                User createdUser = createdUserOptional.get();
	                pageNumber = calculatePageNumberForUser(createdUser);
	                redirectAttributes.addFlashAttribute("createdUser", createdUser);
	                redirectAttributes.addFlashAttribute("pageNumber", pageNumber);
	            }
	            // Add success message attribute
	            redirectAttributes.addFlashAttribute("successMessage", "User created successfully.");
	            // Redirect to filtered view based on the username
	            mv.setViewName("redirect:/api/v1/users/getAllUsers?page=" + pageNumber + "&size=" + DEFAULT_PAGE_SIZE);
	            response.setStatus(HttpServletResponse.SC_OK);
	            return mv;
	        } else {
	            log.error("Username is already taken. Please try again!");
	            throw new UsernameAlreadyExistException("Username is already taken. Please try again!");
	        }
	    } catch (IllegalArgumentException | UsernameAlreadyExistException e) {
	    	log.error("Either username {} and/or fields: name: {}, username: {}, password: {}  do not match the basic fields requirements", username, name, username, password);
	        redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
	        redirectAttributes.addFlashAttribute("nameError", nameError); 
	        redirectAttributes.addFlashAttribute("usernameError", usernameError); 
	        redirectAttributes.addFlashAttribute("passwordError", passwordError); 
	        redirectAttributes.addFlashAttribute("name", name);
	        redirectAttributes.addFlashAttribute("username", username);
	        redirectAttributes.addFlashAttribute("password", password);
	        mv.setViewName("redirect:/api/v1/users/createUser");
	        response.setStatus(HttpServletResponse.SC_CONFLICT);
	    }
	    return mv;
	}

    @GetMapping("/getAllUsers")
    public ModelAndView getAllUsers(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size,
                                    HttpServletRequest request, 
                                    HttpServletResponse response) {
        log.info("Entered into the /getAllUsers request");
        
        CustomPageable<User> pageable = new UserPage(page, size); // Create CustomPageable object
        UserPage customPage = userService.findAllUsers(pageable);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("viewUsers");
        mv.addObject("users", customPage.getContent());
        mv.addObject("pageNumber", customPage.getPageNumber());
        mv.addObject("pageSize", customPage.getPageSize());
        mv.addObject("totalElements", customPage.getTotalElements());
        mv.addObject("totalPages", customPage.getTotalPages());
        response.setStatus(HttpServletResponse.SC_OK);
        return mv;
    }
    
    @GetMapping("/filter")
    public ModelAndView filterUsers(@RequestParam("searchType") String searchType, 
                                     @RequestParam("searchTerm") String searchTerm,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "5") int size,
                                     HttpServletRequest request, 
                                     HttpServletResponse response) {
    	
        if (searchTerm.isEmpty()) {
        	response.setStatus(HttpServletResponse.SC_OK);
            return new ModelAndView("redirect:/api/v1/users/getAllUsers");
        }
        
        CustomPageable<User> pageable = new UserPage(page, size); // Create CustomPageable object
        UserPage filteredUsers;
        if ("name".equals(searchType)) {
            filteredUsers = userService.findUsersByName(searchTerm, pageable);
        } else if ("username".equals(searchType)) {
            filteredUsers = userService.findUsersByUsername(searchTerm, pageable);
        } else {
            // Default behavior: return all users
            filteredUsers = userService.findAllUsers(pageable);
        }
        ModelAndView mv = new ModelAndView();
        mv.setViewName("viewUsers"); // Set view name to the JSP page
        mv.addObject("users", filteredUsers.getContent()); // Add filtered users to the model
        mv.addObject("pageNumber", filteredUsers.getPageNumber()); // Add page number
        mv.addObject("pageSize", filteredUsers.getPageSize()); // Add page size
        mv.addObject("totalElements", filteredUsers.getTotalElements()); // Add total elements
        mv.addObject("totalPages", filteredUsers.getTotalPages()); // Add total pages
        response.setStatus(HttpServletResponse.SC_OK);
        return mv;
    }
    
    @GetMapping("/editUser/{userId}")
    public ModelAndView goToUpdateUserPage(@PathVariable ObjectId userId, HttpServletRequest request, HttpServletResponse response) {
        log.info("Entered into the /editUser/{userId} request for userId: {}", userId);
        ModelAndView mv = new ModelAndView();
        try {
            // Retrieve user information based on the provided userId
            Optional<User> userOptional = userService.findUserById(userId);
            
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                
                // Add the user object to the ModelAndView
                mv.addObject("user", user);
                
                // Set the view name to the JSP page for updating user
                mv.setViewName("editUser");
	            response.setStatus(HttpServletResponse.SC_OK);
                log.info("Went to update User page");
            } else {
                // If user not found, set view to userNotFound page
                mv.setViewName("userNotFound");
                mv.addObject("errorMessage", "User not found with userId: " + userId);
                // Set HTTP status to 404 (Not Found)
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error occurred while retrieving user with userId: {}", userId, e);
            mv.setViewName("generalError");
            mv.addObject("errorMessage", "An error occurred while retrieving user");
            // Set HTTP status to 500 (Internal Server Error)
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return mv;
    }
    
    @PutMapping("/updateUser/{userId}")
    public ModelAndView updateUser(@PathVariable ObjectId userId,
            @RequestParam String name,
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request, 
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        log.info("Entered into the /updateUser/{userId} request for userId: {}", userId);
        ModelAndView mv = new ModelAndView();
        User updatedUser = new User();
		String nameError = "";
	    String usernameError = "";
	    String passwordError = "";
	    int pageNumber = 0;
        try {       
            // Perform form validation
	        nameError = validateName(name);
	        usernameError = validateUsername(username);
	        passwordError = validatePassword(password);
			
            String errorMessage = validateForm(name, username, password);
            if (!errorMessage.isEmpty()) {
                throw new IllegalArgumentException(errorMessage);
            }
			Optional<User> oldUserOptional = userService.findUserById(userId);
			if (oldUserOptional.isPresent()) {
				User oldUser = oldUserOptional.get();
				redirectAttributes.addFlashAttribute("oldUser", oldUser);
			}
            updatedUser.setName(name);
            updatedUser.setUsername(username);
            updatedUser.setPassword(password);
            
            User user = userService.updateUser(userId, updatedUser);
            
            Optional<User> newUserOptional = userService.findUserById(userId);
            if (newUserOptional.isPresent()) {
				User newUser = newUserOptional.get();
				pageNumber = calculatePageNumberForUser(user);
	            redirectAttributes.addFlashAttribute("pageNumber", pageNumber);
				redirectAttributes.addFlashAttribute("newUser", newUser);
            }
            // Set success message and redirect to getAllUsers
            redirectAttributes.addFlashAttribute("successMessage", "User with user id " + userId + " is successfully updated."); 
            // Redirect to filtered view based on the updated user's username
            mv.setViewName("redirect:/api/v1/users/getAllUsers?page=" + pageNumber + "&size=" + DEFAULT_PAGE_SIZE);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException | UsernameAlreadyExistException e) {
            log.error("Either username {} and/or fields: name: {}, username: {}, password: {}  do not match the basic fields requirements", username, name, username, password);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
	        redirectAttributes.addFlashAttribute("nameError", nameError); // Add nameError
	        redirectAttributes.addFlashAttribute("usernameError", usernameError); // Add usernameError
	        redirectAttributes.addFlashAttribute("passwordError", passwordError); // Add passwordError
	        redirectAttributes.addFlashAttribute("name", name);
	        redirectAttributes.addFlashAttribute("username", username);
	        redirectAttributes.addFlashAttribute("password", password);
            mv.addObject("user", updatedUser);
            mv.setViewName("redirect:/api/v1/users/editUser/{userId}");
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        } catch (UserNotFoundException e) {
            log.error("User not found with userId: {}", userId);
            mv.setViewName("userNotFound");
            mv.addObject("errorMessage", e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            log.error("Error occurred while updating user with userId: {}", userId, e);
            mv.setViewName("generalError");
            mv.addObject("errorMessage", "An error occurred while updating user");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return mv;
    }

    @DeleteMapping("/deleteUser/{userId}")
    public ModelAndView deleteUser(@PathVariable ObjectId userId,
                                    HttpServletRequest request,
                                    HttpServletResponse response,
                                    RedirectAttributes redirectAttributes) {
        log.info("Entered into the /deleteUser/{userId} request for userId: {}", userId);
        ModelAndView mv = new ModelAndView();

        try {
            // Retrieve the user data before performing the delete operation
            Optional<User> deletedUserOptional = userService.findUserById(userId);

            if (deletedUserOptional.isPresent()) {
                User deletedUser = deletedUserOptional.get();

                // Perform the delete operation
                userService.deleteUser(userId);
                log.info("User with user ID: {} has been successfully deleted", userId);
                redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully.");
                redirectAttributes.addFlashAttribute("deletedUser", deletedUser);

                // Set the status to SC_OK when the user is successfully deleted
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "User not found with userId: " + userId);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

            // Redirect to the referrer page if available
            mv.setViewName("redirect:" + request.getHeader("referer"));
            return mv;
        } catch (UserNotFoundException e) {
            log.error("User not found while deleting user with userId: {}", userId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "User not found with userId: " + userId);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            // Redirect to the referrer page if available
            mv.setViewName("redirect:" + request.getHeader("referer"));
            return mv;
        } catch (Exception e) {
            log.error("Error occurred while deleting user with userId: {}", userId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while deleting user: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            // Redirect to the referrer page if available
            mv.setViewName("redirect:" + request.getHeader("referer"));
            return mv;
        }
    }
}