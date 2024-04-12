package com.caltech.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.servlet.ModelAndView;

import com.caltech.config.JwtGeneratorValidator;
import com.caltech.service.AdminService;
import com.caltech.service.DefaultUserServiceImplementation;
import com.caltech.service.DoctorService;
import com.caltech.service.SuperAdminService;
import com.caltech.service.UserService;

@ExtendWith(MockitoExtension.class)
public class DefaultControllerTest {

	@Mock
	private JwtGeneratorValidator jwtValidator;
	
	/*@Mock
	private AuthenticationManager authenticationManager;*/
	
	@Mock
	private SuperAdminService superAdminService;
	
	@Mock
	private AdminService adminService;
	
	@Mock
	private DoctorService doctorService;
	
	@Mock
	private UserService userService;
	
	@Mock
	private DefaultUserServiceImplementation defaultUserServiceImplementation;
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    @DisplayName("Test defaultPage method")
    public void testDefaultPage() {
        // Create mock HttpServletRequest and HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Call the defaultPage method
        DefaultController controller = new DefaultController(jwtValidator/*, authenticationManager*/, defaultUserServiceImplementation, 
        		superAdminService, adminService, doctorService, userService);
        ModelAndView mv = controller.goToIndexPage(request, response);

        // Verify the ModelAndView
        assertEquals("index", mv.getViewName());
    }

}
