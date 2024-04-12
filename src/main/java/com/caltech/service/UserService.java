package com.caltech.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.caltech.exception.UserNotFoundException;
import com.caltech.exception.UsernameAlreadyExistException;
import com.caltech.pojo.Pet;
import com.caltech.pojo.User;
import com.caltech.repository.UserRepository;
import com.caltech.utils.UserPage;
import com.caltech.utils.CustomPageable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;
    private final BcryptService bcryptService;

    @Autowired
    public UserService(UserRepository userRepository, BcryptService bcryptService) {
        this.userRepository = userRepository;
        this.bcryptService = bcryptService;
    }
    
    public Optional<User> findUserById(ObjectId userId) {
    	return userRepository.findById(userId);
    }
    
    public Optional<User> findUserByUsername(String username) {
    	return userRepository.findByUsername(username);
    }
    
    public void addPets(ObjectId userId, Pet pet) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            List<ObjectId> pets = user.getPetIds();
            pets.add(pet.getPetId()); 
            user.setPetIds(pets);
            userRepository.save(user);
        } else {
            throw new UserNotFoundException("User not found with ID: " + userId);
        }
    }
    
    public UserPage findAllUsers(CustomPageable<User> pageable) {
        UserPage customPage = userRepository.findAllUsers(pageable);
        if (customPage == null) {
            log.warn("CustomPage is null");
            // Create and return an empty UserPage
            return new UserPage(new ArrayList<>(), 0, 0, 0, 0);
        }
        log.info("Fetching all users with pageable: {}", customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} users", customPage.getContent().size());
        customPage.getContent().forEach(user -> log.info("User: {}", user.getUsername()));
        return customPage;
    }
    
    public UserPage findUsersByUsername(String username, CustomPageable<User> pageable) {
        UserPage customPage = userRepository.findUsersByUsername(username, pageable);
        if (customPage == null) {
            log.warn("CustomPage is null");
            // Create and return an empty UserPage
            return new UserPage(new ArrayList<>(), 0, 0, 0, 0);
        }
        log.info("Fetching user with username: {} with pageable: {}", username, customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} users", customPage.getContent().size());
        return customPage;
    }

    public UserPage findUsersByName(String name, CustomPageable<User> pageable) {
        UserPage customPage = userRepository.findUsersByName(name, pageable);
        if (customPage == null) {
            log.warn("CustomPage is null");
            // Create and return an empty UserPage
            return new UserPage(new ArrayList<>(), 0, 0, 0, 0);
        }
        log.info("Fetching users with name: {} with pageable: {}", name, customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} users", customPage.getContent().size());
        customPage.getContent().forEach(user -> log.info("User: {}", user.getUsername()));
        return customPage;
    }

    public Boolean verifyExistingUsername(String username) {
    	return userRepository.findByUsername(username).isPresent();
    }
    
    public Boolean authenticateUser(String username, String password) {
    	log.info("Autenticating User with Username: {}, Password: {}", username, password);
        boolean correctPassword = false;       
        Optional<User> userOptional = userRepository.findByUsername(username);      
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String hashPasswordFromDb = user.getPassword();
            correctPassword = bcryptService.verifyPassword(password, hashPasswordFromDb);
        }
        
        return correctPassword;
    }

    public User createUser(User user) {
    	log.info("Creating a new User with username: {}", user.getUsername());
        user.setPassword(bcryptService.hashPassword(user.getPassword()));
        return userRepository.save(user);
    }
    
    public User updateUser(ObjectId userId, User updatedUser) throws UserNotFoundException, UsernameAlreadyExistException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setName(updatedUser.getName());
            existingUser.setPassword(bcryptService.hashPassword(updatedUser.getPassword()));
            if (!existingUser.getUsername().equals(updatedUser.getUsername())) {
                // Check if the new username already exists in the database
                if (verifyExistingUsername(updatedUser.getUsername())) {
                    throw new UsernameAlreadyExistException("Username already exists");
                }
                existingUser.setUsername(updatedUser.getUsername());
            }
            return userRepository.save(existingUser);
        } else {
            throw new UserNotFoundException("User not found with userId: " + userId);
        }
    }
    
    public void deleteUser(ObjectId userId) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            log.info("Deleting user with userId: {}", userId);
            if (user.getPetIds() != null) {
                user.getPetIds().clear(); 
            }
            userRepository.deleteById(userId);
            log.info("User with userId: {} deleted successfully", userId);
        } else {
            log.error("User not found with userId: {}", userId);
            throw new UserNotFoundException("User not found with userId: " + userId);
        }
    }
}

