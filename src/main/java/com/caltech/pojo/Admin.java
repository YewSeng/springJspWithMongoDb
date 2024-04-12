package com.caltech.pojo;

import java.time.LocalDateTime;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.caltech.constants.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@Document(collection = "admins")
public class Admin extends Person {
	
	@Id
	private ObjectId adminId;	
	
    public Admin(String name, String username, String password, LocalDateTime registrationDate) {
    	super(name, username, password, registrationDate, Role.ADMIN);
    	this.adminId = new ObjectId();
    }
	
}
