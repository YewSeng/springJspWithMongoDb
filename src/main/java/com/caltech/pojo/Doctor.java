package com.caltech.pojo;

import java.time.LocalDateTime;
import javax.validation.constraints.NotNull;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.caltech.constants.Role;
import com.caltech.constants.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document(collection = "doctors")
public class Doctor extends Person {

    @Id
	private ObjectId doctorId;
    
    @NotNull
    private Status status;
    
    public Doctor(String name, String username, String password, LocalDateTime registrationDate, Status status) {
    	super(name, username, password, registrationDate, Role.DOCTOR);
    	this.doctorId = new ObjectId();
    	this.status = status;
    }    
}
