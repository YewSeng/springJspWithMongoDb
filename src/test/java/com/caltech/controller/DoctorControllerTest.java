package com.caltech.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyString;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;
import com.caltech.config.JwtGeneratorValidator;
import com.caltech.constants.Status;
import com.caltech.exception.DoctorNotFoundException;
import com.caltech.exception.UserNotFoundException;
import com.caltech.exception.UsernameAlreadyExistException;
import com.caltech.pojo.Doctor;
import com.caltech.service.DoctorService;
import com.caltech.utils.CustomPageable;
import com.caltech.utils.DoctorPage;

@ExtendWith(MockitoExtension.class)
public class DoctorControllerTest {

    @Mock
	private DoctorService doctorService;
	
    @Mock
    private JwtGeneratorValidator jwtValidator;
    
    @InjectMocks
	private DoctorController doctorController;
    
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    

}
