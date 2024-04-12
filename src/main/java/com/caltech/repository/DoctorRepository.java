package com.caltech.repository;

import java.util.List;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import com.caltech.utils.DoctorPage;
import com.caltech.utils.CustomPageable;
import com.caltech.pojo.Doctor;

@Repository
public interface DoctorRepository extends MongoRepository<Doctor, ObjectId>, UniqueUsernameRepository<Doctor> {

    // Instantiate logger
    Logger log = LoggerFactory.getLogger(DoctorRepository.class);
    
    Page<Doctor> findAll(Pageable pageable);
    
    
    // Custom query to find doctors by name containing the specified string
    @Query("{'username': {$regex : ?0, $options: 'i'}}")
    Page<Doctor> findDoctorsByUsernameContaining(String username, Pageable pageable); 

    // Custom query to find doctors by name containing the specified string
    @Query("{'name': {$regex : ?0, $options: 'i'}}")
    Page<Doctor> findDoctorsByNameContaining(String name, Pageable pageable); 
    
    // Custom query to find doctors by status
    @Query("{'status': ?0}")
    Page<Doctor> findDoctorsByStatus(String status, Pageable pageable);
    
    default DoctorPage findAllDoctors(CustomPageable<Doctor> pageable) {        
        // Create PageRequest based on CustomPageable and default sort
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "status"));

        // Retrieve Page<Doctor> from the database
        Page<Doctor> page = findAll(pageableRequest);
        
        // Extract content from the Page<Doctor>
        List<Doctor> content = page.getContent();

        // Extract total elements and calculate total pages
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        // Create CustomPage<Doctor> object and return
        DoctorPage customPage = new DoctorPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        // Log the results
        log.info("[DoctorRepository] Fetching results from CustomPage Object: {}", customPage);
       
        // Create CustomPage<Doctor> object and return
        return customPage;
    }
    
    default DoctorPage findDoctorsByUsername(String username, CustomPageable<Doctor> pageable) { // Adjusted method signature
        // Create PageRequest based on CustomPageable and default sort
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "status"));

        // Retrieve Page<Doctor> from the database
        Page<Doctor> page = findDoctorsByUsernameContaining(username, pageableRequest);
        
        // Extract content from the Page<Doctor>
        List<Doctor> content = page.getContent();

        // Extract total elements and calculate total pages
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        // Create DoctorPage object and return
        DoctorPage customPage = new DoctorPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        // Log the results
        log.info("[DoctorRepository] Fetching results of Doctor with username <{}> from CustomPage Object: {}", username, customPage);
       
        // Create DoctorPage object and return
        return customPage;
    }
    
    default DoctorPage findDoctorsByName(String name, CustomPageable<Doctor> pageable) { // Adjusted method signature
        // Create PageRequest based on CustomPageable and default sort
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "status"));

        // Retrieve Page<Doctor> from the database
        Page<Doctor> page = findDoctorsByNameContaining(name, pageableRequest);
        
        // Extract content from the Page<Doctor>
        List<Doctor> content = page.getContent();

        // Extract total elements and calculate total pages
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        // Create DoctorPage object and return
        DoctorPage customPage = new DoctorPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        // Log the results
        log.info("[DoctorRepository] Fetching results of Doctor with name <{}> from CustomPage Object: {}", name, customPage);
       
        // Create CustomPage<Doctor> object and return
        return customPage;
    }
    
    default DoctorPage findDoctorsByStatus(String status, CustomPageable<Doctor> pageable) {
        // Create PageRequest based on CustomPageable and default sort
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "status"));

        // Retrieve Page<Doctor> from the database
        Page<Doctor> page = findDoctorsByStatus(status, pageableRequest);
        
        
        // Extract content from the Page<Doctor>
        List<Doctor> content = page.getContent();

        // Extract total elements and calculate total pages
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        // Create DoctorPage object and return
        DoctorPage customPage = new DoctorPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        // Log the results
        log.info("[DoctorRepository] Fetching results of Doctor with status <{}> from CustomPage Object: {}", status, customPage);
       
        // Create DoctorPage object and return
        return customPage;
    }
}
