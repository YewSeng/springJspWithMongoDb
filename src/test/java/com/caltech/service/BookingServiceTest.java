package com.caltech.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.caltech.exception.BookingNotFoundException;
import com.caltech.pojo.Booking;
import com.caltech.repository.BookingRepository;
import com.caltech.utils.BookingPage;
import com.caltech.utils.CustomPageable;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

	@Mock
	private BookingRepository bookingRepository;
	
	@InjectMocks
	private BookingService bookingService;
	
	@Test
	@DisplayName("Test findBookingById method")
	public void testFindBookingById() {
		ObjectId bookingId = new ObjectId();
		Booking booking = new Booking(new ObjectId(), new ObjectId(), LocalDateTime.now());
		booking.setBookingId(bookingId);
		when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
		Optional<Booking> foundBookingOptional = bookingService.findBookingById(bookingId);
		assertTrue(foundBookingOptional.isPresent());
		assertEquals(booking, foundBookingOptional.get());
	}
	
	@Test
	@DisplayName("Test findAllBookings method")
	public void testFindAllBookings() {
		List<Booking> bookingList = new ArrayList<>();
		bookingList.add(new Booking(new ObjectId(), new ObjectId(), LocalDateTime.now()));
		bookingList.add(new Booking(new ObjectId(), new ObjectId(), LocalDateTime.now()));
		bookingList.add(new Booking(new ObjectId(), new ObjectId(), LocalDateTime.now()));
		BookingPage bookingPage = new BookingPage(bookingList, 0, 10, bookingList.size(), 1);
		CustomPageable<Booking> customPageable = new BookingPage(0,10);
		when(bookingRepository.findAllBookings(customPageable)).thenReturn(bookingPage);
		BookingPage foundBookingPage = bookingService.findAllBookings(customPageable);
		assertEquals(bookingPage, foundBookingPage);
		assertEquals(bookingList.size(), foundBookingPage.getContent().size());
	}
	
	@Test
	@DisplayName("Test findBookingsByUserId method")
	public void testFindBookingsByUserId() {
		ObjectId user1 = new ObjectId();
		ObjectId user2 = new ObjectId();
		List<Booking> bookingList = new ArrayList<>();
		bookingList.add(new Booking(user1, new ObjectId(), LocalDateTime.now()));
		bookingList.add(new Booking(user1, new ObjectId(), LocalDateTime.now()));
		bookingList.add(new Booking(user2, new ObjectId(), LocalDateTime.now()));
		List<Booking> bookingResults = bookingList.stream()
				.filter(booking -> booking.getUserId().equals(user1))
				.collect(Collectors.toList());
		BookingPage bookingPage = new BookingPage(bookingResults, 0, 10, bookingResults.size(), 1);
		CustomPageable<Booking> customPageable = new BookingPage(0,10);
		when(bookingRepository.findBookingsByUserId(user1, customPageable)).thenReturn(bookingPage);
		BookingPage foundBookingPage = bookingService.findBookingsByUserId(user1, customPageable);
		assertEquals(bookingPage, foundBookingPage);
		assertEquals(bookingResults.size(), foundBookingPage.getContent().size());
	}
	
	@Test
	@DisplayName("Test findBookingsByDoctorId method")
	public void testFindBookingsByDoctorId() {
		ObjectId doctor1 = new ObjectId();
		ObjectId doctor2 = new ObjectId();
		List<Booking> bookingList = new ArrayList<>();
		bookingList.add(new Booking(new ObjectId(), doctor1, LocalDateTime.now()));
		bookingList.add(new Booking(new ObjectId(), doctor1, LocalDateTime.now()));
		bookingList.add(new Booking(new ObjectId(), doctor2, LocalDateTime.now()));
		List<Booking> bookingResults = bookingList.stream()
				.filter(booking -> booking.getDoctorId().equals(doctor1))
				.collect(Collectors.toList());
		BookingPage bookingPage = new BookingPage(bookingResults, 0, 10, bookingResults.size(), 1);
		CustomPageable<Booking> customPageable = new BookingPage(0,10);
		when(bookingRepository.findBookingsByDoctorId(doctor1, customPageable)).thenReturn(bookingPage);
		BookingPage foundBookingPage = bookingService.findBookingsByDoctorId(doctor1, customPageable);
		assertEquals(bookingPage, foundBookingPage);
		assertEquals(bookingResults.size(), foundBookingPage.getContent().size());
	}
	
	@Test
	@DisplayName("Test findBookingsByBookingTimeBetween method")
	public void testFindBookingsByBookingTimeBetween() {
	    LocalDateTime outOfRange = LocalDateTime.parse("2022-03-18T09:30:00");
	    LocalDateTime withinRange = LocalDateTime.parse("2024-03-18T12:30:00");
	    LocalDateTime leftBoundaryRange = LocalDateTime.parse("2024-03-11T12:00:00");
	    LocalDateTime rightBoundaryRange = LocalDateTime.parse("2024-03-20T11:59:00");
	    List<Booking> bookingList = new ArrayList<>();
	    bookingList.add(new Booking(new ObjectId(), new ObjectId(), outOfRange));
	    bookingList.add(new Booking(new ObjectId(), new ObjectId(), withinRange));
	    bookingList.add(new Booking(new ObjectId(), new ObjectId(), leftBoundaryRange));
	    bookingList.add(new Booking(new ObjectId(), new ObjectId(), rightBoundaryRange));
	    List<Booking> bookingResults = bookingList.stream()
	            .filter(booking -> (booking.getBookingTime().isEqual(leftBoundaryRange) ||
	                                booking.getBookingTime().isAfter(leftBoundaryRange)) &&
	                               (booking.getBookingTime().isEqual(rightBoundaryRange) ||
	                                booking.getBookingTime().isBefore(rightBoundaryRange) ||
	                                booking.getBookingTime().isEqual(rightBoundaryRange)))
	            .collect(Collectors.toList());
		BookingPage bookingPage = new BookingPage(bookingResults, 0, 10, bookingResults.size(), 1);
		CustomPageable<Booking> customPageable = new BookingPage(0,10);
		when(bookingRepository.findBookingsByBookingTimeBetween(leftBoundaryRange, rightBoundaryRange, customPageable)).thenReturn(bookingPage);
		BookingPage foundBookingPage = bookingService.findBookingsByBookingTimeBetween(leftBoundaryRange, rightBoundaryRange, customPageable);
		assertEquals(bookingPage, foundBookingPage);
		assertEquals(bookingResults.size(), foundBookingPage.getContent().size());
	}
	
	@Test
	@DisplayName("Test findBookingsByBookingTimeBefore method")
	public void testFindBookingsByBookingTimeBefore() {
	    LocalDateTime withinRange = LocalDateTime.parse("2024-03-18T12:30:00");
	    LocalDateTime upperCeilingTime = LocalDateTime.parse("2024-03-11T12:00:00");
	    LocalDateTime outOfRange = LocalDateTime.parse("2024-03-20T11:59:00");
	    List<Booking> bookingList = new ArrayList<>();
	    bookingList.add(new Booking(new ObjectId(), new ObjectId(), outOfRange));
	    bookingList.add(new Booking(new ObjectId(), new ObjectId(), withinRange));
	    bookingList.add(new Booking(new ObjectId(), new ObjectId(), upperCeilingTime));
	    List<Booking> bookingResults = bookingList.stream()
	            .filter(booking -> (booking.getBookingTime().isEqual(upperCeilingTime) ||
	                                booking.getBookingTime().isBefore(upperCeilingTime)))
	            .collect(Collectors.toList());
		BookingPage bookingPage = new BookingPage(bookingResults, 0, 10, bookingResults.size(), 1);
		CustomPageable<Booking> customPageable = new BookingPage(0,10);
		when(bookingRepository.findBookingsByBookingTimeBefore(upperCeilingTime, customPageable)).thenReturn(bookingPage);
		BookingPage foundBookingPage = bookingService.findBookingsByBookingTimeBefore(upperCeilingTime, customPageable);
		assertEquals(bookingPage, foundBookingPage);
		assertEquals(bookingResults.size(), foundBookingPage.getContent().size());
	}
	
	@Test
	@DisplayName("Test createBooking method")
	public void testCreateBooking() {
		Booking booking = new Booking(new ObjectId(), new ObjectId(), LocalDateTime.now());
		when(bookingRepository.save(booking)).thenReturn(booking);
		Booking savedBooking = bookingService.createBooking(booking);
		assertEquals(booking, savedBooking);
	}
	
	@Test
	@DisplayName("Test updateBooking method")
	public void testUpdateBooking() throws BookingNotFoundException {
		ObjectId bookingId = new ObjectId();
		LocalDateTime outOfRange = LocalDateTime.parse("2022-03-18T09:30:00");
		LocalDateTime now = LocalDateTime.parse("2024-03-20T11:59:00");
		Booking existingBooking = new Booking(new ObjectId(), new ObjectId(), outOfRange);
		Booking updatedBooking = new Booking(new ObjectId(), new ObjectId(), now);
		when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(existingBooking));
		when(bookingRepository.save(existingBooking)).thenReturn(updatedBooking);
		Booking returnedBooking = bookingService.updateBooking(bookingId, updatedBooking);
		assertEquals(updatedBooking, returnedBooking);
		assertEquals(now, returnedBooking.getBookingTime());
	}
	
	@Test
	@DisplayName("Test updateBooking method - Booking Not Found")
	public void testUpdateBookingNotFound() throws BookingNotFoundException {
		ObjectId bookingId = new ObjectId();
		LocalDateTime now = LocalDateTime.parse("2024-03-20T11:59:00");
		Booking updatedBooking = new Booking(new ObjectId(), new ObjectId(), now);
		when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());
		BookingNotFoundException exception = assertThrows(BookingNotFoundException.class, () -> {
    		bookingService.updateBooking(bookingId, updatedBooking);
    	});
    	assertNotNull(exception);	    	
	}
	
	@Test
	@DisplayName("Test deleteBooking method")
	public void testDeleteBooking() {
		ObjectId bookingId = new ObjectId();
		Booking booking = new Booking(new ObjectId(), new ObjectId(), LocalDateTime.now());
		when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
		bookingService.deleteBooking(bookingId);
		verify(bookingRepository, times(1)).deleteById(bookingId);
	}
	
	@Test
	@DisplayName("Test deleteBooking method - Booking Id Not Found")
	public void testDeleteBookingNotFound() {
		ObjectId bookingId = new ObjectId();
		when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());
		bookingService.deleteBooking(bookingId);
		verify(bookingRepository, never()).deleteById(bookingId);
	}
}
