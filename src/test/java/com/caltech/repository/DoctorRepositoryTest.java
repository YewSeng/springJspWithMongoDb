package com.caltech.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
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

import com.caltech.constants.Status;
import com.caltech.pojo.Doctor;
import com.caltech.utils.CustomPageable;
import com.caltech.utils.DoctorPage;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class DoctorRepositoryTest {

	@Mock
	private DoctorRepository doctorRepository;
	
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Test findAllDoctors method")
	public void testFindAllDoctors() {
		// Mock Data
		List<Doctor> doctorList = new ArrayList<>();
		doctorList.add(new Doctor("John Doe", "username1", "password1", LocalDateTime.now(), Status.AVAILABLE));
		doctorList.add(new Doctor("Jane Smith", "username2", "password2", LocalDateTime.now(), Status.AVAILABLE));
		
        // Create a custom pageable object
        CustomPageable<Doctor> customPageable = new CustomPageable<>() {
            @Override
            public List<Doctor> getContent() {
                return doctorList; 
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
                return doctorList.size(); 
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

        DoctorPage doctorPageResults = new DoctorPage(doctorList, 0, 10, doctorList.size(), 1);

        when(doctorRepository.findAllDoctors(any(CustomPageable.class))).thenReturn(doctorPageResults);

        DoctorPage doctorPage = doctorRepository.findAllDoctors(customPageable);

        // Assertions
        assertNotNull(doctorPage);
        assertEquals(doctorList.size(), doctorPage.getContent().size());
        assertEquals(doctorList.size(), doctorPage.getTotalElements());
        assertEquals(1, doctorPage.getTotalPages());
	}
	
	@Test
	@DisplayName("Test findDoctorsByUsernameContaining method")
	public void testFindDoctorsByUsernameContaining() {
        // Mock data
        List<Doctor> doctorList = new ArrayList<>();
        doctorList.add(new Doctor("John Doe", "username1", "password1", LocalDateTime.now(), Status.AVAILABLE));
        doctorList.add(new Doctor("Jane Smith", "username2", "password2", LocalDateTime.now(), Status.AVAILABLE));
        doctorList.add(new Doctor("Peter Lim", "User1", "Password1", LocalDateTime.now(), Status.AVAILABLE));
        
        List<Doctor> doctorResults = doctorList.stream()
        		.filter(doctor -> doctor.getUsername().contains("username"))
        		.collect(Collectors.toList());
        
        // Create a DoctorPage object with mock data
        Page<Doctor> doctorPageResults = new PageImpl<>(doctorResults);
        
        // Mock the behavior of the doctorRepository for findDoctorsByUsernameContaining method
        when(doctorRepository.findDoctorsByUsernameContaining(any(String.class), any(Pageable.class))).thenReturn(doctorPageResults);
        
        // Call the method under test
        Page<Doctor> doctorPage = doctorRepository.findDoctorsByUsernameContaining("username", PageRequest.of(0, 10));
        
        // Assertions
        assertNotNull(doctorPage);
        assertEquals(doctorList.size() - 1, doctorPage.getContent().size());
        assertEquals(doctorList.size() - 1, doctorPage.getTotalElements());
        assertEquals(1, doctorPage.getTotalPages());
	}
	
	@Test
	@DisplayName("Test findDoctorsByNameContaining method")
	public void testFindDoctorsByNameContaining() {
			// Mock data
	        List<Doctor> doctorList = new ArrayList<>();
	        doctorList.add(new Doctor("John Doe", "username1", "password1", LocalDateTime.now(), Status.AVAILABLE));
	        doctorList.add(new Doctor("Jane Smith", "username2", "password2", LocalDateTime.now(), Status.AVAILABLE));
	        
	        // Filter the doctorList to get only the doctor with name "John Doe"
	        List<Doctor> doctorResults = doctorList.stream()
	        		.filter(doctor -> doctor.getName().equalsIgnoreCase("John Doe"))
	        		.collect(Collectors.toList());
        
        // Create a DoctorPage object with mock data
        Page<Doctor> doctorPageResults = new PageImpl<>(doctorResults);
        
        // Mock the behavior of the doctorRepository for findDoctorsByNameContaining method
        when(doctorRepository.findDoctorsByNameContaining(any(String.class), any(Pageable.class))).thenReturn(doctorPageResults);
        
        // Call the method under test
        Page<Doctor> doctorPage = doctorRepository.findDoctorsByNameContaining("John", PageRequest.of(0, 10));
        
        // Assertions
        assertNotNull(doctorPage);
        assertEquals(1, doctorPage.getContent().size());
        assertEquals("John Doe", doctorPage.getContent().get(0).getName());
        assertEquals(1, doctorPage.getTotalElements());
        assertEquals(1, doctorPage.getTotalPages());		
	}
	
	@Test
	@DisplayName("Test findDoctorsByStatus method")
	public void testFindDoctorsByStatus() {
	    // Mock data
	    List<Doctor> doctorList = new ArrayList<>();
	    doctorList.add(new Doctor("John Doe", "username1", "password1", LocalDateTime.now(), Status.AVAILABLE));
	    doctorList.add(new Doctor("Jane Smith", "username2", "password2", LocalDateTime.now(), Status.AVAILABLE));
	    doctorList.add(new Doctor("Peter Lim", "username2", "password2", LocalDateTime.now(), Status.BUSY));
	    
	    // Filter the doctorList to get only the doctor with status available
	    List<Doctor> doctorResults = doctorList.stream()
	            .filter(doctor -> doctor.getStatus().getCustomName().equals(Status.AVAILABLE.getCustomName()))
	            .collect(Collectors.toList());
	    
	    // Create a DoctorPage object with mock data
	    Page<Doctor> doctorPageResults = new PageImpl<>(doctorResults);
	    
	    // Mock the behavior of the doctorRepository for findDoctorsByStatus method
	    when(doctorRepository.findDoctorsByStatus(any(String.class), any(Pageable.class))).thenReturn(doctorPageResults);
	    
	    // Call the method under test
	    Page<Doctor> doctorPage = doctorRepository.findDoctorsByStatus(Status.AVAILABLE.getCustomName(), PageRequest.of(0, 10));
	    
	    // Assertions
	    assertNotNull(doctorPage);
	    assertEquals(2, doctorPage.getContent().size());
	    assertEquals("John Doe", doctorPage.getContent().get(0).getName());
	    assertEquals(2, doctorPage.getTotalElements());
	    assertEquals(1, doctorPage.getTotalPages());  
	}
	
    @SuppressWarnings("unchecked")
	@Test
    @DisplayName("Test findDoctorsByName method")
    public void testFindDoctorsByName() {
        // Mock data
        List<Doctor> doctorList = new ArrayList<>();
        doctorList.add(new Doctor("John Doe", "username1", "password1", LocalDateTime.now(), Status.AVAILABLE));
        doctorList.add(new Doctor("Jane Smith", "username2", "password2", LocalDateTime.now(), Status.AVAILABLE));

        // Filter the doctorList to get only the doctor with name "John Doe"
        List<Doctor> doctorResults = doctorList.stream()
                .filter(doctor -> doctor.getName().equalsIgnoreCase("John Doe"))
                .collect(Collectors.toList());
        
        // Create a custom pageable object
        CustomPageable<Doctor> customPageable = new CustomPageable<>() {
            @Override
            public List<Doctor> getContent() {
                return doctorList; 
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
                return doctorList.size(); 
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

        // Create a DoctorPage object with mock data
        DoctorPage doctorPageResults = new DoctorPage(doctorResults, 0, 10, doctorResults.size(), 1);

        // Mock the behavior of the doctorRepository for findDoctorsByNameContaining method
        when(doctorRepository.findDoctorsByName(any(String.class), any(CustomPageable.class))).thenReturn(doctorPageResults);

        // Call the method under test
        DoctorPage doctorPage = doctorRepository.findDoctorsByName("John", customPageable);

        // Assertions
        assertNotNull(doctorPage);
        assertEquals(1, doctorPage.getContent().size());
        assertEquals("John Doe", doctorPage.getContent().get(0).getName());
        assertEquals(1, doctorPage.getTotalElements());
        assertEquals(1, doctorPage.getTotalPages());
    }
    
    @SuppressWarnings("unchecked")
	@Test
    @DisplayName("Test findDoctorsByUsername method")
    public void testFindDoctorsByUsername() {
        // Mock data
        List<Doctor> doctorList = new ArrayList<>();
        doctorList.add(new Doctor("John Doe", "username1", "password1", LocalDateTime.now(), Status.AVAILABLE));
        doctorList.add(new Doctor("Jane Smith", "username2", "password2", LocalDateTime.now(), Status.AVAILABLE));
        doctorList.add(new Doctor("Peter Lim", "User1", "Password1", LocalDateTime.now(), Status.AVAILABLE));
        
        List<Doctor> doctorResults = doctorList.stream()
        		.filter(doctor -> doctor.getUsername().contains("username"))
        		.collect(Collectors.toList());
        
        // Create a custom pageable object
        CustomPageable<Doctor> customPageable = new CustomPageable<>() {
            @Override
            public List<Doctor> getContent() {
                return doctorList; 
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
                return doctorList.size(); 
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

        // Create a DoctorPage object with mock data
        DoctorPage doctorPageResults = new DoctorPage(doctorResults, 0, 10, doctorResults.size(), 1);

        // Mock the behavior of the doctorRepository for findDoctorsByUsername method
        when(doctorRepository.findDoctorsByUsername(any(String.class), any(CustomPageable.class))).thenReturn(doctorPageResults);

        // Call the method under test
        DoctorPage doctorPage = doctorRepository.findDoctorsByUsername("username", customPageable);

        // Assertions
        assertNotNull(doctorPage);
        assertEquals(doctorList.size() - 1, doctorPage.getContent().size());
        assertEquals(doctorList.size() - 1, doctorPage.getTotalElements());
        assertEquals(1, doctorPage.getTotalPages());
    }
    
    @SuppressWarnings("unchecked")
	@Test
    @DisplayName("Test findDoctorsByStatus method - DoctorPage")
    public void testFindDoctorsByStatusDoctorPage() {
	    // Mock data
	    List<Doctor> doctorList = new ArrayList<>();
	    doctorList.add(new Doctor("John Doe", "username1", "password1", LocalDateTime.now(), Status.AVAILABLE));
	    doctorList.add(new Doctor("Jane Smith", "username2", "password2", LocalDateTime.now(), Status.AVAILABLE));
	    doctorList.add(new Doctor("Peter Lim", "username2", "password2", LocalDateTime.now(), Status.BUSY));
	    
	    // Filter the doctorList to get only the doctor with status available
	    List<Doctor> doctorResults = doctorList.stream()
	            .filter(doctor -> doctor.getStatus().getCustomName().equals(Status.AVAILABLE.getCustomName()))
	            .collect(Collectors.toList());
	    
        // Create a custom pageable object
        CustomPageable<Doctor> customPageable = new CustomPageable<>() {
            @Override
            public List<Doctor> getContent() {
                return doctorList; 
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
                return doctorList.size(); 
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

        // Create a DoctorPage object with mock data
        DoctorPage doctorPageResults = new DoctorPage(doctorResults, 0, 10, doctorResults.size(), 1);

        // Mock the behavior of the doctorRepository for findDoctorsByStatus method
        when(doctorRepository.findDoctorsByStatus(any(String.class), any(CustomPageable.class))).thenReturn(doctorPageResults);

        // Call the method under test
        DoctorPage doctorPage = doctorRepository.findDoctorsByStatus(Status.AVAILABLE.getCustomName(), customPageable);

        // Assertions
        assertNotNull(doctorPage);
        assertEquals(doctorList.size() - 1, doctorPage.getContent().size());
        assertEquals(doctorList.size() - 1, doctorPage.getTotalElements());
        assertEquals(1, doctorPage.getTotalPages());
    }
}
