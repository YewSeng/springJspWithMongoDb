package com.caltech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
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
import com.caltech.constants.Status;
import com.caltech.exception.DoctorNotFoundException;
import com.caltech.exception.UsernameAlreadyExistException;
import com.caltech.pojo.Doctor;
import com.caltech.repository.DoctorRepository;
import com.caltech.utils.CustomPageable;
import com.caltech.utils.DoctorPage;

@ExtendWith(MockitoExtension.class)
public class DoctorServiceTest {

	@Mock
	private DoctorRepository doctorRepository;
	
    @Mock
    private BcryptService bcryptService;
    
    @InjectMocks
    private DoctorService doctorService;
    
    @Test
    @DisplayName("Test findDoctorById method")
    public void testFindDoctorById() {
    	ObjectId doctorId = new ObjectId();
    	Doctor doctor = new Doctor("John", "john_doe", "password", LocalDateTime.now(), Status.AVAILABLE);
    	when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
    	Optional<Doctor> foundOptionalDoctor = doctorService.findDoctorById(doctorId);
    	assertTrue(foundOptionalDoctor.isPresent());
    	assertEquals(doctor, foundOptionalDoctor.get());
    }
    
    @Test
    @DisplayName("Test findDoctorByUsername method")
    public void testFindDoctorByUsername() {
    	String username = "john_doe";
    	Doctor doctor = new Doctor("John", "john_doe", "password", LocalDateTime.now(), Status.AVAILABLE);
    	when(doctorRepository.findByUsername(username)).thenReturn(Optional.of(doctor));
    	Optional<Doctor> foundOptionalDoctor = doctorService.findDoctorByUsername(username);
    	assertTrue(foundOptionalDoctor.isPresent());
    	assertEquals(doctor, foundOptionalDoctor.get());
    }
    
    @Test
    @DisplayName("Test findAllDoctors method")
    public void testFindAllDoctors() {
    	List<Doctor> doctorList = new ArrayList<>();
    	doctorList.add(new Doctor("John", "john_doe", "password", LocalDateTime.now(), Status.AVAILABLE));
    	doctorList.add(new Doctor("Alice", "alice_smith", "password123", LocalDateTime.now(), Status.AVAILABLE));
    	DoctorPage doctorPage = new DoctorPage(doctorList, 0, 10, doctorList.size(), 1);
    	CustomPageable<Doctor> customPageable = new DoctorPage(0,10);
    	when(doctorRepository.findAllDoctors(customPageable)).thenReturn(doctorPage);
    	DoctorPage foundDoctorPage = doctorService.findAllDoctors(customPageable);
		assertEquals(doctorPage, foundDoctorPage);
		assertEquals(doctorList.size(), foundDoctorPage.getContent().size());
    }
    
    @Test
    @DisplayName("Test findDoctorsByUsername method")
    public void testFindDoctorsByUsername() {
    	String username = "john_doe";
    	List<Doctor> doctorList = new ArrayList<>();
    	doctorList.add(new Doctor("John", "john_doe", "password", LocalDateTime.now(), Status.AVAILABLE));
    	doctorList.add(new Doctor("Alice", "alice_smith", "password123", LocalDateTime.now(), Status.AVAILABLE));
    	doctorList.add(new Doctor("Johnny", "johnny", "password", LocalDateTime.now(), Status.AVAILABLE));
    	List<Doctor> doctorResults = doctorList.stream()
    			.filter(doctor -> doctor.getUsername().contains(username))
    			.collect(Collectors.toList());
    	DoctorPage doctorPage = new DoctorPage(doctorResults, 0, 10, doctorResults.size(), 1);
    	CustomPageable<Doctor> customPageable = new DoctorPage(0,10);
    	when(doctorRepository.findDoctorsByUsername(username, customPageable)).thenReturn(doctorPage);
    	DoctorPage foundDoctorPage = doctorService.findDoctorsByUsername(username, customPageable);
		assertEquals(doctorPage, foundDoctorPage);
		assertEquals(1, foundDoctorPage.getContent().size());
    }
    
    @Test
    @DisplayName("Test findDoctorsByName method")
    public void testFindDoctorsByName() {
    	String name = "John";
    	List<Doctor> doctorList = new ArrayList<>();
    	doctorList.add(new Doctor("John", "john_doe", "password", LocalDateTime.now(), Status.AVAILABLE));
    	doctorList.add(new Doctor("Alice", "alice_smith", "password123", LocalDateTime.now(), Status.AVAILABLE));
    	doctorList.add(new Doctor("Johnny", "johnny", "password", LocalDateTime.now(), Status.AVAILABLE));
    	List<Doctor> doctorResults = doctorList.stream()
    			.filter(doctor -> doctor.getName().contains(name))
    			.collect(Collectors.toList());
    	DoctorPage doctorPage = new DoctorPage(doctorResults, 0, 10, doctorResults.size(), 1);
    	CustomPageable<Doctor> customPageable = new DoctorPage(0,10);
    	when(doctorRepository.findDoctorsByName(name, customPageable)).thenReturn(doctorPage);
    	DoctorPage foundDoctorPage = doctorService.findDoctorsByName(name, customPageable);
		assertEquals(doctorPage, foundDoctorPage);
		assertEquals(2, foundDoctorPage.getContent().size());
    }
    
    @Test
    @DisplayName("Test findDoctorsByStatus method")
    public void testFindDoctorsByStatus() {
	    List<Doctor> doctorList = new ArrayList<>();
	    doctorList.add(new Doctor("John Doe", "username1", "password1", LocalDateTime.now(), Status.AVAILABLE));
	    doctorList.add(new Doctor("Jane Smith", "username2", "password2", LocalDateTime.now(), Status.AVAILABLE));
	    doctorList.add(new Doctor("Peter Lim", "username2", "password2", LocalDateTime.now(), Status.BUSY));
	    List<Doctor> doctorResults = doctorList.stream()
	            .filter(doctor -> doctor.getStatus().getCustomName().equals(Status.AVAILABLE.getCustomName()))
	            .collect(Collectors.toList());
    	DoctorPage doctorPage = new DoctorPage(doctorResults, 0, 10, doctorResults.size(), 1);
    	CustomPageable<Doctor> customPageable = new DoctorPage(0,10);
    	when(doctorRepository.findDoctorsByStatus(Status.AVAILABLE.getCustomName(), customPageable)).thenReturn(doctorPage);
    	DoctorPage foundDoctorPage = doctorService.findDoctorsByStatus(Status.AVAILABLE.getCustomName(), customPageable);
		assertEquals(doctorPage, foundDoctorPage);
		assertEquals(2, foundDoctorPage.getContent().size());
    }
    
    @Test
    @DisplayName("Test verifyExistingUsername method")
    public void testVerifyExistingUsername() {
    	String username = "john_doe";
		Doctor doctor1 = new Doctor("John", "john_doe", "password", LocalDateTime.now(), Status.AVAILABLE);
		Doctor doctor2 = new Doctor("Alice", "alice_smith", "password123", LocalDateTime.now(), Status.AVAILABLE);
		List<Doctor> doctorList = new ArrayList<>();
		doctorList.add(doctor1);
		doctorList.add(doctor2);
		when(doctorRepository.findByUsername(username)).thenReturn(Optional.of(doctor1));
		boolean isUsernameTaken = doctorService.verifyExistingUsername(username);
		assertTrue(isUsernameTaken);
    }
    
    @Test
    @DisplayName("Test authenticateDoctor method - Correct Password")
    public void testAuthenticateDoctor() {
        String username = "john_doe";
        String password = "password";
        Doctor doctor = new Doctor("John", "john_doe", bcryptService.hashPassword(password), LocalDateTime.now(), Status.AVAILABLE);
        when(doctorRepository.findByUsername(username)).thenReturn(Optional.of(doctor));
        when(bcryptService.verifyPassword(password, doctor.getPassword())).thenReturn(true);
        boolean isDoctor = doctorService.authenticateDoctor(username, password);
        assertTrue(isDoctor);
    }
    
    @Test
    @DisplayName("Test authenticateDoctor method - Incorrect Password")
    public void testAuthenticateDoctorWithIncorrectPassword() {
        String username = "john_doe";
        String password = "wrongPassword";
        Doctor doctor = new Doctor("John", "john_doe", bcryptService.hashPassword(password), LocalDateTime.now(), Status.AVAILABLE);
        when(doctorRepository.findByUsername(username)).thenReturn(Optional.of(doctor));
        when(bcryptService.verifyPassword(password, doctor.getPassword())).thenReturn(false);
        boolean isDoctor = doctorService.authenticateDoctor(username, password);
        assertFalse(isDoctor);
    }
    
    @Test
    @DisplayName("Test createDoctor method")
    public void testCreateDoctor() {
        Doctor doctor = new Doctor("John", "john_doe", "password", LocalDateTime.now(), Status.AVAILABLE);
        Doctor hashedDoctor = new Doctor(doctor.getName(), doctor.getUsername(), bcryptService.hashPassword(doctor.getPassword()), doctor.getRegistrationDate(), doctor.getStatus());
        when(doctorRepository.save(doctor)).thenReturn(hashedDoctor);
    	Doctor createdDoctor = doctorService.createDoctor(doctor);
    	assertEquals(hashedDoctor.getPassword(), createdDoctor.getPassword());
    }
    
    @Test
    @DisplayName("Test updateDoctor method")
    public void testUpdateDoctor() throws DoctorNotFoundException, UsernameAlreadyExistException {
    	ObjectId doctorId = new ObjectId();
    	Doctor existingDoctor = new Doctor("John", "john_doe", "password", LocalDateTime.now(), Status.AVAILABLE);
    	existingDoctor.setDoctorId(doctorId);
    	Doctor updatedDoctor = new Doctor("Peter Lim", "Admin1", "password", LocalDateTime.now(), Status.AVAILABLE);
    	updatedDoctor.setPassword(bcryptService.hashPassword(updatedDoctor.getPassword()));
    	when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(existingDoctor));
    	when(doctorRepository.findByUsername(updatedDoctor.getUsername())).thenReturn(Optional.empty());
    	when(doctorRepository.save(existingDoctor)).thenReturn(updatedDoctor);
    	Doctor returnedDoctor = doctorService.updateDoctor(doctorId, updatedDoctor);
    	when(bcryptService.verifyPassword(updatedDoctor.getPassword(), returnedDoctor.getPassword())).thenReturn(true);
    	assertEquals(updatedDoctor, returnedDoctor);
    	assertEquals(updatedDoctor.getPassword(), returnedDoctor.getPassword());
    	assertTrue(bcryptService.verifyPassword(updatedDoctor.getPassword(), returnedDoctor.getPassword()));
    }
    
    @Test
    @DisplayName("Test updateDoctor method - Admin Not Found")
    public void testUpdateDoctorNotFound() throws DoctorNotFoundException, UsernameAlreadyExistException {
    	ObjectId doctorId = new ObjectId();
    	Doctor updatedDoctor = new Doctor("Peter Lim", "Admin1", "password", LocalDateTime.now(), Status.AVAILABLE);
    	when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());
    	DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class, () -> {
    		doctorService.updateDoctor(doctorId, updatedDoctor);
    	});
    	assertNotNull(exception);	    	
    }
    
    @Test
    @DisplayName("Test updateDoctor method - Username Already Exists")
    public void testUpdateDoctorUsernameAlreadyExists() throws DoctorNotFoundException, UsernameAlreadyExistException {
    	ObjectId doctorId = new ObjectId();
    	Doctor existingDoctor = new Doctor("John", "john_doe", "password", LocalDateTime.now(), Status.AVAILABLE);
    	existingDoctor.setDoctorId(doctorId);
    	Doctor updatedDoctor = new Doctor("Peter Lim", "Admin1", "password", LocalDateTime.now(), Status.AVAILABLE);
    	when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(existingDoctor));
    	when(doctorRepository.findByUsername(updatedDoctor.getUsername())).thenReturn(Optional.of(new Doctor()));
        UsernameAlreadyExistException exception = assertThrows(UsernameAlreadyExistException.class, () -> {
        	doctorService.updateDoctor(doctorId, updatedDoctor);
        });   
        assertNotNull(exception); 			
    }
    
    @Test
    @DisplayName("Test deleteDoctor method")
    public void testDeleteDoctor() throws DoctorNotFoundException {
    	ObjectId doctorId = new ObjectId();
    	Doctor doctor = new Doctor("John", "john_doe", "password", LocalDateTime.now(), Status.AVAILABLE);
    	doctor.setDoctorId(doctorId);
    	when(doctorRepository.findById(doctorId)).thenReturn(Optional.of(doctor));
    	doctorService.deleteDoctor(doctorId);
    	verify(doctorRepository, times(1)).deleteById(doctorId);   	
    }
    
    @Test
    @DisplayName("Test deleteDoctor method - Doctor Id Not Found")
    public void testDeleteDoctorNotFound() throws DoctorNotFoundException {
    	ObjectId doctorId = new ObjectId();
    	when(doctorRepository.findById(doctorId)).thenReturn(Optional.empty());
        DoctorNotFoundException exception = assertThrows(DoctorNotFoundException.class, () -> {
            doctorService.deleteDoctor(doctorId);
        });
        assertNotNull(exception);
    	verify(doctorRepository, never()).deleteById(doctorId);   	
    }
}
