package com.caltech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import com.caltech.constants.Species;
import com.caltech.exception.UserNotFoundException;
import com.caltech.exception.UsernameAlreadyExistException;
import com.caltech.pojo.Pet;
import com.caltech.pojo.User;
import com.caltech.repository.UserRepository;
import com.caltech.utils.CustomPageable;
import com.caltech.utils.ObjectIdUtils;
import com.caltech.utils.UserPage;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BcryptService bcryptService;
    
    @Mock
    private ObjectIdUtils objectIdUtils;

    @InjectMocks
    private UserService userService;
    
    @Test
    @DisplayName("Test findUserById method")
    public void testFindUserById() {
        ObjectId userId = new ObjectId();
        User user = new User("John", "john_doe", "password", LocalDateTime.now());
        user.setUserId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        Optional<User> foundUserOptional = userService.findUserById(userId);
        assertTrue(foundUserOptional.isPresent());
        assertEquals(user, foundUserOptional.get());
    }

    @Test
    @DisplayName("Test findUserByUsername method")
    public void testFindUserByUsername() {
        String username = "username";
        User user = new User("John", "username", "password", LocalDateTime.now());
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        Optional<User> foundUserOptional = userService.findUserByUsername(username);
        assertTrue(foundUserOptional.isPresent());
        assertEquals(user, foundUserOptional.get());
    }
    
    @Test
    @DisplayName("Test Find All Users")
    public void testFindAllUsers() {
        List<User> userList = new ArrayList<>();
        userList.add(new User("John", "john_doe", "password", LocalDateTime.now()));
        userList.add(new User("Alice", "alice_smith", "password123", LocalDateTime.now()));
        UserPage userPage = new UserPage(userList, 0, 10, userList.size(), 1);

        CustomPageable<User> customPageable = new UserPage(0, 10);
        when(userRepository.findAllUsers(customPageable)).thenReturn(userPage);

        UserPage foundUserPage = userService.findAllUsers(customPageable);
        assertEquals(userPage, foundUserPage);
    }

    @Test
    @DisplayName("Test findUsersByUsername method")
    public void testFindUsersByUsername() {
        String username = "john_doe";
        List<User> userList = new ArrayList<>();
        userList.add(new User("John", "john_doe", "password", LocalDateTime.now()));
        UserPage userPage = new UserPage(userList, 0, 10, userList.size(), 1);

        CustomPageable<User> customPageable = new UserPage(0, 10);
        when(userRepository.findUsersByUsername(username, customPageable)).thenReturn(userPage);

        UserPage foundUserPage = userService.findUsersByUsername(username, customPageable);
        assertEquals(userPage, foundUserPage);
    }

    @Test
    @DisplayName("Test findUsersByName method")
    public void testFindUsersByName() {
        String name = "John";
        List<User> userList = new ArrayList<>();
        userList.add(new User("John", "john_doe", "password", LocalDateTime.now()));
        UserPage userPage = new UserPage(userList, 0, 10, userList.size(), 1);

        CustomPageable<User> customPageable = new UserPage(0, 10);
        when(userRepository.findUsersByName(name, customPageable)).thenReturn(userPage);

        UserPage foundUserPage = userService.findUsersByName(name, customPageable);
        assertEquals(userPage, foundUserPage);
    }

    @Test
    @DisplayName("Test verifyExistingUsername method")
    public void testVerifyExistingUsername() {
        String username = "john_doe";
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(new User()));

        assertTrue(userService.verifyExistingUsername(username));
    }

    @Test
    @DisplayName("Test authenticateUser method - Correct Password")
    public void testAuthenticateUserWithCorrectPassword() {
        String username = "john_doe";
        String password = "password";
        User user = new User("John", "john_doe", bcryptService.hashPassword(password), LocalDateTime.now());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(bcryptService.verifyPassword(password, user.getPassword())).thenReturn(true);

        assertTrue(userService.authenticateUser(username, password));
    }

    @Test
    @DisplayName("Test authenticateUser method - Incorrect Password")
    public void testAuthenticateUserWithIncorrectPassword() {
        String username = "john_doe";
        String password = "password";
        User user = new User("John", "john_doe", bcryptService.hashPassword(password), LocalDateTime.now());

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
        when(bcryptService.verifyPassword(password, user.getPassword())).thenReturn(false);

        assertFalse(userService.authenticateUser(username, password));
    }

    @Test
    @DisplayName("Test createUser method")
    public void testCreateUser() {
        User user = new User("John", "john_doe", "password", LocalDateTime.now());
        User hashedUser = new User(user.getName(), user.getUsername(), bcryptService.hashPassword(user.getPassword()), user.getRegistrationDate());

        when(userRepository.save(Mockito.any(User.class))).thenReturn(hashedUser);

        User createdUser = userService.createUser(user);
        assertEquals(hashedUser.getPassword(), createdUser.getPassword());
    }

    @Test
    @DisplayName("Test updateUser method")
    public void testUpdateUser() throws UserNotFoundException, UsernameAlreadyExistException {
        ObjectId userId = new ObjectId();
        User existingUser = new User("John", "john_doe", "password", LocalDateTime.now());
        existingUser.setUserId(userId);

        User updatedUser = new User("John Doe", "johnny", "newpassword", LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.save(Mockito.any(User.class))).thenReturn(updatedUser);
        when(userRepository.findByUsername(updatedUser.getUsername())).thenReturn(Optional.empty());

        User returnedUser = userService.updateUser(userId, updatedUser);
        assertEquals(updatedUser, returnedUser);
    }

    @Test
    @DisplayName("Test updateUser method - User Not Found")
    public void testUpdateUserNotFound() throws UserNotFoundException, UsernameAlreadyExistException {
        ObjectId userId = new ObjectId();
        User updatedUser = new User("John Doe", "johnny", "newpassword", LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.updateUser(userId, updatedUser);
        });
        
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Test updateUser method - Username Already Exists")
    public void testUpdateUserUsernameAlreadyExists() throws UserNotFoundException, UsernameAlreadyExistException {
        ObjectId userId = new ObjectId();
        User existingUser = new User("John", "john_doe", "password", LocalDateTime.now());
        existingUser.setUserId(userId);

        User updatedUser = new User("John Doe", "johnny", "newpassword", LocalDateTime.now());

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userRepository.findByUsername(updatedUser.getUsername())).thenReturn(Optional.of(new User()));

        UsernameAlreadyExistException exception = assertThrows(UsernameAlreadyExistException.class, () -> {
            userService.updateUser(userId, updatedUser);
        });
        
        assertNotNull(exception);
    }

    @Test
    @DisplayName("Test deleteUser method")
    public void testDeleteUser() throws UserNotFoundException {
        ObjectId userId = new ObjectId();
        User user = new User("John", "john_doe", "password", LocalDateTime.now());
        user.setUserId(userId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUser(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    @DisplayName("Test deleteUser method - User Id Not Found")
    public void testDeleteUserNotFound() throws UserNotFoundException {
        ObjectId userId = new ObjectId();

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(userId);
        });
        
        assertNotNull(exception);

        verify(userRepository, never()).deleteById(userId);
    }
    
    @Test
    @DisplayName("Test addPets method")
    public void testAddPets() throws UserNotFoundException {
    	ObjectId userId = new ObjectId();
    	ObjectId petId = new ObjectId();
    	
    	User user = new User("John", "john_doe", "password", LocalDateTime.now());
    	user.setUserId(userId);
    	
    	Pet pet = new Pet("pet", Species.BIRD, 2.0, new ObjectId());
    	pet.setPetId(petId);
    	
    	pet.setUserId(userId);
    	
    	when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    	
    	userService.addPets(userId, pet);
        verify(userRepository).save(user);
        assertTrue(user.getPetIds().contains(petId));
    }
    
    @Test
    @DisplayName("Test addPets method - user does not exist")
    public void testAddPetsUserNotFound() {
        ObjectId userId = new ObjectId();
        ObjectId petId = new ObjectId();

        Pet pet = new Pet("pet", Species.BIRD, 2.0, new ObjectId());
        pet.setPetId(petId);
        
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        
        assertThrows(UserNotFoundException.class, () -> userService.addPets(userId, pet));
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).save(any());
    }
}
