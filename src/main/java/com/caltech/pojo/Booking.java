package com.caltech.pojo;

import java.time.LocalDateTime;
import org.bson.types.ObjectId;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "bookings")
public class Booking {

	@Id
	private ObjectId bookingId;
	
	@NotNull
	private ObjectId userId;
	
	@NotNull
	private ObjectId doctorId;
	
	private LocalDateTime bookingTime  = LocalDateTime.now();

	public Booking(ObjectId userId, ObjectId doctorId, LocalDateTime bookingTime) {
		super();
		this.userId = userId;
		this.doctorId = doctorId;
		this.bookingTime = bookingTime;
	}	
}
