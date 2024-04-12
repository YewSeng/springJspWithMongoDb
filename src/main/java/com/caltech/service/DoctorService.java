package com.caltech.service;

import java.util.ArrayList;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.caltech.constants.Status;
import com.caltech.exception.DoctorNotFoundException;
import com.caltech.exception.UsernameAlreadyExistException;
import com.caltech.pojo.Doctor;
import com.caltech.repository.DoctorRepository;
import com.caltech.utils.CustomPageable;
import com.caltech.utils.DoctorPage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DoctorService {

	private final DoctorRepository doctorRepository;
	private final BcryptService bcryptService;
	
    @Autowired
	public DoctorService(DoctorRepository doctorRepository, BcryptService bcryptService) {
		this.doctorRepository = doctorRepository;
		this.bcryptService = bcryptService;
	}
	
	public Optional<Doctor> findDoctorById(ObjectId doctorId) {
		return doctorRepository.findById(doctorId);
	}
	
	public Optional<Doctor> findDoctorByUsername(String username) {
		return doctorRepository.findByUsername(username);
	}
	
	public DoctorPage findAllDoctors(CustomPageable<Doctor> pageable) {
		DoctorPage customPage = doctorRepository.findAllDoctors(pageable);
		if (customPage == null) {
            log.warn("CustomPage is null");
            return new DoctorPage(new ArrayList<>(), 0, 0, 0, 0);
		}
        log.info("Fetching all doctors with pageable: {}", customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} doctors", customPage.getContent().size());
        customPage.getContent().forEach(doctor -> log.info("Doctor: {}", doctor.getUsername()));
        return customPage;
	}
	
	public DoctorPage findDoctorsByUsername(String username, CustomPageable<Doctor> pageable) {
		DoctorPage customPage = doctorRepository.findDoctorsByUsername(username, pageable);
		if (customPage == null) {
            log.warn("CustomPage is null");
            return new DoctorPage(new ArrayList<>(), 0, 0, 0, 0);
		}
        log.info("Fetching Doctor with username: {} with pageable: {}", username, customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} doctors", customPage.getContent().size());
        return customPage;
	}
	
	public DoctorPage findDoctorsByName(String name, CustomPageable<Doctor> pageable) {
		DoctorPage customPage = doctorRepository.findDoctorsByName(name, pageable);
		if (customPage == null) {
            log.warn("CustomPage is null");
            return new DoctorPage(new ArrayList<>(), 0, 0, 0, 0);
		}
        log.info("Fetching Doctor with name: {} with pageable: {}", name, customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} doctors", customPage.getContent().size());
        return customPage;
	}
	
	public DoctorPage findDoctorsByStatus(String status, CustomPageable<Doctor> pageable) {
		DoctorPage customPage = doctorRepository.findDoctorsByStatus(status, pageable);
		if (customPage == null) {
            log.warn("CustomPage is null");
            return new DoctorPage(new ArrayList<>(), 0, 0, 0, 0);
		}
        log.info("Fetching Doctor with status: {} with pageable: {}", status, customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} doctors", customPage.getContent().size());
        return customPage;
	}
	
	public Boolean verifyExistingUsername(String username) {
		return doctorRepository.findByUsername(username).isPresent();
	}
	
	public Boolean authenticateDoctor(String username, String password) {
    	log.info("Autenticating Doctor with Username: {}, Password: {}", username, password);
        boolean correctPassword = false;       
        Optional<Doctor> doctorOptional = doctorRepository.findByUsername(username);
        if (doctorOptional.isPresent()) {
        	Doctor doctor = doctorOptional.get();
        	String hashPasswordFromDb = doctor.getPassword();
        	correctPassword = bcryptService.verifyPassword(password, hashPasswordFromDb);
        }
        return correctPassword;
	}
	
	public Doctor createDoctor(Doctor doctor) {
		log.info("Creating a new Doctor with username: {}", doctor.getUsername());
		doctor.setPassword(bcryptService.hashPassword(doctor.getPassword()));
		return doctorRepository.save(doctor);
	}
	
	public Doctor updateDoctor(ObjectId doctorId, Doctor updatedDoctor) throws DoctorNotFoundException, UsernameAlreadyExistException {
		Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorId);
		if (optionalDoctor.isPresent()) {
			Doctor existingDoctor = optionalDoctor.get();
			existingDoctor.setName(updatedDoctor.getName());
			existingDoctor.setPassword(bcryptService.hashPassword(updatedDoctor.getPassword()));
			existingDoctor.setStatus(updatedDoctor.getStatus());
			if(!existingDoctor.getUsername().equals(updatedDoctor.getUsername())) {
				// Check if the new username already exists in the database
                if (verifyExistingUsername(updatedDoctor.getUsername())) {
                    throw new UsernameAlreadyExistException("Username already exists");
                }
                existingDoctor.setUsername(updatedDoctor.getUsername());
			}
			return doctorRepository.save(existingDoctor);
		} else {
			throw new DoctorNotFoundException("Doctor not found with adminId: " + doctorId);
		}
	}
	
	public void deleteDoctor(ObjectId doctorId) throws DoctorNotFoundException  {
		Optional<Doctor> optionalDoctor = doctorRepository.findById(doctorId);
        if (optionalDoctor.isPresent()) {
            log.info("Deleting doctor with adminId: {}", doctorId);
            doctorRepository.deleteById(doctorId);
            log.info("Doctor with doctorId: {} deleted successfully", doctorId);
        } else {        	
            log.error("Doctor not found with doctorId: {}", doctorId);
            throw new DoctorNotFoundException("Doctor not found with doctorId: " + doctorId);
        }
	}
}
