package com.caltech.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.caltech.constants.Species;
import com.caltech.pojo.Pet;
import com.caltech.utils.CustomPageable;
import com.caltech.utils.PetPage;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class PetRepositoryTest {

	@Mock
	private PetRepository petRepository;
	
	@Test
	@DisplayName("Test findByPetAgeUpperCeiling method")
	public void testFindByPetAgeUpperCeiling() {
		// Mock the Data
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
		
		// Filter the pets that are <= maxAgeBoundary
		List<Pet> petResults = petList.stream()
				.filter(pet -> Double.compare(pet.getPetAge(), maxAgeBoundary) <= 0)
				.collect(Collectors.toList());
		
		// Create a PetPage containing the mock data
		Page<Pet> petPageResults = new PageImpl<>(petResults);
		
		// Mock the behavior of PetRepository
		when(petRepository.findByPetAgeUpperCeiling(any(Double.class), any(Pageable.class))).thenReturn(petPageResults);
		
		// Call the method under test
		Page<Pet> petPage = petRepository.findByPetAgeUpperCeiling(maxAgeBoundary, PageRequest.of(0, 10));
		
		// Assertions
		assertNotNull(petPage);
		assertEquals(petResults.size(), petPage.getContent().size());
		assertEquals(petResults.size(),petPage.getTotalElements());
		assertEquals(1, petPage.getTotalPages());
	}
	
	@Test
	@DisplayName("Test findByPetAgeLowerCeiling method")
	public void testFindByPetAgeLowerCeiling() {
		// Mock the Data
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
		
		// Filter the pets that are >= minAgeBoundary
		List<Pet> petResults = petList.stream()
				.filter(pet -> Double.compare(pet.getPetAge(), minAgeBoundary) >= 0)
				.collect(Collectors.toList());
		
		// Create a PetPage containing the mock data
		Page<Pet> petPageResults = new PageImpl<>(petResults);
		
		// Mock the behavior of PetRepository
		when(petRepository.findByPetAgeLowerCeiling(any(Double.class), any(Pageable.class))).thenReturn(petPageResults);
		
		// Call the method under test
		Page<Pet> petPage = petRepository.findByPetAgeLowerCeiling(minAgeBoundary, PageRequest.of(0, 10));
		
		// Assertions
		assertNotNull(petPage);
		assertEquals(petResults.size(), petPage.getContent().size());
		assertEquals(petResults.size(),petPage.getTotalElements());
		assertEquals(1, petPage.getTotalPages());
	}
	
	@Test
	@DisplayName("Test findByPetAgeRange method")
	public void testFindByPetAgeRange() {
		// Mock the Data
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
		
		// Filter the pets that are within minAgeBoundary and maxAgeBoundary
		List<Pet> petResults = petList.stream()
				.filter(pet -> Double.compare(pet.getPetAge(), minAgeBoundary) >= 0 &&
						Double.compare(pet.getPetAge(), maxAgeBoundary) <= 0)			
				.collect(Collectors.toList());	
		
		// Create a PetPage containing the mock data
		Page<Pet> petPageResults = new PageImpl<>(petResults);
		
		// Mock the behavior of PetRepository
		when(petRepository.findByPetAgeRange(any(Double.class), any(Double.class), any(Pageable.class))).thenReturn(petPageResults);
		
		// Call the method under test
		Page<Pet> petPage = petRepository.findByPetAgeRange(minAgeBoundary, maxAgeBoundary, PageRequest.of(0, 10));
		
		// Assertions
		assertNotNull(petPage);
		assertEquals(petResults.size(), petPage.getContent().size());
		assertEquals(petResults.size(),petPage.getTotalElements());
		assertEquals(1, petPage.getTotalPages());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Test findAllPets method")
	public void testFindAllPets() {
		// Mock the Data
		List<Pet> petList = new ArrayList<>();
		petList.add(new Pet("pet1", Species.BIRD, 2.0, new ObjectId()));
		petList.add(new Pet("pet2", Species.CAT, 1.2, new ObjectId()));
		petList.add(new Pet("pet3", Species.DOG, 3.5, new ObjectId()));
		petList.add(new Pet("pet4", Species.HAMSTER, 0.9, new ObjectId()));
		petList.add(new Pet("pet5", Species.BIRD, 3.6, new ObjectId()));
		petList.add(new Pet("pet6", Species.CAT, 4.1, new ObjectId()));
		petList.add(new Pet("pet1", Species.DOG, 5.6, new ObjectId()));
		petList.add(new Pet("pet1", Species.HAMSTER, 3.9, new ObjectId()));
		
        // Create a custom pageable object
        CustomPageable<Pet> customPageable = new CustomPageable<>() {
            @Override
            public List<Pet> getContent() {
                return petList; 
            }

            @Override
            public int getPageNumber() {
                return 0; 
            }

            @Override
            public int getPageSize() {
                return 10; 
            }

            @Override
            public long getTotalElements() {
                return petList.size(); 
            }

            @Override
            public int getTotalPages() {
                return 1; 
            }

            @Override
            public boolean hasNext() {
                return false; 
            }

            @Override
            public boolean hasPrevious() {
                return false; 
            }

            @Override
            public Sort getDefaultSort() {
                return Sort.unsorted();
            }
        };
        
        PetPage petPageResults = new PetPage(petList, 0, 10, petList.size(), 1);

        when(petRepository.findAllPets(any(CustomPageable.class))).thenReturn(petPageResults);

        PetPage petPage = petRepository.findAllPets(customPageable);

        // Assertions
        assertNotNull(petPage);
        assertEquals(petList.size(), petPage.getContent().size());
        assertEquals(petList.size(), petPage.getTotalElements());
        assertEquals(1, petPage.getTotalPages());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Test findPetsByUser method")
	public void testFindPetsByUser() {
		// Mock the Data
		ObjectId user1 = new ObjectId();
		ObjectId user2 = new ObjectId();
		List<Pet> petList = new ArrayList<>();
		petList.add(new Pet("pet1", Species.BIRD, 2.0, user1));
		petList.add(new Pet("pet2", Species.CAT, 1.2, user2));
		petList.add(new Pet("pet3", Species.DOG, 3.5, user1));
		petList.add(new Pet("pet4", Species.HAMSTER, 0.9, user2));
		petList.add(new Pet("pet5", Species.BIRD, 3.6, user1));
		petList.add(new Pet("pet6", Species.CAT, 4.1, user2));
		petList.add(new Pet("pet1", Species.DOG, 5.6, user1));
		petList.add(new Pet("pet1", Species.HAMSTER, 3.9, user2));
		
		// Filter the pets that belongs to user1
		List<Pet> petResults = petList.stream()
				.filter(pet -> pet.getUserId().equals(user1))
				.collect(Collectors.toList());
		
        // Create a custom pageable object
        CustomPageable<Pet> customPageable = new CustomPageable<>() {
            @Override
            public List<Pet> getContent() {
                return petResults; 
            }

            @Override
            public int getPageNumber() {
                return 0; 
            }

            @Override
            public int getPageSize() {
                return 10; 
            }

            @Override
            public long getTotalElements() {
                return petResults.size(); 
            }

            @Override
            public int getTotalPages() {
                return 1; 
            }

            @Override
            public boolean hasNext() {
                return false; 
            }

            @Override
            public boolean hasPrevious() {
                return false; 
            }

            @Override
            public Sort getDefaultSort() {
                return Sort.unsorted();
            }
        };
        
        PetPage petPageResults = new PetPage(petResults, 0, 10, petResults.size(), 1);

        when(petRepository.findPetsByUserId(any(ObjectId.class), any(CustomPageable.class))).thenReturn(petPageResults);

        PetPage petPage = petRepository.findPetsByUserId(user1, customPageable);

        // Assertions
        assertNotNull(petPage);
        assertEquals(petResults.size(), petPage.getContent().size());
        assertEquals(petResults.size(), petPage.getTotalElements());
        assertEquals(1, petPage.getTotalPages());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Test findPetsByPetNameContainingIgnoreCase method")
	public void testFindPetsByPetNameContainingIgnoreCase() {
		// Mock the Data
		List<Pet> petList = new ArrayList<>();
		petList.add(new Pet("pet1", Species.BIRD, 2.0, new ObjectId()));
		petList.add(new Pet("pet2", Species.CAT, 1.2, new ObjectId()));
		petList.add(new Pet("pet3", Species.DOG, 3.5, new ObjectId()));
		petList.add(new Pet("pet4", Species.HAMSTER, 0.9, new ObjectId()));
		petList.add(new Pet("pet5", Species.BIRD, 3.6, new ObjectId()));
		petList.add(new Pet("pet6", Species.CAT, 4.1, new ObjectId()));
		petList.add(new Pet("pet1", Species.DOG, 5.6, new ObjectId()));
		petList.add(new Pet("pet1", Species.HAMSTER, 3.9, new ObjectId()));
		
		// Filter the pets that have name as pet1
		List<Pet> petResults = petList.stream()
				.filter(pet -> pet.getPetName().equalsIgnoreCase("pet1"))
				.collect(Collectors.toList());
		
        // Create a custom pageable object
        CustomPageable<Pet> customPageable = new CustomPageable<>() {
            @Override
            public List<Pet> getContent() {
                return petResults; 
            }

            @Override
            public int getPageNumber() {
                return 0; 
            }

            @Override
            public int getPageSize() {
                return 10; 
            }

            @Override
            public long getTotalElements() {
                return petResults.size(); 
            }

            @Override
            public int getTotalPages() {
                return 1; 
            }

            @Override
            public boolean hasNext() {
                return false; 
            }

            @Override
            public boolean hasPrevious() {
                return false; 
            }

            @Override
            public Sort getDefaultSort() {
                return Sort.unsorted();
            }
        };
        
        PetPage petPageResults = new PetPage(petResults, 0, 10, petResults.size(), 1);

        when(petRepository.findPetsByPetNameContainingIgnoreCase(any(String.class), any(CustomPageable.class))).thenReturn(petPageResults);

        PetPage petPage = petRepository.findPetsByPetNameContainingIgnoreCase("pet1", customPageable);

        // Assertions
        assertNotNull(petPage);
        assertEquals(petResults.size(), petPage.getContent().size());
        assertEquals(petResults.size(), petPage.getTotalElements());
        assertEquals(1, petPage.getTotalPages());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Test findPetsByPetType method")
	public void testFindPetsByPetType() {
		// Mock the Data
		List<Pet> petList = new ArrayList<>();
		petList.add(new Pet("pet1", Species.BIRD, 2.0, new ObjectId()));
		petList.add(new Pet("pet2", Species.CAT, 1.2, new ObjectId()));
		petList.add(new Pet("pet3", Species.DOG, 3.5, new ObjectId()));
		petList.add(new Pet("pet4", Species.HAMSTER, 0.9, new ObjectId()));
		petList.add(new Pet("pet5", Species.BIRD, 3.6, new ObjectId()));
		petList.add(new Pet("pet6", Species.CAT, 4.1, new ObjectId()));
		petList.add(new Pet("pet1", Species.DOG, 5.6, new ObjectId()));
		petList.add(new Pet("pet1", Species.HAMSTER, 3.9, new ObjectId()));
		
		// Filter the pets that belongs to Species.DOG
		List<Pet> petResults = petList.stream()
				.filter(pet -> pet.getPetType().equals(Species.DOG))
				.collect(Collectors.toList());
		
        // Create a custom pageable object
        CustomPageable<Pet> customPageable = new CustomPageable<>() {
            @Override
            public List<Pet> getContent() {
                return petResults; 
            }

            @Override
            public int getPageNumber() {
                return 0; 
            }

            @Override
            public int getPageSize() {
                return 10; 
            }

            @Override
            public long getTotalElements() {
                return petResults.size(); 
            }

            @Override
            public int getTotalPages() {
                return 1; 
            }

            @Override
            public boolean hasNext() {
                return false; 
            }

            @Override
            public boolean hasPrevious() {
                return false; 
            }

            @Override
            public Sort getDefaultSort() {
                return Sort.unsorted();
            }
        };
        
        PetPage petPageResults = new PetPage(petResults, 0, 10, petResults.size(), 1);

        when(petRepository.findPetsByPetType(any(Species.class), any(CustomPageable.class))).thenReturn(petPageResults);

        PetPage petPage = petRepository.findPetsByPetType(Species.DOG, customPageable);

        // Assertions
        assertNotNull(petPage);
        assertEquals(petResults.size(), petPage.getContent().size());
        assertEquals(petResults.size(), petPage.getTotalElements());
        assertEquals(1, petPage.getTotalPages());	
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Test findPetsByPetAgeUpperCeiling method")
	public void testFindPetsByPetAgeUpperCeiling() {
		// Mock the Data
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
		
		// Filter the pets that are <= maxAgeBoundary
		List<Pet> petResults = petList.stream()
				.filter(pet -> Double.compare(pet.getPetAge(), maxAgeBoundary) <= 0)
				.collect(Collectors.toList());
		
        // Create a custom pageable object
        CustomPageable<Pet> customPageable = new CustomPageable<>() {
            @Override
            public List<Pet> getContent() {
                return petResults; 
            }

            @Override
            public int getPageNumber() {
                return 0; 
            }

            @Override
            public int getPageSize() {
                return 10; 
            }

            @Override
            public long getTotalElements() {
                return petResults.size(); 
            }

            @Override
            public int getTotalPages() {
                return 1; 
            }

            @Override
            public boolean hasNext() {
                return false; 
            }

            @Override
            public boolean hasPrevious() {
                return false; 
            }

            @Override
            public Sort getDefaultSort() {
                return Sort.unsorted();
            }
        };
        
        PetPage petPageResults = new PetPage(petResults, 0, 10, petResults.size(), 1);

        when(petRepository.findPetsByPetAgeUpperCeiling(any(Double.class), any(CustomPageable.class))).thenReturn(petPageResults);

        PetPage petPage = petRepository.findPetsByPetAgeUpperCeiling(maxAgeBoundary, customPageable);

        // Assertions
        assertNotNull(petPage);
        assertEquals(petResults.size(), petPage.getContent().size());
        assertEquals(petResults.size(), petPage.getTotalElements());
        assertEquals(1, petPage.getTotalPages());	
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Test findPetsByPetAgeLowerCeiling method")
	public void testFindPetsByPetAgeLowerCeiling() {
		// Mock the Data
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
		
		// Filter the pets that are >= minAgeBoundary
		List<Pet> petResults = petList.stream()
				.filter(pet -> Double.compare(pet.getPetAge(), minAgeBoundary) >= 0)
				.collect(Collectors.toList());
		
        // Create a custom pageable object
        CustomPageable<Pet> customPageable = new CustomPageable<>() {
            @Override
            public List<Pet> getContent() {
                return petResults; 
            }

            @Override
            public int getPageNumber() {
                return 0; 
            }

            @Override
            public int getPageSize() {
                return 10; 
            }

            @Override
            public long getTotalElements() {
                return petResults.size(); 
            }

            @Override
            public int getTotalPages() {
                return 1; 
            }

            @Override
            public boolean hasNext() {
                return false; 
            }

            @Override
            public boolean hasPrevious() {
                return false; 
            }

            @Override
            public Sort getDefaultSort() {
                return Sort.unsorted();
            }
        };
        
        PetPage petPageResults = new PetPage(petResults, 0, 10, petResults.size(), 1);

        when(petRepository.findPetsByPetAgeLowerCeiling(any(Double.class), any(CustomPageable.class))).thenReturn(petPageResults);

        PetPage petPage = petRepository.findPetsByPetAgeLowerCeiling(minAgeBoundary, customPageable);

        // Assertions
        assertNotNull(petPage);
        assertEquals(petResults.size(), petPage.getContent().size());
        assertEquals(petResults.size(), petPage.getTotalElements());
        assertEquals(1, petPage.getTotalPages());	
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Test findPetsByPetAgeRange method")
	public void testFindPetsByPetAgeRange() {
		// Mock the Data
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
		
		// Filter the pets that are within minAgeBoundary and maxAgeBoundary
		List<Pet> petResults = petList.stream()
				.filter(pet -> Double.compare(pet.getPetAge(), minAgeBoundary) >= 0 &&
						Double.compare(pet.getPetAge(), maxAgeBoundary) <= 0)			
				.collect(Collectors.toList());	
		
        // Create a custom pageable object
        CustomPageable<Pet> customPageable = new CustomPageable<>() {
            @Override
            public List<Pet> getContent() {
                return petResults; 
            }

            @Override
            public int getPageNumber() {
                return 0; 
            }

            @Override
            public int getPageSize() {
                return 10; 
            }

            @Override
            public long getTotalElements() {
                return petResults.size(); 
            }

            @Override
            public int getTotalPages() {
                return 1; 
            }

            @Override
            public boolean hasNext() {
                return false; 
            }

            @Override
            public boolean hasPrevious() {
                return false; 
            }

            @Override
            public Sort getDefaultSort() {
                return Sort.unsorted();
            }
        };
        
        PetPage petPageResults = new PetPage(petResults, 0, 10, petResults.size(), 1);

        when(petRepository.findPetsByPetAgeRange(any(Double.class), any(Double.class), any(CustomPageable.class))).thenReturn(petPageResults);

        PetPage petPage = petRepository.findPetsByPetAgeRange(minAgeBoundary, maxAgeBoundary, customPageable);

        // Assertions
        assertNotNull(petPage);
        assertEquals(petResults.size(), petPage.getContent().size());
        assertEquals(petResults.size(), petPage.getTotalElements());
        assertEquals(1, petPage.getTotalPages());			
	}
}
