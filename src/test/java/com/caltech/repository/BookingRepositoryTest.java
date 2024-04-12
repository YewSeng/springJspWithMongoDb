package com.caltech.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import com.caltech.pojo.Booking;
import com.caltech.utils.BookingPage;
import com.caltech.utils.CustomPageable;

@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class BookingRepositoryTest {

	@Mock
	private BookingRepository bookingRepository;
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Test findAllBookings method")
	public void testFindAllBookings() {
		// Mock the Data
		List<Booking> bookingList = new ArrayList<>();
		bookingList.add(new Booking(new ObjectId(), new ObjectId(), LocalDateTime.now()));
		bookingList.add(new Booking(new ObjectId(), new ObjectId(), LocalDateTime.now()));
		bookingList.add(new Booking(new ObjectId(), new ObjectId(), LocalDateTime.now()));
		
		// Create a custom pageable object
		CustomPageable<Booking> customPageable = new CustomPageable<>() {
			@Override
			public List<Booking> getContent() {
				return bookingList;
			}

			@Override
			public int getPageNumber() {
				return 0;
			}

			@Override
			public int getPageSize() {
				return 10;
			}

			@Override
			public long getTotalElements() {
				return bookingList.size();
			}

			@Override
			public int getTotalPages() {
				return 1;
			}

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public boolean hasPrevious() {
				return false;
			}
			
		};
		
		// Create a BookingPage with Mock data
		BookingPage bookingPageResults = new BookingPage(bookingList, 0, 10, bookingList.size(), 1);
		
		// Mock the behavior of bookingRepository
		when(bookingRepository.findAllBookings(any(CustomPageable.class))).thenReturn(bookingPageResults);
		
		// Call the method under test
		BookingPage bookingPage = bookingRepository.findAllBookings(customPageable);
		
		// Assertions
        assertNotNull(bookingPage);
        assertEquals(bookingList.size(), bookingPage.getContent().size());
        assertEquals(bookingList.size(), bookingPage.getTotalElements());
        assertEquals(1, bookingPage.getTotalPages());		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Test findBookingsByUserId method")
	public void testFindBookingsByUserId() {
		// Mock the Data
		ObjectId user1 = new ObjectId();
		ObjectId user2 = new ObjectId();
		List<Booking> bookingList = new ArrayList<>();
		bookingList.add(new Booking(user1, new ObjectId(), LocalDateTime.now()));
		bookingList.add(new Booking(user1, new ObjectId(), LocalDateTime.now()));
		bookingList.add(new Booking(user2, new ObjectId(), LocalDateTime.now()));
		
		List<Booking> bookingResults = bookingList.stream()
				.filter(booking -> booking.getUserId().equals(user1))
				.collect(Collectors.toList());
		
		// Create a custom pageable object
		CustomPageable<Booking> customPageable = new CustomPageable<>() {
			@Override
			public List<Booking> getContent() {
				return bookingResults;
			}

			@Override
			public int getPageNumber() {
				return 0;
			}

			@Override
			public int getPageSize() {
				return 10;
			}

			@Override
			public long getTotalElements() {
				return bookingResults.size();
			}

			@Override
			public int getTotalPages() {
				return 1;
			}

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public boolean hasPrevious() {
				return false;
			}
			
		};
		
		// Create a BookingPage with Mock data
		BookingPage bookingPageResults = new BookingPage(bookingResults, 0, 10, bookingResults.size(), 1);
		
		// Mock the behavior of bookingRepository
		when(bookingRepository.findBookingsByUserId(any(ObjectId.class), any(CustomPageable.class))).thenReturn(bookingPageResults);
		
		// Call the method under test
		BookingPage bookingPage = bookingRepository.findBookingsByUserId(user1, customPageable);
		
		// Assertions
        assertNotNull(bookingPage);
        assertEquals(bookingList.size() - 1, bookingPage.getContent().size());
        assertEquals(bookingList.size() - 1, bookingPage.getTotalElements());
        assertEquals(1, bookingPage.getTotalPages());		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Test findBookingsByDoctorId method")
	public void testFindBookingsByDoctorId() {
		// Mock the Data
		ObjectId doctor1 = new ObjectId();
		ObjectId doctor2 = new ObjectId();
		List<Booking> bookingList = new ArrayList<>();
		bookingList.add(new Booking(new ObjectId(), doctor1, LocalDateTime.now()));
		bookingList.add(new Booking(new ObjectId(), doctor2, LocalDateTime.now()));
		bookingList.add(new Booking(new ObjectId(), doctor1, LocalDateTime.now()));
		
		List<Booking> bookingResults = bookingList.stream()
				.filter(booking -> booking.getDoctorId().equals(doctor1))
				.collect(Collectors.toList());
		
		// Create a custom pageable object
		CustomPageable<Booking> customPageable = new CustomPageable<>() {
			@Override
			public List<Booking> getContent() {
				return bookingResults;
			}

			@Override
			public int getPageNumber() {
				return 0;
			}

			@Override
			public int getPageSize() {
				return 10;
			}

			@Override
			public long getTotalElements() {
				return bookingResults.size();
			}

			@Override
			public int getTotalPages() {
				return 1;
			}

			@Override
			public boolean hasNext() {
				return false;
			}

			@Override
			public boolean hasPrevious() {
				return false;
			}
			
		};
		
		// Create a BookingPage with Mock data
		BookingPage bookingPageResults = new BookingPage(bookingResults, 0, 10, bookingResults.size(), 1);
		
		// Mock the behavior of bookingRepository
		when(bookingRepository.findBookingsByDoctorId(any(ObjectId.class), any(CustomPageable.class))).thenReturn(bookingPageResults);
		
		// Call the method under test
		BookingPage bookingPage = bookingRepository.findBookingsByDoctorId(doctor1, customPageable);
		
		// Assertions
        assertNotNull(bookingPage);
        assertEquals(bookingList.size() - 1, bookingPage.getContent().size());
        assertEquals(bookingList.size() - 1, bookingPage.getTotalElements());
        assertEquals(1, bookingPage.getTotalPages());	
	}
	
	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Test findBookingsByBookingTimeBetween method")
	public void testFindBookingsByBookingTimeBetween() {
	    // Mock the Data
	    LocalDateTime outOfRange = LocalDateTime.parse("2022-03-18T09:30:00");
	    LocalDateTime withinRange = LocalDateTime.parse("2024-03-18T12:30:00");
	    LocalDateTime leftBoundaryRange = LocalDateTime.parse("2024-03-11T12:00:00");
	    LocalDateTime rightBoundaryRange = LocalDateTime.parse("2024-03-20T11:59:00");
	    List<Booking> bookingList = new ArrayList<>();
	    bookingList.add(new Booking(new ObjectId(), new ObjectId(), outOfRange));
	    bookingList.add(new Booking(new ObjectId(), new ObjectId(), withinRange));
	    bookingList.add(new Booking(new ObjectId(), new ObjectId(), leftBoundaryRange));
	    bookingList.add(new Booking(new ObjectId(), new ObjectId(), rightBoundaryRange));

	    // Filter bookings within the specified time range
	    List<Booking> bookingResults = bookingList.stream()
	            .filter(booking -> (booking.getBookingTime().isEqual(leftBoundaryRange) ||
	                                booking.getBookingTime().isAfter(leftBoundaryRange)) &&
	                               (booking.getBookingTime().isEqual(rightBoundaryRange) ||
	                                booking.getBookingTime().isBefore(rightBoundaryRange) ||
	                                booking.getBookingTime().isEqual(rightBoundaryRange)))
	            .collect(Collectors.toList());

	    // Create a custom pageable object
	    CustomPageable<Booking> customPageable = new CustomPageable<>() {
	        @Override
	        public List<Booking> getContent() {
	            return bookingResults;
	        }

	        @Override
	        public int getPageNumber() {
	            return 0;
	        }

	        @Override
	        public int getPageSize() {
	            return 10;
	        }

	        @Override
	        public long getTotalElements() {
	            return bookingResults.size();
	        }

	        @Override
	        public int getTotalPages() {
	            return 1;
	        }

	        @Override
	        public boolean hasNext() {
	            return false;
	        }

	        @Override
	        public boolean hasPrevious() {
	            return false;
	        }

	    };

	    // Create a BookingPage with Mock data
	    BookingPage bookingPageResults = new BookingPage(bookingResults, 0, 10, bookingResults.size(), 1);

	    // Mock the behavior of bookingRepository
	    when(bookingRepository.findBookingsByBookingTimeBetween(any(LocalDateTime.class), any(LocalDateTime.class), any(CustomPageable.class))).thenReturn(bookingPageResults);

	    // Call the method under test
	    BookingPage bookingPage = bookingRepository.findBookingsByBookingTimeBetween(leftBoundaryRange, rightBoundaryRange, customPageable);

	    // Assertions
	    assertNotNull(bookingPage);
	    assertEquals(bookingResults.size(), bookingPage.getContent().size());
	    assertEquals(bookingResults.size(), bookingPage.getTotalElements());
	    assertEquals(1, bookingPage.getTotalPages());
	}

	@SuppressWarnings("unchecked")
	@Test
	@DisplayName("Test findBookingsByBookingTimeBefore method")
	public void testFindBookingsByBookingTimeBefore() {
	    // Mock the Data
	    LocalDateTime withinRange = LocalDateTime.parse("2024-03-18T12:30:00");
	    LocalDateTime upperCeilingTime = LocalDateTime.parse("2024-03-11T12:00:00");
	    LocalDateTime outOfRange = LocalDateTime.parse("2024-03-20T11:59:00");
	    List<Booking> bookingList = new ArrayList<>();
	    bookingList.add(new Booking(new ObjectId(), new ObjectId(), outOfRange));
	    bookingList.add(new Booking(new ObjectId(), new ObjectId(), withinRange));
	    bookingList.add(new Booking(new ObjectId(), new ObjectId(), upperCeilingTime));

	    // Filter bookings within the specified time range
	    List<Booking> bookingResults = bookingList.stream()
	            .filter(booking -> (booking.getBookingTime().isEqual(upperCeilingTime) ||
	                                booking.getBookingTime().isBefore(upperCeilingTime)))
	            .collect(Collectors.toList());

	    // Create a custom pageable object
	    CustomPageable<Booking> customPageable = new CustomPageable<>() {
	        @Override
	        public List<Booking> getContent() {
	            return bookingResults;
	        }

	        @Override
	        public int getPageNumber() {
	            return 0;
	        }

	        @Override
	        public int getPageSize() {
	            return 10;
	        }

	        @Override
	        public long getTotalElements() {
	            return bookingResults.size();
	        }

	        @Override
	        public int getTotalPages() {
	            return 1;
	        }

	        @Override
	        public boolean hasNext() {
	            return false;
	        }

	        @Override
	        public boolean hasPrevious() {
	            return false;
	        }

	    };

	    // Create a BookingPage with Mock data
	    BookingPage bookingPageResults = new BookingPage(bookingResults, 0, 10, bookingResults.size(), 1);

	    // Mock the behavior of bookingRepository
	    when(bookingRepository.findBookingsByBookingTimeBefore(any(LocalDateTime.class), any(CustomPageable.class))).thenReturn(bookingPageResults);

	    // Call the method under test
	    BookingPage bookingPage = bookingRepository.findBookingsByBookingTimeBefore(upperCeilingTime, customPageable);

	    // Assertions
	    assertNotNull(bookingPage);
	    assertEquals(bookingResults.size(), bookingPage.getContent().size());
	    assertEquals(bookingResults.size(), bookingPage.getTotalElements());
	    assertEquals(1, bookingPage.getTotalPages());
	}
}
