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
import com.caltech.utils.AdminPage;
import com.caltech.utils.CustomPageable;
import com.caltech.pojo.Admin;


@Repository
public interface AdminRepository extends MongoRepository<Admin, ObjectId>, UniqueUsernameRepository<Admin> {

    // Instantiate logger
    Logger log = LoggerFactory.getLogger(AdminRepository.class);
    
    Page<Admin> findAll(Pageable pageable);
    
    // Custom query to find users by name containing the specified string
    @Query("{'username': {$regex : ?0, $options: 'i'}}")
    Page<Admin> findAdminsByUsernameContaining(String username, Pageable pageable); 

    // Custom query to find users by name containing the specified string
    @Query("{'name': {$regex : ?0, $options: 'i'}}")
    Page<Admin> findAdminsByNameContaining(String name, Pageable pageable); 
    
    default AdminPage findAllAdmins(CustomPageable<Admin> pageable) {        
        // Create PageRequest based on CustomPageable and default sort
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getDefaultSort());

        // Retrieve Page<Admin> from the database
        Page<Admin> page = findAll(pageableRequest);
        
        // Extract content from the Page<Admin>
        List<Admin> content = page.getContent();

        // Extract total elements and calculate total pages
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        // Create CustomPage<Admin> object and return
        AdminPage customPage = new AdminPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        // Log the results
        log.info("[AdminRepository] Fetching results from CustomPage Object: {}", customPage);
       
        // Create CustomPage<Admin> object and return
        return customPage;
    }
    
    default AdminPage findAdminsByUsername(String username, CustomPageable<Admin> pageable) { // Adjusted method signature
        // Create PageRequest based on CustomPageable and default sort
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getDefaultSort());

        // Retrieve Page<Admin> from the database
        Page<Admin> page = findAdminsByUsernameContaining(username, pageableRequest);
        
        // Extract content from the Page<Admin>
        List<Admin> content = page.getContent();

        // Extract total elements and calculate total pages
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        // Create AdminPage object and return
        AdminPage customPage = new AdminPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        // Log the results
        log.info("[AdminRepository] Fetching results of Admin with username <{}> from CustomPage Object: {}", username, customPage);
       
        // Create AdminPage object and return
        return customPage;
    }
    
    default AdminPage findAdminsByName(String name, CustomPageable<Admin> pageable) { // Adjusted method signature
        // Create PageRequest based on CustomPageable and default sort
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                pageable.getDefaultSort());

        // Retrieve Page<Admin> from the database
        Page<Admin> page = findAdminsByNameContaining(name, pageableRequest);
        
        // Extract content from the Page<Admin>
        List<Admin> content = page.getContent();

        // Extract total elements and calculate total pages
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        // Create AdminPage object and return
        AdminPage customPage = new AdminPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        // Log the results
        log.info("[AdminRepository] Fetching results of Admin with name <{}> from CustomPage Object: {}", name, customPage);
       
        // Create CustomPage<Admin> object and return
        return customPage;
    }
}
