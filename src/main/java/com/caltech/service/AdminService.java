package com.caltech.service;

import java.util.ArrayList;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.caltech.exception.AdminNotFoundException;
import com.caltech.exception.UsernameAlreadyExistException;
import com.caltech.pojo.Admin;
import com.caltech.repository.AdminRepository;
import com.caltech.utils.AdminPage;
import com.caltech.utils.CustomPageable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AdminService {

	private final AdminRepository adminRepository;
	private final BcryptService bcryptService;
	
	@Autowired
	public AdminService(AdminRepository adminRepository, BcryptService bcryptService) {
		this.adminRepository = adminRepository;
		this.bcryptService = bcryptService;
	}
	
	public Optional<Admin> findAdminById(ObjectId adminId) {
		return adminRepository.findById(adminId);
	}
	
	public Optional<Admin> findAdminByUsername(String username) {
		return adminRepository.findByUsername(username);
	}
	
	public AdminPage findAllAdmins(CustomPageable<Admin> pageable) {
		AdminPage customPage = adminRepository.findAllAdmins(pageable);
        if (customPage == null) {
            log.warn("CustomPage is null");
            // Create and return an empty AdminPage
            return new AdminPage(new ArrayList<>(), 0, 0, 0, 0);
        }
        log.info("Fetching all admins with pageable: {}", customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} admins", customPage.getContent().size());
        customPage.getContent().forEach(admin -> log.info("Admin: {}", admin.getUsername()));
        return customPage;
	}
	
	public AdminPage findAdminsByUsername(String username, CustomPageable<Admin> pageable) {
        AdminPage customPage = adminRepository.findAdminsByUsername(username, pageable);
        if (customPage == null) {
            log.warn("CustomPage is null");
            // Create and return an empty AdminPage
            return new AdminPage(new ArrayList<>(), 0, 0, 0, 0);
        }
        log.info("Fetching Admin with username: {} with pageable: {}", username, customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} admins", customPage.getContent().size());
        return customPage;
	}
	
	public AdminPage findAdminsByName(String name, CustomPageable<Admin> pageable) {
        AdminPage customPage = adminRepository.findAdminsByName(name, pageable);
        if (customPage == null) {
            log.warn("CustomPage is null");
            // Create and return an empty AdminPage
            return new AdminPage(new ArrayList<>(), 0, 0, 0, 0);
        }
        log.info("Fetching admins with name: {} with pageable: {}", name, customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} admins", customPage.getContent().size());
        customPage.getContent().forEach(admin -> log.info("Admin: {}", admin.getUsername()));
        return customPage;
	}
	
    public Boolean verifyExistingUsername(String username) {
    	return adminRepository.findByUsername(username).isPresent();
    }
    
    public Boolean authenticateAdmin(String username, String password) {
    	log.info("Autenticating Admin with Username: {}, Password: {}", username, password);
        boolean correctPassword = false;       
        Optional<Admin> adminOptional = adminRepository.findByUsername(username);      
        if (adminOptional.isPresent()) {
            Admin admin = adminOptional.get();
            String hashPasswordFromDb = admin.getPassword();
            correctPassword = bcryptService.verifyPassword(password, hashPasswordFromDb);
        }
        
        return correctPassword;
    }
	
    public Admin createAdmin(Admin admin) {
    	log.info("Creating a new Admin with username: {}", admin.getUsername());
    	admin.setPassword(bcryptService.hashPassword(admin.getPassword()));
    	return adminRepository.save(admin);
    }
    
    public Admin updateAdmin(ObjectId adminId, Admin updatedAdmin) throws AdminNotFoundException, UsernameAlreadyExistException {
        Optional<Admin> optionalAdmin = adminRepository.findById(adminId);
        if (optionalAdmin.isPresent()) {
            Admin existingAdmin = optionalAdmin.get();
            existingAdmin.setName(updatedAdmin.getName());
            existingAdmin.setPassword(bcryptService.hashPassword(updatedAdmin.getPassword()));
            if (!existingAdmin.getUsername().equals(updatedAdmin.getUsername())) {
                // Check if the new username already exists in the database
                if (verifyExistingUsername(updatedAdmin.getUsername())) {
                    throw new UsernameAlreadyExistException("Username already exists");
                }
                existingAdmin.setUsername(updatedAdmin.getUsername());
            }
            return adminRepository.save(existingAdmin);
        } else {
            throw new AdminNotFoundException("Admin not found with adminId: " + adminId);
        }
    }
    
    public void deleteAdmin(ObjectId adminId) throws AdminNotFoundException {
        Optional<Admin> optionalAdmin = adminRepository.findById(adminId);
        if (optionalAdmin.isPresent()) {
            log.info("Deleting admin with adminId: {}", adminId);
            adminRepository.deleteById(adminId);
            log.info("Admin with adminId: {} deleted successfully", adminId);
        } else {
            log.error("Admin not found with adminId: {}", adminId);
            throw new AdminNotFoundException("Admin not found with adminId: " + adminId);
        }
    }
}
