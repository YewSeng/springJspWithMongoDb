package com.caltech.pojo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
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
@Document(collection = "users")
public class User extends Person {

    @Id
	private ObjectId userId;
    
    private List<ObjectId> petIds = new ArrayList<>();
    
    public User(String name, String username, String password, LocalDateTime registrationDate) {
    	super(name, username, password, registrationDate, Role.USER);
    	this.userId = new ObjectId();
    }
}
