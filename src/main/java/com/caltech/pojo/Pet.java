package com.caltech.pojo;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;
import com.caltech.constants.Species;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "pets")
public class Pet {

	@Id
	private ObjectId petId;
	
	@NotNull
	private String petName;
	
	@NotNull
	private Species petType;
	
	@NotNull
	private double petAge;
	
	@NotNull
	private ObjectId userId;

	public Pet(@NotNull String petName, @NotNull Species petType, @NotNull double petAge, ObjectId userId) {
		super();
		this.petName = petName;
		this.petType = petType;
		this.petAge = petAge;
		this.userId = userId;
	}
}
