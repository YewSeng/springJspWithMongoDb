package com.caltech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.caltech.constants.Species;
import com.caltech.exception.PetNotFoundException;
import com.caltech.exception.UserNotFoundException;
import com.caltech.pojo.Pet;
import com.caltech.pojo.User;
import com.caltech.repository.PetRepository;
import com.caltech.repository.UserRepository;
import com.caltech.utils.CustomPageable;
import com.caltech.utils.PetPage;

@ExtendWith(MockitoExtension.class)
public class PetServiceTest {

	@Mock
	private PetRepository petRepository;
	
	@Mock
	private UserRepository userRepository;
	
	@InjectMocks
	private PetService petService;
	
	@Test
	@DisplayName("Test findById method")
	public void testFindById() {
		ObjectId petId = new ObjectId();
		Pet pet = new Pet("Bingo",Species.DOG, 1.2, new ObjectId());
		when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
		Optional<Pet> optionalPet = petService.findPetById(petId);
		assertTrue(optionalPet.isPresent());
		assertEquals(pet, optionalPet.get());
	}
	
	@Test
	@DisplayName("Test findAllPets method")
	public void testFindAllPets() {
		List<Pet> petList = new ArrayList<>();
		petList.add(new Pet("Bingo",Species.DOG, 1.2, new ObjectId()));
		petList.add(new Pet("Tom",Species.CAT, 0.6, new ObjectId()));
		petList.add(new Pet("Jerry",Species.HAMSTER, 0.2, new ObjectId()));
		petList.add(new Pet("Swift",Species.DOG, 2.2, new ObjectId()));
		PetPage petPage = new PetPage(petList, 0, 10, petList.size(), 1);
		CustomPageable<Pet> customPageable = new PetPage(0,10);
		when(petRepository.findAllPets(customPageable)).thenReturn(petPage);
		PetPage foundPetPage = petService.findAllPets(customPageable);
		assertEquals(petPage, foundPetPage);
		assertEquals(petList.size(), foundPetPage.getContent().size());
	}
	
	@Test
	@DisplayName("Test findPetsByUserId method")
	public void testFindPetsByUserId() {
		ObjectId user1 = new ObjectId();
		ObjectId user2 = new ObjectId();
		List<Pet> petList = new ArrayList<>();
		petList.add(new Pet("Bingo",Species.DOG, 1.2, user1));
		petList.add(new Pet("Tom",Species.CAT, 0.6, user2));
		petList.add(new Pet("Jerry",Species.HAMSTER, 0.2, user2));
		petList.add(new Pet("Swift",Species.DOG, 2.2, user1));
		List<Pet> petResults = petList.stream()
				.filter(pet -> pet.getUserId().equals(user2))
				.collect(Collectors.toList());
		PetPage petPage = new PetPage(petResults, 0, 10, petResults.size(), 1);
		CustomPageable<Pet> customPageable = new PetPage(0,10);
		when(petRepository.findPetsByUserId(user2, customPageable)).thenReturn(petPage);
		PetPage foundPetPage = petService.findPetsByUserId(user2, customPageable);
		assertEquals(petPage, foundPetPage);
		assertEquals(2, foundPetPage.getContent().size());
	}
	
	@Test
	@DisplayName("Test findPetsByPetName method")
	public void testFindPetsByPetName() {
		List<Pet> petList = new ArrayList<>();
		petList.add(new Pet("Bingo",Species.DOG, 1.2, new ObjectId()));
		petList.add(new Pet("Tom",Species.CAT, 0.6, new ObjectId()));
		petList.add(new Pet("Jerry",Species.HAMSTER, 0.2, new ObjectId()));
		petList.add(new Pet("Swift",Species.DOG, 2.2, new ObjectId()));
		List<Pet> petResults = petList.stream()
				.filter(pet -> pet.getPetName().equalsIgnoreCase("tom"))
				.collect(Collectors.toList());
		PetPage petPage = new PetPage(petResults, 0, 10, petResults.size(), 1);
		CustomPageable<Pet> customPageable = new PetPage(0,10);
		when(petRepository.findPetsByPetNameContainingIgnoreCase("Tom",customPageable)).thenReturn(petPage);
		PetPage foundPetPage = petService.findPetsByPetName("Tom", customPageable);
		assertEquals(petPage, foundPetPage);
		assertEquals(petResults.size(), foundPetPage.getContent().size());
	}
	
	@Test
	@DisplayName("Test findPetsByPetType method")
	public void testFindPetsByPetType() {
		List<Pet> petList = new ArrayList<>();
		petList.add(new Pet("Bingo",Species.DOG, 1.2, new ObjectId()));
		petList.add(new Pet("Tom",Species.CAT, 0.6, new ObjectId()));
		petList.add(new Pet("Jerry",Species.HAMSTER, 0.2, new ObjectId()));
		petList.add(new Pet("Swift",Species.DOG, 2.2, new ObjectId()));
		List<Pet> petResults = petList.stream()
				.filter(pet -> pet.getPetType().equals(Species.DOG))
				.collect(Collectors.toList());
		PetPage petPage = new PetPage(petResults, 0, 10, petResults.size(), 1);
		CustomPageable<Pet> customPageable = new PetPage(0,10);
		when(petRepository.findPetsByPetType(Species.DOG,customPageable)).thenReturn(petPage);
		PetPage foundPetPage = petService.findPetsByPetType(Species.DOG, customPageable);
		assertEquals(petPage, foundPetPage);
		assertEquals(petResults.size(), foundPetPage.getContent().size());
	}
	
	@Test
	@DisplayName("Test findPetsByPetAgeUpperCeiling method")
	public void testFindPetsByPetAgeUpperCeiling() {
		double maxAgeBoundary = 3.5;
		List<Pet> petList = new ArrayList<>();
		petList.add(new Pet("pet1", Species.BIRD, 2.0, new ObjectId()));
		petList.add(new Pet("pet2", Species.CAT, 1.2, new ObjectId()));
		petList.add(new Pet("pet3", Species.DOG, 3.5, new ObjectId()));
		petList.add(new Pet("pet4", Species.HAMSTER, 0.9, new ObjectId()));
		petList.add(new Pet("pet5", Species.BIRD, 3.6, new ObjectId()));
		petList.add(new Pet("pet6", Species.CAT, 4.1, new ObjectId()));
		petList.add(new Pet("pet1", Species.DOG, 5.6, new ObjectId()));
		petList.add(new Pet("pet1", Species.HAMSTER, 3.9, new ObjectId()));
		List<Pet> petResults = petList.stream()
				.filter(pet -> Double.compare(pet.getPetAge(), maxAgeBoundary) <= 0)
				.collect(Collectors.toList());
		PetPage petPage = new PetPage(petResults, 0, 10, petResults.size(), 1);
		CustomPageable<Pet> customPageable = new PetPage(0,10);
		when(petRepository.findPetsByPetAgeUpperCeiling(maxAgeBoundary, customPageable)).thenReturn(petPage);
		PetPage foundPetPage = petService.findPetsByPetAgeUpperCeiling(maxAgeBoundary, customPageable);
		assertEquals(petPage, foundPetPage);
		assertEquals(petResults.size(), foundPetPage.getContent().size());
	}
	
	@Test
	@DisplayName("Test findPetsByPetAgeLowerCeiling method")
	public void testFindPetsByPetAgeLowerCeiling() {
		double minAgeBoundary = 3.6;
		List<Pet> petList = new ArrayList<>();
		petList.add(new Pet("pet1", Species.BIRD, 2.0, new ObjectId()));
		petList.add(new Pet("pet2", Species.CAT, 1.2, new ObjectId()));
		petList.add(new Pet("pet3", Species.DOG, 3.5, new ObjectId()));
		petList.add(new Pet("pet4", Species.HAMSTER, 0.9, new ObjectId()));
		petList.add(new Pet("pet5", Species.BIRD, 3.6, new ObjectId()));
		petList.add(new Pet("pet6", Species.CAT, 4.1, new ObjectId()));
		petList.add(new Pet("pet1", Species.DOG, 5.6, new ObjectId()));
		petList.add(new Pet("pet1", Species.HAMSTER, 3.9, new ObjectId()));
		List<Pet> petResults = petList.stream()
				.filter(pet -> Double.compare(pet.getPetAge(), minAgeBoundary) >= 0)
				.collect(Collectors.toList());
        PetPage petPage = new PetPage(petResults, 0, 10, petResults.size(), 1);
        CustomPageable<Pet> customPageable = new PetPage(0,10);
        when(petRepository.findPetsByPetAgeLowerCeiling(minAgeBoundary, customPageable)).thenReturn(petPage);
        PetPage foundPetPage = petService.findPetsByPetAgeLowerCeiling(minAgeBoundary, customPageable);
		assertEquals(petPage, foundPetPage);
		assertEquals(petResults.size(), foundPetPage.getContent().size());
	}
	
	@Test
	@DisplayName("Test findPetsByPetAgeRange method")
	public void testFindPetsByPetAgeRange() {
		double minAgeBoundary = 2.0;
		double maxAgeBoundary = 4.0;
		List<Pet> petList = new ArrayList<>();
		petList.add(new Pet("pet1", Species.BIRD, 2.0, new ObjectId()));
		petList.add(new Pet("pet2", Species.CAT, 1.2, new ObjectId()));
		petList.add(new Pet("pet3", Species.DOG, 3.5, new ObjectId()));
		petList.add(new Pet("pet4", Species.HAMSTER, 0.9, new ObjectId()));
		petList.add(new Pet("pet5", Species.BIRD, 3.6, new ObjectId()));
		petList.add(new Pet("pet6", Species.CAT, 4.1, new ObjectId()));
		petList.add(new Pet("pet1", Species.DOG, 5.6, new ObjectId()));
		petList.add(new Pet("pet1", Species.HAMSTER, 3.9, new ObjectId()));
		List<Pet> petResults = petList.stream()
				.filter(pet -> Double.compare(pet.getPetAge(), minAgeBoundary) >= 0 &&
						Double.compare(pet.getPetAge(), maxAgeBoundary) <= 0)			
				.collect(Collectors.toList());	
        PetPage petPage = new PetPage(petResults, 0, 10, petResults.size(), 1);
        CustomPageable<Pet> customPageable = new PetPage(0,10);
        when(petRepository.findPetsByPetAgeRange(minAgeBoundary, maxAgeBoundary, customPageable)).thenReturn(petPage);
        PetPage foundPetPage = petService.findPetsByPetAgeRange(minAgeBoundary, maxAgeBoundary, customPageable);
		assertEquals(petPage, foundPetPage);
		assertEquals(petResults.size(), foundPetPage.getContent().size());
	}
	
	@Test
	@DisplayName("Test createPet method")
	public void testCreatePet() {
		Pet pet = new Pet("pet1", Species.BIRD, 2.0, new ObjectId());
		when(petRepository.save(pet)).thenReturn(pet);
		Pet createdPet = petService.createPet(pet);
		assertEquals(pet.getPetName(), createdPet.getPetName());
	}
	
	@Test
	@DisplayName("Test updatePet method")
	public void testUpdatePet() throws PetNotFoundException {
		ObjectId petId = new ObjectId();
		ObjectId user1 = new ObjectId();
		ObjectId user2 = new ObjectId();
		Pet pet = new Pet("pet1", Species.BIRD, 2.0, user1);
		Pet updatedPet = new Pet("pet3", Species.DOG, 3.5, user2);
		updatedPet.setPetId(petId);
		when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
		when(petRepository.save(any(Pet.class))).thenAnswer(invocation -> invocation.getArgument(0));
		Pet changedPet = petService.updatePet(petId, updatedPet);
		assertEquals("pet3", changedPet.getPetName());
		assertEquals(Species.DOG, changedPet.getPetType());
		assertEquals(3.5, changedPet.getPetAge());
		assertEquals(user2, changedPet.getUserId());
	    verify(petRepository, times(1)).findById(petId);
	    verify(petRepository, times(1)).save(any(Pet.class));
	}
	
	@Test
	@DisplayName("Test updatePet method - Pet Not Found")
	public void testUpdatePetNotFound() throws PetNotFoundException {
		ObjectId petId = new ObjectId();
		ObjectId user1 = new ObjectId();
		Pet updatedPet = new Pet("pet3", Species.DOG, 3.5, user1);
		updatedPet.setPetId(petId);
		when(petRepository.findById(petId)).thenReturn(Optional.empty());
		PetNotFoundException exception = assertThrows(PetNotFoundException.class, () -> {
            petService.updatePet(petId, updatedPet);
        });   
        assertNotNull(exception); 			
		verify(petRepository, never()).save(any(Pet.class));
	}
	
	@Test
	@DisplayName("Test deletePet method")
	public void testDeletePet() throws PetNotFoundException, UserNotFoundException {
		ObjectId user1 = new ObjectId();
		Pet pet = new Pet("pet3", Species.DOG, 3.5, user1);
	    String uuidString = user1.toString().replace("-", "");
	    ObjectId userId = new ObjectId(uuidString);
	    ObjectId petId = new ObjectId();
	    when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
	    when(userRepository.findById(userId)).thenReturn(Optional.of(new User()));
		petService.deletePet(petId);
		verify(petRepository, times(1)).deleteById(any(ObjectId.class));
	}
	
    @Test
    @DisplayName("Test deletePet method - Pet not found")
    public void testDeletePetPetNotFound() {
        ObjectId petId = new ObjectId();
        when(petRepository.findById(petId)).thenReturn(Optional.empty());
        assertThrows(PetNotFoundException.class, () -> petService.deletePet(petId));
        verify(petRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Test deletePet method - User not found")
    public void testDeletePetUserNotFound() {
        ObjectId petId = new ObjectId();
        ObjectId userId = new ObjectId();
        Pet pet = new Pet("pet1", Species.BIRD, 2.0, userId);
        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundException.class, () -> petService.deletePet(petId));
        verify(petRepository, never()).deleteById(any());
    }

    @Test
    @DisplayName("Test deletePet method - Pet has no associated user")
    public void testDeletePetNoAssociatedUser() throws PetNotFoundException, UserNotFoundException {
        ObjectId petId = new ObjectId();
        Pet pet = new Pet("pet1", Species.BIRD, 2.0, null);
        when(petRepository.findById(petId)).thenReturn(Optional.of(pet));
        petService.deletePet(petId);
        verify(petRepository, times(1)).deleteById(petId);
    }
}
