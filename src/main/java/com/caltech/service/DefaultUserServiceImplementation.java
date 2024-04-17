package com.caltech.service;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.caltech.pojo.Admin;
import com.caltech.pojo.Doctor;
import com.caltech.pojo.User;
import com.caltech.repository.AdminRepository;
import com.caltech.repository.DoctorRepository;
import com.caltech.repository.UserRepository;
import lombok.Data;

@Data
@Service
public class DefaultUserServiceImplementation implements DefaultUserService {

    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private final AdminRepository adminRepository;
    
    @Value("${superadmin.secretKey}")
    private String superAdminSecretKey;
    
    @Autowired
    public DefaultUserServiceImplementation(UserRepository userRepository, 
    		DoctorRepository doctorRepository, AdminRepository adminRepository) {
    	this.userRepository = userRepository;
    	this.doctorRepository = doctorRepository;
    	this.adminRepository = adminRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Check if the user is a SuperAdmin
        if (superAdminSecretKey.equals(username)) {
            List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_SUPERADMIN");
            return new org.springframework.security.core.userdetails.User(
            		"Super Admin",
                    "",
                    authorities
            );
        }

        if (username.startsWith("U")) {
        	// Check if the user is in the User repository
            Optional<User> optionalUser = userRepository.findByUsername(username);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_USER");
                return new org.springframework.security.core.userdetails.User(
                        user.getUsername(),
                        user.getPassword(),
                        authorities
                );
            }
        } else if (username.startsWith("D")) {
            // Check if the user is in the Doctor repository
            Optional<Doctor> optionalDoctor = doctorRepository.findByUsername(username);
            if (optionalDoctor.isPresent()) {
                Doctor doctor = optionalDoctor.get();
                List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_DOCTOR");
                return new org.springframework.security.core.userdetails.User(
                        doctor.getUsername(),
                        doctor.getPassword(),
                        authorities
                );
            }
        } else if (username.startsWith("A")) {
            // Check if the user is in the Admin repository
            Optional<Admin> optionalAdmin = adminRepository.findByUsername(username);
            if (optionalAdmin.isPresent()) {
                Admin admin = optionalAdmin.get();
                List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("ROLE_ADMIN");
                return new org.springframework.security.core.userdetails.User(
                        admin.getUsername(),
                        admin.getPassword(),
                        authorities
                );
            }
        }
        throw new UsernameNotFoundException("User/Admin/Doctor not found with username: " + username);
    }
}
