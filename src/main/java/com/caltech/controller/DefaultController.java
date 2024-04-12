package com.caltech.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.caltech.config.JwtGeneratorValidator;
import com.caltech.service.AdminService;
import com.caltech.service.AuthenticationService;
import com.caltech.service.DefaultUserServiceImplementation;
import com.caltech.service.DoctorService;
import com.caltech.service.IpAddressLockoutService;
import com.caltech.service.SuperAdminService;
import com.caltech.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
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
	private DefaultUserServiceImplementation defaultUserServiceImplementation;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Value("${superadmin.secretKey}")
    private String superAdminKey;
	
	@Autowired
	private SuperAdminService superAdminService;	
	
	@Autowired
	private AdminService adminService;
	
	@Autowired
	private DoctorService doctorService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	public DefaultController(JwtGeneratorValidator jwtValidator, DefaultUserServiceImplementation defaultUserServiceImplementation, 
			SuperAdminService superAdminService, AdminService adminService, 
			DoctorService doctorService, UserService userService) {
		this.jwtValidator = jwtValidator;
		/*this.authenticationManager = authenticationManager;*/
		this.defaultUserServiceImplementation = defaultUserServiceImplementation;
		this.superAdminService = superAdminService;
		this.adminService = adminService;
		this.doctorService = doctorService;
		this.userService = userService;
	}
	
    private String validateUsername(String username) {
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
	
    private String validateForm(String username, String password) {
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
	        String usernameError = validateUsername(username);
	        String passwordError = validatePassword(password);
	        String errorMessage = validateForm(username, password);

	        if (!errorMessage.isEmpty()) {
	            ipAddressLockoutService.incrementFailedAttempts(ipAddress);
	            redirectAttributes.addFlashAttribute("usernameError", usernameError);
	            redirectAttributes.addFlashAttribute("passwordError", passwordError);
	            redirectAttributes.addFlashAttribute("errorMessage", errorMessage);
                mv.setViewName("redirect: "); // Redirect back to login page
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return mv; 
	        } else {
	            // Check if the user is currently locked out
	            if (ipAddressLockoutService.isIpAddressLockedOut(ipAddress)) {
	                // Return lockout status to the frontend
	                mv.addObject("lockout", true);
	                mv.addObject("lockoutDuration", lockoutDuration);
	                mv.setViewName("redirect: "); // Redirect back to login page
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
	                
	                SecurityContextHolder.getContext().setAuthentication(authentication);
	                UserDetails userDetails = (UserDetails) authentication.getPrincipal();
	                if (isAdmin || isDoctor || isUser) {
	                    // Reset login attempts on successful login
	                    ipAddressLockoutService.resetFailedAttempts(ipAddress);

	                    // Generate JWT token
	                    String token = jwtValidator.generateToken(authentication);
	                    log.info("JWT TOKEN: {}", token);
	                    
	                    // Store token in cookie
	                    Cookie tokenCookie = new Cookie("token", token);
	                    tokenCookie.setHttpOnly(true);
	                    response.addCookie(tokenCookie);	                    
	                    
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
	                        long currentTime = System.currentTimeMillis();
	                        long lockoutEndTime = currentTime + lockoutDuration;
	                        long timeLeft = lockoutEndTime - currentTime;
	                        long timerLeftForFormToNotBeDisabled = timeLeft / 1000; // Convert to seconds
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

	
//	@PostMapping("/hidden")
//	public ModelAndView authenticationSuperAdmin(@RequestParam String adminKey,  
//			HttpServletRequest request, HttpServletResponse response) {
//		
//	}
}
