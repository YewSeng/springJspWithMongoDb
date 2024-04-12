package com.caltech.pojo;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import com.caltech.constants.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public abstract class Person {

    @NotNull
    @Size(max = 50)
    private String name;
    
    @NotNull
    @Size(min = 8, max = 20, message = "Username must be between 8 and 16 characters")
    private String username;

    @NotNull
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String password;
    
    private LocalDateTime registrationDate = LocalDateTime.now();
    
    private Role role;
    
}
