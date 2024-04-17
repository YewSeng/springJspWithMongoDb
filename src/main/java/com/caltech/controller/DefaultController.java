package com.caltech.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import com.caltech.config.JwtGeneratorValidator;
import com.caltech.exception.UsernameAlreadyExistException;
import com.caltech.pojo.User;
import com.caltech.service.AuthenticationService;
import com.caltech.service.IpAddressLockoutService;
import com.caltech.service.SuperAdminService;
import com.caltech.service.UserService;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@NoArgsConstructor
public class DefaultController {
	
	private static final int MAX_ATTEMPTS = 5;
	private static final int LOCKOUT_DURATION = 10 * 60 * 1000;
	
	@Autowired
	private JwtGeneratorValidator jwtValidator;

	@Autowired
	private IpAddressLockoutService ipAddressLockoutService;
	
	@Autowired
	private AuthenticationService authenticationService;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private SuperAdminService superAdminService;
	
	@Autowired
	private UserService userService;
	
	@Value("${superadmin.secretKey}")
    private String superAdminKey;	
		

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
    
    private String validateUserUsername(String username) {
        if (!username.matches("^U[a-zA-Z0-9]{7,}$")) {
            return "Username must start with an uppercase 'U' followed by at least 7 characters";
        } else {
            return ""; // No error
        }
    }
    
    private String validateLoginFormUsername(String username) {
        if (!username.matches("^(A|D|U)[a-zA-Z0-9]{7,}$")) {
            return "Username must start with an uppercase 'A', 'D', or 'U' followed by at least 7 characters";
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
    
    private String validateSuperAdminKey(String superAdminKey) {
    	String superAdminKeyRegex = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$";
    	if (superAdminKey == null || superAdminKey.trim().isEmpty()) {
    		return "Super Admin Key cannot be empty!";
    	} else if (!superAdminKey.matches(superAdminKeyRegex)) {
    		return "Invalid Super Admin Key Pattern!";
    	}
    	return "";
    }
	
    private String validateLoginForm(String username, String password) {
        // Regular expressions for validation
    	String usernameRegex = "^(A|D|U)[a-zA-Z0-9]{7,}$"; // Username must start with "A", "D", or "U" (upper case) and be followed by at least 7 characters and contain only letters and numbers
        String passwordRegex = "^(?=.*[A-Z])(?=.*[@])(?=.*[0-9])[a-zA-Z0-9@]{8,}$"; // Password: At least 1 uppercase, 1 "@", 1 number, At least length 8

        // Validation checks
        StringBuilder errorMessage = new StringBuilder();

        if (!username.matches(usernameRegex)) {
            errorMessage.append("Username must start with an uppercase 'A', 'D', or 'U' followed by at least 7 characters\n");
        }
        if (!password.matches(passwordRegex)) {
            errorMessage.append("Password must be at least 8 characters long, contain at least 1 uppercase letter, 1 '@', and 1 number\n");
        }

        return errorMessage.toString();
    }
    
    private String validateUserForm(String name, String username, String password) {
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
	
	@GetMapping("/")
	public ModelAndView goToIndexPage(HttpServletRequest request, HttpServletResponse response) {
		log.info("Entered into the / request");
		ModelAndView mv = new ModelAndView();
		mv.setViewName("index");
		log.info("Went to Index.jsp page");
	    response.setStatus(HttpServletResponse.SC_OK);
		return mv;
	}	
	
	@PostMapping("/login")
	public ModelAndView genericAuthenticationForm(@RequestParam String username,
	        @RequestParam String password,
	        HttpServletRequest request,
	        HttpServletResponse response,
	        RedirectAttributes redirectAttributes) {
	    log.info("Entered into the /login request");
	    ModelAndView mv = new ModelAndView();
	    String ipAddress = request.getRemoteAddr();
	    int maxAttempts = MAX_ATTEMPTS;
	    int lockoutDuration = LOCKOUT_DURATION;
	    boolean isAdmin = false;
	    boolean isDoctor = false;
	    boolean isUser = false;

	    try {
	        // Perform form validation
	        String usernameError = validateLoginFormUsername(username);
	        String passwordError = validatePassword(password);
	        String errorMessage = validateLoginForm(username, password);

	        if (!errorMessage.isEmpty()) {
	            ipAddressLockoutService.incrementFailedAttempts(ipAddress);
	            redirectAttributes.addFlashAttribute("usernameError", usernameError);
	            redirectAttributes.addFlashAttribute("passwordError", passwordError);
	            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
	            mv.setViewName("redirect:"); // Redirect back to login page
	            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	            return mv;
	        } else {
	            // Check if the user is currently locked out
	            if (ipAddressLockoutService.isIpAddressLockedOut(ipAddress)) {
	                // Return lockout status to the frontend
	                long lockoutEndTime = ipAddressLockoutService.getLockoutEndTime(ipAddress);
	                long timeLeft = lockoutEndTime - System.currentTimeMillis();
	                long timerLeftForFormToNotBeDisabled = TimeUnit.MILLISECONDS.toSeconds(timeLeft);
	                mv.addObject("lockout", true);
	                mv.addObject("timerLeftForFormToNotBeDisabled", timerLeftForFormToNotBeDisabled);
	                mv.setViewName("redirect:"); // Redirect back to login page
	                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	                return mv;
	            } else {
	                // Authenticate
	                isAdmin = authenticationService.authenticateAdmin(username, password);
	                isDoctor = authenticationService.authenticateDoctor(username, password);
	                isUser = authenticationService.authenticateUser(username, password);
	                log.info("Attempting to authenticate as a User: {}", isUser);
	                log.info("Attempting to authenticate as a Doctor: {}", isDoctor);
	                log.info("Attempting to authenticate as a Admin: {}", isAdmin);
	                // Authenticate using Spring Security
	                Authentication authentication = authenticationManager.authenticate(
	                        new UsernamePasswordAuthenticationToken(username, password));
	                log.info("Authentication: {}", authentication);
	                SecurityContextHolder.getContext().setAuthentication(authentication);
	                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	                if (isAdmin || isDoctor || isUser) {
	                    // Reset login attempts on successful login
	                    ipAddressLockoutService.resetFailedAttempts(ipAddress);

	                    // Extract roles from user details
	                    List<String> roles = userDetails.getAuthorities().stream()
	                            .map(GrantedAuthority::getAuthority)
	                            .collect(Collectors.toList());

	                    // Log the roles
	                    log.info("User roles: {}", roles);
	                    
	                    // Generate JWT token
	                    String token = jwtValidator.generateToken(authentication);
	                    log.info("JWT TOKEN: {}", token);

	                    // Generate JWT refresh token
	                    String refreshToken = jwtValidator.generateRefreshToken(authentication);
	                    log.info("JWT REFRESH TOKEN: {}", refreshToken);

	                    // Store token in cookie
	                    Cookie tokenCookie = new Cookie("token", token);
	                    tokenCookie.setHttpOnly(true);
	                    response.addCookie(tokenCookie);

	                    Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
	                    refreshTokenCookie.setHttpOnly(true);
	                    response.addCookie(refreshTokenCookie);

	                    if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
	                        mv.setViewName("redirect:/api/v1/admins/home");
	                    } else if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER"))) {
	                        mv.setViewName("redirect:/api/v1/users/home");
	                    } else if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_DOCTOR"))) {
	                        mv.setViewName("redirect:/api/v1/doctors/home");
	                    } else {
	                        // Handle other roles or unknown roles
	                        mv.setViewName("redirect:");
	                    }
	                    response.setStatus(HttpServletResponse.SC_OK);
	                    return mv;
	                } else {
	                    // Increment login attempts
	                    ipAddressLockoutService.incrementFailedAttempts(ipAddress);

	                    // Check if login attempts exceed the maximum allowed
	                    int remainingAttempts = maxAttempts - ipAddressLockoutService.getInvalidAttempts(ipAddress);
	                    redirectAttributes.addFlashAttribute("remainingAttempts", remainingAttempts);

	                    if (ipAddressLockoutService.getInvalidAttempts(ipAddress) >= maxAttempts) {
	                        // Set user lockout flag and timestamp
	                        ipAddressLockoutService.setIpAdressLockout(ipAddress);
	                        long lockoutEndTime = System.currentTimeMillis() + lockoutDuration;
	                        ipAddressLockoutService.setLockoutEndTime(ipAddress, lockoutEndTime);
	                        long timeLeft = lockoutDuration;
	                        long timerLeftForFormToNotBeDisabled = TimeUnit.MILLISECONDS.toSeconds(timeLeft);
	                        mv.addObject("lockout", true);
	                        mv.addObject("timerLeftForFormToNotBeDisabled", timerLeftForFormToNotBeDisabled);
	                    }

	                    // Authentication failed
	                    redirectAttributes.addFlashAttribute("loginError", true);
	                    redirectAttributes.addFlashAttribute("loginErrorMessage", "Invalid username or password. Please try again.");
	                    mv.setViewName("redirect:");
	                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	                    return mv;
	                }
	            }
	        }
	    } catch (Exception e) {
	        log.error("Error occurred during login", e);
	        redirectAttributes.addFlashAttribute("loginError", true);
	        redirectAttributes.addFlashAttribute("loginErrorMessage", "Invalid username or password. Please try again.");
	        mv.setViewName("redirect:");
	        return mv;
	    }
	}
	
	@GetMapping("/hidden")
	public ModelAndView goToHiddenSuperAdminLoginForm(HttpServletRequest request, HttpServletResponse response) {
		log.info("Entered into the /hidden request");
		ModelAndView mv = new ModelAndView();
		mv.setViewName("hidden");
		log.info("Went to hidden.jsp page");
	    response.setStatus(HttpServletResponse.SC_OK);
		return mv;
	}
	
	@PostMapping("/superAdminLogin")
	public ModelAndView authenticationSuperAdmin(@RequestParam String superAdminKey,
	                                              HttpServletRequest request,
	                                              HttpServletResponse response,
	                                              RedirectAttributes redirectAttributes) {
	    log.info("Entered into the /superAdminLogin request");
	    ModelAndView mv = new ModelAndView();
	    String ipAddress = request.getRemoteAddr();
	    int maxAttempts = MAX_ATTEMPTS;
	    int lockoutDuration = LOCKOUT_DURATION;
	    boolean isSuperAdmin = false;
	    
	    try {
	        String superAdminKeyError = validateSuperAdminKey(superAdminKey);
	        if (!superAdminKeyError.isEmpty()) {
	            ipAddressLockoutService.incrementFailedAttempts(ipAddress);
	            redirectAttributes.addFlashAttribute("superAdminKeyError", superAdminKeyError);
	            mv.setViewName("redirect:/hidden");
	            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	            return mv;
	        } else {
	            if (ipAddressLockoutService.isIpAddressLockedOut(ipAddress)) {
	                // Return lockout status to the frontend
	                long lockoutEndTime = ipAddressLockoutService.getLockoutEndTime(ipAddress);
	                long timeLeft = lockoutEndTime - System.currentTimeMillis();
	                long timerLeftForFormToNotBeDisabled = TimeUnit.MILLISECONDS.toSeconds(timeLeft);
	                mv.addObject("lockout", true);
	                mv.addObject("timerLeftForFormToNotBeDisabled", timerLeftForFormToNotBeDisabled);
	                mv.setViewName("redirect:/hidden"); // Redirect back to login page
	                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	                return mv;
	            } else {
	                isSuperAdmin = authenticationService.authenticateSuperAdmin(superAdminKey);
	                log.info("Attempting to authenticate as a Super Admin: {}", isSuperAdmin);
	                // Authenticate using Spring Security
	                Authentication authentication = authenticationManager.authenticate(
	                		new UsernamePasswordAuthenticationToken(superAdminKey, null));
	                log.info("Authentication: {}", authentication);
	                if (isSuperAdmin) {
	                    // Create authentication token with super admin details
	                    UserDetails userDetails = new org.springframework.security.core.userdetails.User("Super Admin", "", Collections.singletonList(new SimpleGrantedAuthority("ROLE_SUPERADMIN")));
	                    UsernamePasswordAuthenticationToken authenticationToken =
	                            new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
	                    
	                    // Set authentication in SecurityContextHolder
	                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
	                 // Log authentication status
	                    log.info("Is Super Admin authenticated: {}", SecurityContextHolder.getContext().getAuthentication().isAuthenticated());
	                    // Generate JWT token
	                    String token = jwtValidator.generateToken(authentication);
	                    log.info("JWT TOKEN: {}", token);
	                    // Generate JWT refresh token
	                    String refreshToken = jwtValidator.generateRefreshToken(authentication);
	                    log.info("JWT REFRESH TOKEN: {}", refreshToken);
	                    // Store token in cookie
	                    Cookie tokenCookie = new Cookie("token", token);
	                    tokenCookie.setHttpOnly(true);
	                    response.addCookie(tokenCookie);
	                    Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
	                    refreshTokenCookie.setHttpOnly(true);
	                    response.addCookie(refreshTokenCookie);
	                    if (userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPERADMIN"))) {
	                        mv.setViewName("redirect:/api/v1/superadmins/home");
	                    } else {
	                        // Handle other roles or unknown roles
	                        mv.setViewName("redirect:/hidden");
	                    }
	                    response.setStatus(HttpServletResponse.SC_OK);
	                    return mv;
	                } else {
	                    // Increment login attempts
	                    ipAddressLockoutService.incrementFailedAttempts(ipAddress);
	                    // Check if login attempts exceed the maximum allowed
	                    int remainingAttempts = maxAttempts - ipAddressLockoutService.getInvalidAttempts(ipAddress);
	                    redirectAttributes.addFlashAttribute("remainingAttempts", remainingAttempts);
	                    if (ipAddressLockoutService.getInvalidAttempts(ipAddress) >= maxAttempts) {
	                        // Set user lockout flag and timestamp
	                        ipAddressLockoutService.setIpAdressLockout(ipAddress);
	                        long lockoutEndTime = System.currentTimeMillis() + lockoutDuration;
	                        ipAddressLockoutService.setLockoutEndTime(ipAddress, lockoutEndTime);
	                        long timeLeft = lockoutDuration;
	                        long timerLeftForFormToNotBeDisabled = TimeUnit.MILLISECONDS.toSeconds(timeLeft);
	                        mv.addObject("lockout", true);
	                        mv.addObject("timerLeftForFormToNotBeDisabled", timerLeftForFormToNotBeDisabled);
	                    }
	                    // Authentication failed
	                    redirectAttributes.addFlashAttribute("loginError", true);
	                    redirectAttributes.addFlashAttribute("loginErrorMessage", "Invalid username or password. Please try again.");
	                    mv.setViewName("redirect:");
	                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	                    return mv;
	                }
	            }
	        }
	    } catch (Exception e) {
	        log.error("Error occurred during login", e);
	        redirectAttributes.addFlashAttribute("loginError", true);
	        redirectAttributes.addFlashAttribute("loginErrorMessage", "Invalid username or password. Please try again.");
	        mv.setViewName("redirect:");
	        return mv;
	    }
	}
	
	@GetMapping("/registration")
	public ModelAndView goToRegistrationPage(HttpServletRequest request, HttpServletResponse response) {
		log.info("Entered into the /registration request");
		ModelAndView mv = new ModelAndView();
		mv.setViewName("registration");
		log.info("Went to registration.jsp page");
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
	public ModelAndView registerUser(@RequestParam String name,
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
	    try {
	        // Perform form validation
	        nameError = validateName(name);
	        usernameError = validateUserUsername(username);
	        passwordError = validatePassword(password);

	        String errorMessage = validateUserForm(name, username, password);
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
	            // Add success message attribute
	            redirectAttributes.addFlashAttribute("successMessage", "User with username <" + user.getUsername() + "> has been successfully created. Please Login now!");
	            // Redirect to filtered view based on the username
	            mv.setViewName("redirect:");
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
	        mv.setViewName("redirect:/registration");
	        response.setStatus(HttpServletResponse.SC_CONFLICT);
	    }
	    return mv;
	}
}
