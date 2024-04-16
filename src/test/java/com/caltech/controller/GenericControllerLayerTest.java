package com.caltech.controller;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({AdminControllerTest.class, DefaultControllerTest.class, DoctorControllerTest.class, 
	GlobalExceptionHandlerTest.class, SuperAdminControllerTest.class,
	UserControllerTest.class})
public class GenericControllerLayerTest {

}
