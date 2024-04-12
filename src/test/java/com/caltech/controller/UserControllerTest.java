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
import com.caltech.exception.UserNotFoundException;
import com.caltech.exception.UsernameAlreadyExistException;
import com.caltech.pojo.User;
import com.caltech.service.UserService;
import com.caltech.utils.CustomPageable;
import com.caltech.utils.UserPage;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtGeneratorValidator jwtValidator;
    
    @InjectMocks
    private UserController userController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Test goToCreateUserPage method - Success")
    public void testGoToCreateUserPage() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        // Invoke the controller method
        ModelAndView mav = userController.goToCreateUserPage(request, response);
        
        // Additional assertions
        assertEquals("createUser", mav.getViewName());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }
    
    @Test
    @DisplayName("Test createUser method - Success")
    public void testCreateUserSuccess() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock user data
        String name = "John Doe";
        String username = "UserTest1";
        String password = "Newpassword@1";

        // Mock UserService behavior
        when(userService.verifyExistingUsername(anyString())).thenReturn(false);

        // Invoke the controller method
        ModelAndView mav = userController.createUser(name, username, password, request, response, redirectAttributes);

        // Verify behavior
        verify(userService, times(1)).createUser(any(User.class));
        assertEquals("redirect:/api/v1/users/getAllUsers?page=0&size=5", mav.getViewName());

        // Additional assertions
        verify(redirectAttributes).addFlashAttribute(eq("successMessage"), anyString());
        verify(response).setStatus(HttpServletResponse.SC_OK);
    }

    @Test
    @DisplayName("Test createUser method - Username already exists")
    public void testCreateUserUsernameAlreadyExists() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock user data
        String name = "John Doe";
        String username = "UserTest1";
        String password = "Newpassword@1";
        
        // Mock UserService behavior to return true (username already exists)
        UserService userService = mock(UserService.class);
        when(userService.verifyExistingUsername(anyString())).thenReturn(true);

        // Invoke the controller method
        UserController userController = new UserController(userService, jwtValidator);
        ModelAndView mav = userController.createUser(name, username, password, request, response, redirectAttributes);

        // Verify behavior
        verify(userService, never()).createUser(any(User.class)); // Ensure createUser is not called
        assertEquals("redirect:/api/v1/users/createUser", mav.getViewName());
        
        // Verify error message is set correctly
        verify(redirectAttributes).addFlashAttribute("errorMessage", "Username is already taken. Please try again!");

        // Verify status code of the response
        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
    }
    
    @Test
    @DisplayName("Test createUser method - IllegalArgumentException")
    public void testCreateUserIllegalArgumentException() {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = new RedirectAttributesModelMap(); // Use RedirectAttributesModelMap
        
        // Mock user data
        String name = "";
        String username = "UserTest1";
        String password = "Newpassword@1";
        
        // Mock UserService behavior
        UserService userService = mock(UserService.class);

        // Invoke the controller method
        UserController userController = new UserController(userService, jwtValidator);
        ModelAndView mav = userController.createUser(name, username, password, request, response, redirectAttributes);

        // Verify behavior
        verify(userService, never()).createUser(any(User.class)); 
        assertEquals("redirect:/api/v1/users/createUser", mav.getViewName());
        
        // Verify error message is set correctly
        assertEquals("Name cannot be empty", redirectAttributes.getFlashAttributes().get("errorMessage").toString().trim());

        // Verify status code of the response
        verify(response).setStatus(HttpServletResponse.SC_CONFLICT);
        
        // Verify nameError, usernameError, and passwordError are set correctly
        assertEquals("Name cannot be empty", redirectAttributes.getFlashAttributes().get("nameError"));
        assertEquals("", redirectAttributes.getFlashAttributes().get("usernameError"));
        assertEquals("", redirectAttributes.getFlashAttributes().get("passwordError"));
        assertEquals("", redirectAttributes.getFlashAttributes().get("name"));
        assertEquals("UserTest1", redirectAttributes.getFlashAttributes().get("username"));
        assertEquals("Newpassword@1", redirectAttributes.getFlashAttributes().get("password"));
    }
    
    @SuppressWarnings("unchecked")
	@Test
    @DisplayName("Test getAllUsers method")
    public void testGetAllUsers() {
        // Create mock user data
        User user1 = new User();
        user1.setName("User 1");
        User user2 = new User();
        user2.setName("User 2");
        List<User> userList = Arrays.asList(user1, user2);

        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        
        // Mock UserService behavior
        when(userService.findAllUsers(any(CustomPageable.class))).thenReturn(new UserPage(userList, 0, 10, userList.size(), 1));

        // Call the controller method
        ModelAndView mav = userController.getAllUsers(0, 10, request, response);

        // Verify behavior
        assertEquals("viewUsers", mav.getViewName());
        assertEquals(userList, mav.getModel().get("users"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }

    
    @SuppressWarnings("unchecked")
	@Test
    @DisplayName("Test filterUsers method")
    public void testFilterUsers() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock user data
        User user1 = new User();
        user1.setName("User 1");
        User user2 = new User();
        user2.setName("User 2");
        List<User> userList = Arrays.asList(user1, user2);

        // Mock UserService behavior
        when(userService.findUsersByName(any(), any(CustomPageable.class)))
            .thenReturn(new UserPage(userList, 0, 10, userList.size(), 1));

        // Call the controller method
        ModelAndView mav = userController.filterUsers("name", "John", 0, 10, request, response);

        // Verify behavior
        assertEquals("viewUsers", mav.getViewName());
        assertEquals(userList, mav.getModel().get("users"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }
    
    @Test
    @DisplayName("Test filterUsers method - Name Search")
    public void testFilterUsersNameSearch() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock user data
        User user1 = new User();
        user1.setName("John Doe");
        User user2 = new User();
        user2.setName("Jane Smith");
        List<User> userList = Arrays.asList(user1, user2);

        // Mock UserService behavior for name search
        when(userService.findUsersByName(any(), any(CustomPageable.class)))
            .thenReturn(new UserPage(userList, 0, 10, userList.size(), 1));

        // Call the controller method for name search
        ModelAndView mav = userController.filterUsers("name", "John", 0, 10, request, response);

        // Verify behavior for name search
        assertEquals("viewUsers", mav.getViewName());
        assertEquals(userList, mav.getModel().get("users"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }

    @Test
    @DisplayName("Test filterUsers method - Username Search")
    public void testFilterUsersUsernameSearch() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock user data
        User user1 = new User();
        user1.setUsername("john123");
        User user2 = new User();
        user2.setUsername("jane456");
        List<User> userList = Arrays.asList(user1, user2);

        // Mock UserService behavior for username search
        when(userService.findUsersByUsername(any(), any(CustomPageable.class)))
            .thenReturn(new UserPage(userList, 0, 10, userList.size(), 1));

        // Call the controller method for username search
        ModelAndView mav = userController.filterUsers("username", "john", 0, 10, request, response);

        // Verify behavior for username search
        assertEquals("viewUsers", mav.getViewName());
        assertEquals(userList, mav.getModel().get("users"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }

    @Test
    @DisplayName("Test filterUsers method - No Search Term")
    public void testFilterUsersNoSearchTerm() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Call the controller method with empty search term
        ModelAndView mav = userController.filterUsers("name", "", 0, 10, request, response);

        // Verify behavior when no search term is provided
        assertEquals("redirect:/api/v1/users/getAllUsers", mav.getViewName());
    }

    @Test
    @DisplayName("Test filterUsers method - Default Search")
    public void testFilterUsersDefaultSearch() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Create mock user data
        User user1 = new User();
        user1.setName("John Doe");
        User user2 = new User();
        user2.setName("Jane Smith");
        List<User> userList = Arrays.asList(user1, user2);

        // Mock UserService behavior for default search
        when(userService.findAllUsers(any(CustomPageable.class)))
            .thenReturn(new UserPage(userList, 0, 10, userList.size(), 1));

        // Call the controller method for default search
        ModelAndView mav = userController.filterUsers("other", "term", 0, 10, request, response);

        // Verify behavior for default search
        assertEquals("viewUsers", mav.getViewName());
        assertEquals(userList, mav.getModel().get("users"));
        assertEquals(0, mav.getModel().get("pageNumber"));
        assertEquals(10, mav.getModel().get("pageSize"));
        assertEquals(2L, mav.getModel().get("totalElements"));
        assertEquals(1, mav.getModel().get("totalPages"));
    }
    
    @Test
    @DisplayName("Test goToUpdateUserPage method - User found")
    public void testGoToUpdateUserPageUserFound() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Mock user data
        User user = new User();
        user.setName("John Doe");
        user.setUserId(ObjectId.get());

        // Mock UserService behavior
        when(userService.findUserById(any())).thenReturn(Optional.of(user));

        // Call the controller method
        ModelAndView mav = userController.goToUpdateUserPage(user.getUserId(), request, response);

        // Verify behavior
        assertEquals("editUser", mav.getViewName());
        assertTrue(mav.getModel().containsKey("user"));
        assertEquals(user, mav.getModel().get("user"));
    }

    @Test
    @DisplayName("Test goToUpdateUserPage method - User not found")
    public void testGoToUpdateUserPageUserNotFound() {
        // Mock HttpServletRequest, HttpServletResponse
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);

        // Mock UserService behavior
        when(userService.findUserById(any())).thenReturn(Optional.empty());

        // Call the controller method
        ModelAndView mav = userController.goToUpdateUserPage(ObjectId.get(), request, response);

        // Verify behavior
        assertEquals("userNotFound", mav.getViewName());
        assertTrue(mav.getModel().containsKey("errorMessage"));
        assertTrue(mav.getModel().get("errorMessage").toString().contains("User not found with userId:"));
    }
    
    @Test
    @DisplayName("Test updateUser method - Success")
    public void testUpdateUserSuccess() throws UserNotFoundException, UsernameAlreadyExistException {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock user data
        ObjectId userId = ObjectId.get();
        String name = "John Doe";
        String username = "UserTest2";
        String password = "Newpassword@1";

        // Mock UserService behavior
        when(userService.updateUser(any(), any())).thenReturn(new User());

        // Call the controller method
        ModelAndView mav = userController.updateUser(userId, name, username, password, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/users/getAllUsers?page=0&size=5", mav.getViewName());
        verify(redirectAttributes).addFlashAttribute("successMessage", "User with user id " + userId + " is successfully updated.");
        verify(response).setStatus(HttpServletResponse.SC_OK); // Ensure response status is set to OK
    }

    @Test
    @DisplayName("Test updateUser method - UserNotFoundException")
    public void testUpdateUserUserNotFoundException() throws UserNotFoundException, UsernameAlreadyExistException {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock user data
        ObjectId userId = ObjectId.get();
        String name = "John Doe";
        String username = "UserTest2";
        String password = "Newpassword@1";

        // Mock UserService behavior to throw UserNotFoundException
        when(userService.updateUser(any(), any())).thenThrow(new UserNotFoundException("User not found"));

        // Call the controller method
        ModelAndView mav = userController.updateUser(userId, name, username, password, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("userNotFound", mav.getViewName());
        assertEquals("User not found", mav.getModel().get("errorMessage"));
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
    }

    @Test
    @DisplayName("Test updateUser method - Normal Exception")
    public void testUpdateUserNormalException() throws UserNotFoundException, UsernameAlreadyExistException {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock user data
        ObjectId userId = ObjectId.get();
        String name = "John Doe";
        String username = "UserTest2";
        String password = "Newpassword@1";

        // Mock UserService behavior to throw a normal exception
        when(userService.updateUser(any(), any())).thenThrow(new RuntimeException("Some unexpected error occurred"));

        // Call the controller method
        ModelAndView mav = userController.updateUser(userId, name, username, password, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("generalError", mav.getViewName());
        assertEquals("An error occurred while updating user", mav.getModel().get("errorMessage"));
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
    
    @Test
    @DisplayName("Test deleteUser method - Successful Deletion")
    public void testDeleteUserSuccess() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock userId
        ObjectId userId = ObjectId.get();

        // Mock the returning of the User
        when(userService.findUserById(userId)).thenReturn(Optional.of(new User()));

        // Set the referer header in the request mock
        when(request.getHeader("referer")).thenReturn("/api/v1/users/getAllUsers?page=1&size=5");

        // Call the controller method
        ModelAndView mav = userController.deleteUser(userId, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/users/getAllUsers?page=1&size=5", mav.getViewName());
        verify(redirectAttributes).addFlashAttribute("successMessage", "User deleted successfully.");
        verify(response).setStatus(HttpServletResponse.SC_OK); // Verify that setStatus is invoked with SC_OK
        verify(userService).deleteUser(userId);
        verify(redirectAttributes, never()).addFlashAttribute(eq("errorMessage"), anyString());
    }

    @Test
    @DisplayName("Test deleteUser method - User Not Found")
    public void testDeleteUserNotFound() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock userId
        ObjectId userId = ObjectId.get();
        
        // Set the referer header in the request mock
        when(request.getHeader("referer")).thenReturn("/api/v1/users/getAllUsers?page=1&size=5");

        // Mock userService to return Optional.empty() for the user
        when(userService.findUserById(userId)).thenReturn(Optional.empty());
        
        // Call the controller method
        ModelAndView mav = userController.deleteUser(userId, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/users/getAllUsers?page=1&size=5", mav.getViewName());
        verify(redirectAttributes).addFlashAttribute("errorMessage", "User not found with userId: " + userId);
        verify(response).setStatus(HttpServletResponse.SC_NOT_FOUND);
        verify(userService, never()).deleteUser(userId);
        verify(redirectAttributes, never()).addFlashAttribute(eq("successMessage"), anyString());
    }

    @Test
    @DisplayName("Test deleteUser method - Exception")
    public void testDeleteUserException() throws Exception {
        // Mock HttpServletRequest, HttpServletResponse, RedirectAttributes
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        RedirectAttributes redirectAttributes = mock(RedirectAttributes.class);

        // Mock userId
        ObjectId userId = ObjectId.get();

        // Mock the returning of the User
        when(userService.findUserById(userId)).thenReturn(Optional.of(new User()));

        // Stub the referer header
        when(request.getHeader("referer")).thenReturn("/api/v1/users/getAllUsers?page=1&size=5");

        // Mock UserService behavior to throw an exception with the correct message
        doThrow(new RuntimeException("User not found with userId: " + userId)).when(userService).deleteUser(userId);

        // Call the controller method
        ModelAndView mav = userController.deleteUser(userId, request, response, redirectAttributes);

        // Verify behavior
        assertEquals("redirect:/api/v1/users/getAllUsers?page=1&size=5", mav.getViewName());
        verify(redirectAttributes).addFlashAttribute("errorMessage", "An error occurred while deleting user: User not found with userId: " + userId);
        verify(response).setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        
        // Verify that the deleteUser method is invoked with the correct userId
        verify(userService).deleteUser(userId);
    }
}

