package com.caltech.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.caltech.exception.BookingNotFoundException;
import com.caltech.pojo.Booking;
import com.caltech.repository.BookingRepository;
import com.caltech.utils.BookingPage;
import com.caltech.utils.CustomPageable;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BookingService {

	private final BookingRepository bookingRepository;
	
	@Autowired
	public BookingService(BookingRepository bookingRepository) {
		this.bookingRepository = bookingRepository;
	}
	
	public Optional<Booking> findBookingById(ObjectId bookingId) {
		return bookingRepository.findById(bookingId);
	}
	
	public BookingPage findAllBookings(CustomPageable<Booking> pageable) {
		BookingPage customPage = bookingRepository.findAllBookings(pageable);
        if (customPage == null) {
            log.warn("CustomPage is null");
            // Create and return an empty BookingPage
            return new BookingPage(new ArrayList<>(), 0, 0, 0, 0);
        }
        log.info("Fetching all bookings with pageable: {}", customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} bookings", customPage.getContent().size());
        customPage.getContent().forEach(booking -> log.info("Booking: {}", booking.getUserId()));
        return customPage;
	}
	
	public BookingPage findBookingsByUserId(ObjectId userId, CustomPageable<Booking> pageable) {
		BookingPage customPage = bookingRepository.findBookingsByUserId(userId, pageable);
        if (customPage == null) {
            log.warn("CustomPage is null");
            // Create and return an empty BookingPage
            return new BookingPage(new ArrayList<>(), 0, 0, 0, 0);
        }
        log.info("Fetching all bookings with pageable: {}", customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} bookings", customPage.getContent().size());
        customPage.getContent().forEach(booking -> log.info("Booking: {}", booking.getUserId()));
        return customPage;
	}
	
	public BookingPage findBookingsByDoctorId(ObjectId doctorId, CustomPageable<Booking> pageable) {
		BookingPage customPage = bookingRepository.findBookingsByDoctorId(doctorId, pageable);
        if (customPage == null) {
            log.warn("CustomPage is null");
            // Create and return an empty BookingPage
            return new BookingPage(new ArrayList<>(), 0, 0, 0, 0);
        }
        log.info("Fetching all bookings with pageable: {}", customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} bookings", customPage.getContent().size());
        customPage.getContent().forEach(booking -> log.info("Booking: {}", booking.getUserId()));
        return customPage;
	}
	
	public BookingPage findBookingsByBookingTimeBetween(LocalDateTime start, LocalDateTime end, CustomPageable<Booking> pageable) {
		BookingPage customPage = bookingRepository.findBookingsByBookingTimeBetween(start, end, pageable);
        if (customPage == null) {
            log.warn("CustomPage is null");
            // Create and return an empty BookingPage
            return new BookingPage(new ArrayList<>(), 0, 0, 0, 0);
        }
        log.info("Fetching all bookings with pageable: {}", customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} bookings", customPage.getContent().size());
        customPage.getContent().forEach(booking -> log.info("Booking: {}", booking.getUserId()));
        return customPage;
	}
	
	public BookingPage findBookingsByBookingTimeBefore(LocalDateTime end, CustomPageable<Booking> pageable) {
		BookingPage customPage = bookingRepository.findBookingsByBookingTimeBefore(end, pageable);
        if (customPage == null) {
            log.warn("CustomPage is null");
            // Create and return an empty BookingPage
            return new BookingPage(new ArrayList<>(), 0, 0, 0, 0);
        }
        log.info("Fetching all bookings with pageable: {}", customPage);
        log.info("Content: {}", customPage.getContent());
        log.info("Total Elements: {}", customPage.getTotalElements());
        log.info("Total Pages: {}", customPage.getTotalPages());
        log.info("Fetched {} bookings", customPage.getContent().size());
        customPage.getContent().forEach(booking -> log.info("Booking: {}", booking.getUserId()));
        return customPage;
	}
	
	public Booking createBooking(Booking booking) {
		log.info("Creating a new Booking under User with User ID: {}", booking.getUserId());
		return bookingRepository.save(booking);
	}
	
	public Booking updateBooking(ObjectId bookingId, Booking updatedBooking) throws BookingNotFoundException {
		Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
		if (optionalBooking.isPresent()) {
			Booking existingBooking = optionalBooking.get();
			existingBooking.setDoctorId(updatedBooking.getDoctorId());
			existingBooking.setBookingTime(updatedBooking.getBookingTime());
			return bookingRepository.save(existingBooking);
		} else {
			throw new BookingNotFoundException("Booking not found with bookingId: " + bookingId);
		}
	}
	
	public void deleteBooking(ObjectId bookingId) {
		Optional<Booking> optionalBooking = bookingRepository.findById(bookingId);
        if (optionalBooking.isPresent()) {
            log.info("Deleting booking with bookingId: {}", bookingId);
            bookingRepository.deleteById(bookingId);
            log.info("Booking with bookingId: {} deleted successfully", bookingId);
        } else {
            log.error("Booking not found with bookingId: {}", bookingId);
        }
	}
}
