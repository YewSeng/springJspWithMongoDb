package com.caltech;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import com.caltech.controller.AdminControllerTest;
import com.caltech.controller.DefaultControllerTest;
import com.caltech.controller.DoctorControllerTest;
import com.caltech.controller.GlobalExceptionHandlerTest;
import com.caltech.controller.UserControllerTest;
import com.caltech.repository.AdminRepositoryTest;
import com.caltech.repository.BookingRepositoryTest;
import com.caltech.repository.DoctorRepositoryTest;
import com.caltech.repository.PetRepositoryTest;
import com.caltech.repository.UniqueUsernameRepositoryTest;
import com.caltech.repository.UserRepositoryTest;
import com.caltech.service.AdminServiceTest;
import com.caltech.service.BcryptServiceTest;
import com.caltech.service.BookingServiceTest;
import com.caltech.service.DefaultUserServiceImplementationTest;
import com.caltech.service.DoctorServiceTest;
import com.caltech.service.IpAddressLockoutServiceTest;
import com.caltech.service.PetServiceTest;
import com.caltech.service.SuperAdminServiceTest;
import com.caltech.service.UserServiceTest;

@Suite
@SelectClasses({AdminRepositoryTest.class, BookingRepositoryTest.class, 
	DoctorRepositoryTest.class, PetRepositoryTest.class,
	UniqueUsernameRepositoryTest.class, UserRepositoryTest.class,
	AdminServiceTest.class, BcryptServiceTest.class, BookingServiceTest.class, 
	DefaultUserServiceImplementationTest.class,DoctorServiceTest.class, 
	IpAddressLockoutServiceTest.class, PetServiceTest.class, SuperAdminServiceTest.class,
	UserServiceTest.class, AdminControllerTest.class, DefaultControllerTest.class, 
	DoctorControllerTest.class, GlobalExceptionHandlerTest.class, 
	UserControllerTest.class})
public class GenericTest {

}
