package com.caltech.repository;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({AdminRepositoryTest.class, BookingRepositoryTest.class, 
	DoctorRepositoryTest.class, PetRepositoryTest.class,
	UniqueUsernameRepositoryTest.class, UserRepositoryTest.class})
public class GenericRepositoryLayerTest {

}
