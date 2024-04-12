package com.caltech.service;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({AdminServiceTest.class, BcryptServiceTest.class, BookingServiceTest.class,
	DefaultUserServiceImplementationTest.class, DoctorServiceTest.class, 
	IpAddressLockoutServiceTest.class, PetServiceTest.class, SuperAdminServiceTest.class,
	UserServiceTest.class})
public class GenericServiceLayerTest {

}
