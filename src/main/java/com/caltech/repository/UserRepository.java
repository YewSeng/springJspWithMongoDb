package com.caltech.repository;

import java.util.List;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import com.caltech.pojo.User;
import com.caltech.utils.UserPage;
import com.caltech.utils.CustomPageable;

@Repository
public interface UserRepository extends MongoRepository<User, ObjectId>, UniqueUsernameRepository<User> {

    // Instantiate logger
    Logger log = LoggerFactory.getLogger(UserRepository.class);
    
    Page<User> findAll(Pageable pageable);
    
    // Custom query to find users by name containing the specified string
    @Query("{'username': {$regex : ?0, $options: 'i'}}")
    Page<User> findUsersByUsernameContaining(String username, Pageable pageable); 

    // Custom query to find users by name containing the specified string
    @Query("{'name': {$regex : ?0, $options: 'i'}}")
    Page<User> findUsersByNameContaining(String name, Pageable pageable); 
    
    default UserPage findAllUsers(CustomPageable<User> pageable) {        
        // Create PageRequest based on CustomPageable and default sort
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getDefaultSort());

        // Retrieve Page<User> from the database
        Page<User> page = findAll(pageableRequest);
        
        // Extract content from the Page<User>
        List<User> content = page.getContent();

        // Extract total elements and calculate total pages
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        // Create CustomPage<User> object and return
        UserPage customPage = new UserPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        // Log the results
        log.info("[UserRepository] Fetching results from CustomPage Object: {}", customPage);
       
        // Create CustomPage<User> object and return
        return customPage;
    }
    
    default UserPage findUsersByUsername(String username, CustomPageable<User> pageable) { // Adjusted method signature
        // Create PageRequest based on CustomPageable and default sort
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getDefaultSort());

        // Retrieve Page<User> from the database
        Page<User> page = findUsersByUsernameContaining(username, pageableRequest);
        
        // Extract content from the Page<User>
        List<User> content = page.getContent();

        // Extract total elements and calculate total pages
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        // Create UserPage object and return
        UserPage customPage = new UserPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        // Log the results
        log.info("[UserRepository] Fetching results of user with username <{}> from CustomPage Object: {}", username, customPage);
       
        // Create UserPage object and return
        return customPage;
    }
    
    default UserPage findUsersByName(String name, CustomPageable<User> pageable) { // Adjusted method signature
        // Create PageRequest based on CustomPageable and default sort
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getDefaultSort());

        // Retrieve Page<User> from the database
        Page<User> page = findUsersByNameContaining(name, pageableRequest);
        
        // Extract content from the Page<User>
        List<User> content = page.getContent();

        // Extract total elements and calculate total pages
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        // Create UserPage object and return
        UserPage customPage = new UserPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        // Log the results
        log.info("[UserRepository] Fetching results of user with name <{}> from CustomPage Object: {}", name, customPage);
       
        // Create CustomPage<User> object and return
        return customPage;
    }
}
