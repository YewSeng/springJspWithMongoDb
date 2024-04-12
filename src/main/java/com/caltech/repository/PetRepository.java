package com.caltech.repository;

import java.util.List;
import java.util.UUID;
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
import com.caltech.constants.Species;
import com.caltech.pojo.Pet;
import com.caltech.utils.CustomPageable;
import com.caltech.utils.PetPage;

@Repository
public interface PetRepository extends MongoRepository<Pet, ObjectId>{

    // Instantiate logger
    Logger log = LoggerFactory.getLogger(PetRepository.class);
    
    Page<Pet> findAll(Pageable pageable);
    
    Page<Pet> findByUserId(ObjectId userId, Pageable pageable);
    
    Page<Pet> findByPetNameContainingIgnoreCase(String petName, Pageable pageable);
    
    Page<Pet> findByPetType(Species petType, Pageable pageable);
    
    @Query("{'petAge': {$lte: ?0}}")
    Page<Pet> findByPetAgeUpperCeiling(double maxAge, Pageable pageable);
    
    @Query("{'petAge': {$gte: ?0}}")
    Page<Pet> findByPetAgeLowerCeiling(double minAge, Pageable pageable);
    
    @Query("{'petAge': {$gte: ?0, $lte: ?1}}")
    Page<Pet> findByPetAgeRange(double minAge, double maxAge, Pageable pageable);
    
    default PetPage findAllPets(CustomPageable<Pet> pageable) {
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "petType"));

        Page<Pet> page = findAll(pageableRequest);
        
        List<Pet> content = page.getContent();
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        PetPage customPage = new PetPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        log.info("[PetRepository] Fetching results from CustomPage Object: {}", customPage);
       
        return customPage;
    }
    
    default PetPage findPetsByUserId(ObjectId user1, CustomPageable<Pet> pageable) {
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "petType"));
        
        Page<Pet> page = findByUserId(user1, pageableRequest);
        
        List<Pet> content = page.getContent();
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        PetPage customPage = new PetPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        log.info("[PetRepository] Fetching pet results belonging to user with User ID: <{}> from CustomPage Object: {}", user1, customPage);
       
        return customPage;
    }
    
    default PetPage findPetsByPetNameContainingIgnoreCase(String petName, CustomPageable<Pet> pageable) {
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "petType"));
        
        Page<Pet> page = findByPetNameContainingIgnoreCase(petName, pageableRequest);
        
        List<Pet> content = page.getContent();
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        PetPage customPage = new PetPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        log.info("[PetRepository] Fetching pet results with name: <{}> from CustomPage Object: {}", petName, customPage);
       
        return customPage;
    }
    
    default PetPage findPetsByPetType(Species petType, CustomPageable<Pet> pageable) {
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "petName"));
        
        Page<Pet> page = findByPetType(petType, pageableRequest);
        
        List<Pet> content = page.getContent();
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        PetPage customPage = new PetPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        log.info("[PetRepository] Fetching pet results with species: <{}> from CustomPage Object: {}", petType, customPage);
       
        return customPage;
    }
    
    default PetPage findPetsByPetAgeUpperCeiling(double maxAge, CustomPageable<Pet> pageable) {
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "petName"));
        
        Page<Pet> page = findByPetAgeUpperCeiling(maxAge, pageableRequest);
        
        List<Pet> content = page.getContent();
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        PetPage customPage = new PetPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        log.info("[PetRepository] Fetching pet results with age less than or equals to : <{}> from CustomPage Object: {}", maxAge, customPage);
       
        return customPage;
    }
    
    default PetPage findPetsByPetAgeLowerCeiling(double minAge, CustomPageable<Pet> pageable) {
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "petName"));
        
        Page<Pet> page = findByPetAgeLowerCeiling(minAge, pageableRequest);
        
        List<Pet> content = page.getContent();
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        PetPage customPage = new PetPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        log.info("[PetRepository] Fetching pet results with age greater than or equals to: <{}> from CustomPage Object: {}", minAge, customPage);
       
        return customPage;
    }
    
    default PetPage findPetsByPetAgeRange(double minAge, double maxAge, CustomPageable<Pet> pageable) {
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "petName"));
        
        Page<Pet> page = findByPetAgeRange(minAge, maxAge, pageableRequest);
        
        List<Pet> content = page.getContent();
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        PetPage customPage = new PetPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        log.info("[PetRepository] Fetching pet results with age between (inclusive): <{}> and (inclusive): <{}> from CustomPage Object: {}", minAge, maxAge, customPage);
       
        return customPage;
    }
}
