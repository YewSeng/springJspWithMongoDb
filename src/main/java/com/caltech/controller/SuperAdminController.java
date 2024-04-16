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
import com.caltech.exception.AdminNotFoundException;
import com.caltech.exception.UsernameAlreadyExistException;
import com.caltech.pojo.Admin;
import com.caltech.service.AdminService;
import com.caltech.utils.AdminPage;
import com.caltech.utils.CustomPageable;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/api/v1/superadmins")
public class SuperAdminController {

	private AdminService adminService;
	private JwtGeneratorValidator jwtValidator;
	private static final int DEFAULT_PAGE_SIZE = 5;
	
    @Value("${jwt.secretKey}")
    private String SECRET;
    
    @Autowired
    public SuperAdminController(AdminService adminService, JwtGeneratorValidator jwtValidator) {
        this.adminService = adminService;
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
        if (!username.matches("^A[a-zA-Z0-9]{7,}$")) {
            return "Username must start with an uppercase 'A' followed by at least 7 characters";
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
        String usernameRegex = "^A[a-zA-Z0-9]{7,}$"; // Username must start with "A" (upper case) and followed by be at least 7 characters and contain only letters and numbers
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
            errorMessage.append("Username must start with an uppercase 'A' followed by at least 7 characters\n");
        }
        if (!password.matches(passwordRegex)) {
            errorMessage.append("Password must be at least 8 characters long, contain at least 1 uppercase letter, 1 '@', and 1 number\n");
        }

        return errorMessage.toString();
    }
    
    private int calculatePageNumberForAdmin(Admin admin) {
        int pageSize = DEFAULT_PAGE_SIZE;
        int pageNumber = 0;
        boolean adminFound = false;

        while (!adminFound) {
            // Fetch admins for the current page number
            AdminPage adminPage = adminService.findAllAdmins(new AdminPage(pageNumber, pageSize));
            List<Admin> admins = adminPage.getContent();

            // Check if the admin is in the current page
            if (admins.contains(admin)) {
                adminFound = true;
            } else {
                // If admin not found, increment the page number
                pageNumber++;

                // Break the loop if we have reached the last page
                if (pageNumber >= adminPage.getTotalPages()) {
                    pageNumber = -1; // Indicate admin not found
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
    
    private boolean isSuperAdmin(HttpServletRequest request) {
        String token = extractTokenFromRequest(request);
        if (token != null) {
            try {
                // Parse the token to extract claims
                Claims claims = Jwts.parser().setSigningKey(SECRET).parseClaimsJws(token).getBody();
                
                // Check if the token contains the "role" claim and its value is "ROLE_ADMIN"
                if (claims.containsKey("role")) {
                    String role = (String) claims.get("role");
                    log.info("Role: {}", role);
                    return role.equals("ROLE_SUPERADMIN");
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
    @PreAuthorize("hasAuthority('ROLE_SUPERADMIN')")
    public ModelAndView goToSuperAdminHomePage(HttpServletRequest request, HttpServletResponse response) {
        log.info("Entered into the /home request");
        ModelAndView mv = new ModelAndView();
        
        // Check if the user is authorized
        if (!isSuperAdmin(request)) {
            // Invalidate session and clear tokens
            request.getSession().invalidate();
            response.setHeader("Authorization", "");
            // Redirect to index page with error message
            mv.setViewName("redirect:");
            mv.addObject("errorMessage", "Unauthorized access. Please login again.");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return mv;
        }
        
        mv.setViewName("superAdminHome");
        log.info("Went to superAdminHome.jsp page");

        // Extract the token from the request cookie
        String token = extractTokenFromRequest(request);
        log.info("Extracted token from request: {}", token);

        if (token != null) {
            // Parse the token to extract the username
            String username = jwtValidator.extractUsername(token);
            mv.addObject("username", username);
        }

        // Set the response status to OK
        response.setStatus(HttpServletResponse.SC_OK);
        return mv;
    }
    
	@GetMapping("/createAdmin")
    @PreAuthorize("hasAuthority('ROLE_ROLE_SUPERADMIN')")
	public ModelAndView goToCreateAdminPage(HttpServletRequest request, HttpServletResponse response) {
		log.info("Entered into the /createAdmin request");
		ModelAndView mv = new ModelAndView();
		mv.setViewName("createAdmin");
		log.info("Went to createAdmin.jsp page");
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
    
	@PostMapping("/registerAdmin")
	@PreAuthorize("hasAuthority('ROLE_ROLE_SUPERADMIN')")
	public ModelAndView createAdmin(@RequestParam String name,
	                                @RequestParam String username,
	                                @RequestParam String password,
	                                HttpServletRequest request,
	                                HttpServletResponse response,
	                                RedirectAttributes redirectAttributes) {
	    log.info("Entered into the /registerAdmin request");
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

	        if (!adminService.verifyExistingUsername(username)) {
	            Admin admin = new Admin();
	            admin.setAdminId(new ObjectId());
	            admin.setName(name);
	            admin.setUsername(username);
	            admin.setPassword(password);

	            adminService.createAdmin(admin);
	            Optional<Admin> createdAdminOptional = adminService.findAdminByUsername(username);
	            if (createdAdminOptional.isPresent()) {
	                Admin createdAdmin = createdAdminOptional.get();
	                pageNumber = calculatePageNumberForAdmin(createdAdmin);
	                redirectAttributes.addFlashAttribute("createdAdmin", createdAdmin);
	                redirectAttributes.addFlashAttribute("pageNumber", pageNumber);
	            }
	            // Add success message attribute
	            redirectAttributes.addFlashAttribute("successMessage", "Admin created successfully.");
	            // Redirect to filtered view based on the username
	            mv.setViewName("redirect:/api/v1/superadmins/getAllAdmins?page=" + pageNumber + "&size=" + DEFAULT_PAGE_SIZE);
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
	        mv.setViewName("redirect:/api/v1/superadmins/createAdmin");
	        response.setStatus(HttpServletResponse.SC_CONFLICT);
	    }
	    return mv;
	}

    @GetMapping("/getAllAdmins")
    @PreAuthorize("hasAuthority('ROLE_ROLE_SUPERADMIN')")
    public ModelAndView getAllAdmins(@RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "5") int size,
                                    HttpServletRequest request, 
                                    HttpServletResponse response) {
        log.info("Entered into the /getAllAdmins request");
        
        CustomPageable<Admin> pageable = new AdminPage(page, size); // Create CustomPageable object
        AdminPage customPage = adminService.findAllAdmins(pageable);
        ModelAndView mv = new ModelAndView();
        mv.setViewName("viewAdmins");
        mv.addObject("admins", customPage.getContent());
        mv.addObject("pageNumber", customPage.getPageNumber());
        mv.addObject("pageSize", customPage.getPageSize());
        mv.addObject("totalElements", customPage.getTotalElements());
        mv.addObject("totalPages", customPage.getTotalPages());
        response.setStatus(HttpServletResponse.SC_OK);
        return mv;
    }
    
    @GetMapping("/filter")
    public ModelAndView filterAdmins(@RequestParam("searchType") String searchType, 
                                     @RequestParam("searchTerm") String searchTerm,
                                     @RequestParam(defaultValue = "0") int page,
                                     @RequestParam(defaultValue = "5") int size,
                                     HttpServletRequest request, 
                                     HttpServletResponse response) {
    	
        if (searchTerm.isEmpty()) {
        	response.setStatus(HttpServletResponse.SC_OK);
            return new ModelAndView("redirect:/api/v1/superadmins/getAllAdmins");
        }
        
        CustomPageable<Admin> pageable = new AdminPage(page, size); // Create CustomPageable object
        AdminPage filteredAdmins;
        if ("name".equals(searchType)) {
            filteredAdmins = adminService.findAdminsByName(searchTerm, pageable);
        } else if ("username".equals(searchType)) {
            filteredAdmins = adminService.findAdminsByUsername(searchTerm, pageable);
        } else {
            // Default behavior: return all admins
            filteredAdmins = adminService.findAllAdmins(pageable);
        }
        ModelAndView mv = new ModelAndView();
        mv.setViewName("viewAdmins"); // Set view name to the JSP page
        mv.addObject("admins", filteredAdmins.getContent()); // Add filtered admins to the model
        mv.addObject("pageNumber", filteredAdmins.getPageNumber()); // Add page number
        mv.addObject("pageSize", filteredAdmins.getPageSize()); // Add page size
        mv.addObject("totalElements", filteredAdmins.getTotalElements()); // Add total elements
        mv.addObject("totalPages", filteredAdmins.getTotalPages()); // Add total pages
        response.setStatus(HttpServletResponse.SC_OK);
        return mv;
    }
    
    @GetMapping("/editAdmin/{adminId}")
    @PreAuthorize("hasAuthority('ROLE_ROLE_SUPERADMIN')")
    public ModelAndView goToUpdateAdminPage(@PathVariable ObjectId adminId, HttpServletRequest request, HttpServletResponse response) {
        log.info("Entered into the /editAdmin/{adminId} request for adminId: {}", adminId);
        ModelAndView mv = new ModelAndView();
        try {
            // Retrieve admin information based on the provided adminId
            Optional<Admin> adminOptional = adminService.findAdminById(adminId);
            
            if (adminOptional.isPresent()) {
                Admin admin = adminOptional.get();
                
                // Add the admin object to the ModelAndView
                mv.addObject("admin", admin);
                
                // Set the view name to the JSP page for updating admin
                mv.setViewName("editAdmin");
	            response.setStatus(HttpServletResponse.SC_OK);
                log.info("Went to update Admin page");
            } else {
                // If admin not found, set view to adminNotFound page
                mv.setViewName("adminNotFound");
                mv.addObject("errorMessage", "Admin not found with adminId: " + adminId);
                // Set HTTP status to 404 (Not Found)
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            log.error("Error occurred while retrieving admin with adminId: {}", adminId, e);
            mv.setViewName("generalError");
            mv.addObject("errorMessage", "An error occurred while retrieving admin");
            // Set HTTP status to 500 (Internal Server Error)
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return mv;
    }
    
    @PutMapping("/updateAdmin/{adminId}")
    @PreAuthorize("hasAuthority('ROLE_ROLE_SUPERADMIN')")
    public ModelAndView updateAdmin(@PathVariable ObjectId adminId,
            @RequestParam String name,
            @RequestParam String username,
            @RequestParam String password,
            HttpServletRequest request, 
            HttpServletResponse response,
            RedirectAttributes redirectAttributes) {
        log.info("Entered into the /updateAdmin/{adminId} request for adminId: {}", adminId);
        ModelAndView mv = new ModelAndView();
        Admin updatedAdmin = new Admin();
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
			Optional<Admin> oldAdminOptional = adminService.findAdminById(adminId);
			if (oldAdminOptional.isPresent()) {
				Admin oldAdmin = oldAdminOptional.get();
				redirectAttributes.addFlashAttribute("oldAdmin", oldAdmin);
			}
            updatedAdmin.setName(name);
            updatedAdmin.setUsername(username);
            updatedAdmin.setPassword(password);
            
            Admin admin = adminService.updateAdmin(adminId, updatedAdmin);
            
            Optional<Admin> newAdminOptional = adminService.findAdminById(adminId);
            if (newAdminOptional.isPresent()) {
				Admin newAdmin = newAdminOptional.get();
				pageNumber = calculatePageNumberForAdmin(admin);
	            redirectAttributes.addFlashAttribute("pageNumber", pageNumber);
				redirectAttributes.addFlashAttribute("newAdmin", newAdmin);
            }
            // Set success message and redirect to getAllAdmins
            redirectAttributes.addFlashAttribute("successMessage", "Admin with admin id " + adminId + " is successfully updated."); 
            // Redirect to filtered view based on the updated admin's username
            mv.setViewName("redirect:/api/v1/superadmins/getAllAdmins?page=" + pageNumber + "&size=" + DEFAULT_PAGE_SIZE);
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
            mv.addObject("admin", updatedAdmin);
            mv.setViewName("redirect:/api/v1/superadmins/editAdmin/{adminId}");
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        } catch (AdminNotFoundException e) {
            log.error("Admin not found with adminId: {}", adminId);
            mv.setViewName("adminNotFound");
            mv.addObject("errorMessage", e.getMessage());
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (Exception e) {
            log.error("Error occurred while updating admin with adminId: {}", adminId, e);
            mv.setViewName("generalError");
            mv.addObject("errorMessage", "An error occurred while updating admin");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return mv;
    }

    @DeleteMapping("/deleteAdmin/{adminId}")
    @PreAuthorize("hasAuthority('ROLE_ROLE_SUPERADMIN')")
    public ModelAndView deleteAdmin(@PathVariable ObjectId adminId,
                                    HttpServletRequest request,
                                    HttpServletResponse response,
                                    RedirectAttributes redirectAttributes) {
        log.info("Entered into the /deleteAdmin/{adminId} request for userId: {}", adminId);
        ModelAndView mv = new ModelAndView();

        try {
            // Retrieve the admin data before performing the delete operation
            Optional<Admin> deletedAdminOptional = adminService.findAdminById(adminId);

            if (deletedAdminOptional.isPresent()) {
            	Admin deletedAdmin = deletedAdminOptional.get();

                // Perform the delete operation
            	adminService.deleteAdmin(adminId);
                log.info("Admin with admin ID: {} has been successfully deleted", adminId);
                redirectAttributes.addFlashAttribute("successMessage", "Admin deleted successfully.");
                redirectAttributes.addFlashAttribute("deletedAdmin", deletedAdmin);

                // Set the status to SC_OK when the admin is successfully deleted
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                redirectAttributes.addFlashAttribute("errorMessage", "Admin not found with adminId: " + adminId);
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }

            // Redirect to the referrer page if available
            mv.setViewName("redirect:" + request.getHeader("referer"));
            return mv;
        } catch (AdminNotFoundException e) {
            log.error("Admin not found while deleting admin with adminId: {}", adminId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Admin not found with adminId: " + adminId);
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);

            // Redirect to the referrer page if available
            mv.setViewName("redirect:" + request.getHeader("referer"));
            return mv;
        } catch (Exception e) {
            log.error("Error occurred while deleting admin with adminId: {}", adminId, e);
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while deleting admin: " + e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

            // Redirect to the referrer page if available
            mv.setViewName("redirect:" + request.getHeader("referer"));
            return mv;
        }
    }
}
