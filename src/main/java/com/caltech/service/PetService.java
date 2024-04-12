package com.caltech.service;

import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.caltech.constants.Species;
import com.caltech.exception.PetNotFoundException;
import com.caltech.exception.UserNotFoundException;
import com.caltech.pojo.Pet;
import com.caltech.pojo.User;
import com.caltech.repository.PetRepository;
import com.caltech.repository.UserRepository;
import com.caltech.utils.CustomPageable;
import com.caltech.utils.ObjectIdUtils;
import com.caltech.utils.PetPage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PetService {

	private final UserRepository userRepository;
	private final PetRepository petRepository;
	
	@Autowired
	public PetService(UserRepository userRepository, PetRepository petRepository) {
		this.userRepository = userRepository;
		this.petRepository = petRepository;
	}
	
	public Optional<Pet> findPetById(ObjectId petId) {
		return petRepository.findById(petId);
	}
	
	public PetPage findAllPets(CustomPageable<Pet> pageable) {
		PetPage customPage = petRepository.findAllPets(pageable);
		if (customPage == null) {
            log.warn("CustomPage is null");
            return new PetPage(new ArrayList<>(), 0, 0, 0, 0);
		}
        log.info("Fetching all pets with pageable: {}", customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} pets", customPage.getContent().size());
        customPage.getContent().forEach(pet -> log.info("Pet: {}", pet.getPetName()));
        return customPage;       
	}
	
	public PetPage findPetsByUserId(ObjectId userId, CustomPageable<Pet> pageable) {
		PetPage customPage = petRepository.findPetsByUserId(userId, pageable);
		if (customPage == null) {
            log.warn("CustomPage is null");
            return new PetPage(new ArrayList<>(), 0, 0, 0, 0);
		}
        log.info("Fetching all pets with pageable: {}", customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} pets", customPage.getContent().size());
        customPage.getContent().forEach(pet -> log.info("Pet: {}", pet.getPetName()));
        return customPage;
	}
	
	public PetPage findPetsByPetName(String petName, CustomPageable<Pet> pageable) {
		PetPage customPage = petRepository.findPetsByPetNameContainingIgnoreCase(petName, pageable);
		if (customPage == null) {
            log.warn("CustomPage is null");
            return new PetPage(new ArrayList<>(), 0, 0, 0, 0);
		}
        log.info("Fetching all pets with pageable: {}", customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} pets", customPage.getContent().size());
        customPage.getContent().forEach(pet -> log.info("Pet: {}", pet.getPetName()));
        return customPage;
	}
	
	public PetPage findPetsByPetType(Species petType, CustomPageable<Pet> pageable) {
		PetPage customPage = petRepository.findPetsByPetType(petType, pageable);
		if (customPage == null) {
            log.warn("CustomPage is null");
            return new PetPage(new ArrayList<>(), 0, 0, 0, 0);
		}
        log.info("Fetching all pets with pageable: {}", customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} pets", customPage.getContent().size());
        customPage.getContent().forEach(pet -> log.info("Pet: {}", pet.getPetName()));
        return customPage;
	}
	
	public PetPage findPetsByPetAgeUpperCeiling(double maxAge, CustomPageable<Pet> pageable) {
		PetPage customPage = petRepository.findPetsByPetAgeUpperCeiling(maxAge, pageable);
		if (customPage == null) {
            log.warn("CustomPage is null");
            return new PetPage(new ArrayList<>(), 0, 0, 0, 0);
		}
        log.info("Fetching all pets with pageable: {}", customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} pets", customPage.getContent().size());
        customPage.getContent().forEach(pet -> log.info("Pet: {}", pet.getPetName()));
        return customPage;
	}
	
	public PetPage findPetsByPetAgeLowerCeiling(double minAge, CustomPageable<Pet> pageable) {
		PetPage customPage = petRepository.findPetsByPetAgeLowerCeiling(minAge, pageable);
		if (customPage == null) {
            log.warn("CustomPage is null");
            return new PetPage(new ArrayList<>(), 0, 0, 0, 0);
		}
        log.info("Fetching all pets with pageable: {}", customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} pets", customPage.getContent().size());
        customPage.getContent().forEach(pet -> log.info("Pet: {}", pet.getPetName()));
        return customPage;
	}
	
	public PetPage findPetsByPetAgeRange(double minAge, double maxAge, CustomPageable<Pet> pageable) {
		PetPage customPage = petRepository.findPetsByPetAgeRange(minAge, maxAge, pageable);
		if (customPage == null) {
            log.warn("CustomPage is null");
            return new PetPage(new ArrayList<>(), 0, 0, 0, 0);
		}
        log.info("Fetching all pets with pageable: {}", customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} pets", customPage.getContent().size());
        customPage.getContent().forEach(pet -> log.info("Pet: {}", pet.getPetName()));
        return customPage;
	}
	
	public Pet createPet(Pet pet) {
		log.info("Creating a new Pet with name: {}", pet.getPetName());
		return petRepository.save(pet);
	}
	
    public Pet updatePet(ObjectId petId, Pet updatedPet) throws PetNotFoundException {
    	Optional<Pet> optionalPet = petRepository.findById(petId);
        if (optionalPet.isPresent()) {
        	Pet existingPet = optionalPet.get();
            existingPet.setPetName(updatedPet.getPetName());
            existingPet.setPetAge(updatedPet.getPetAge());
            existingPet.setPetType(updatedPet.getPetType());
            existingPet.setUserId(updatedPet.getUserId());
            return petRepository.save(existingPet);
        } else {
        	throw new PetNotFoundException("Pet not found with ID: " + petId);
        }
    }
	
	public void deletePet(ObjectId petId) throws PetNotFoundException, UserNotFoundException {
	    Optional<Pet> optionalPet = petRepository.findById(petId);
	    if (optionalPet.isPresent()) {
	        Pet pet = optionalPet.get();
	        ObjectId userId = pet.getUserId();
	        if (userId != null) {
	            Optional<User> optionalUser = userRepository.findById(userId);
	            if (optionalUser.isPresent()) {
	                User user = optionalUser.get();
	                user.getPetIds().remove(petId);
	                userRepository.save(user);
	            } else {
	                throw new UserNotFoundException("User not found with ID: " + userId);
	            }
	        }
	        petRepository.deleteById(petId);
	    } else {
	        throw new PetNotFoundException("Pet not found with ID: " + petId);
	    }
	}
}
