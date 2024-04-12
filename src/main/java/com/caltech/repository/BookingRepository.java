package com.caltech.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import com.caltech.pojo.Booking;
import com.caltech.utils.BookingPage;
import com.caltech.utils.CustomPageable;

@Repository
public interface BookingRepository extends MongoRepository<Booking, ObjectId> {

    // Instantiate logger
    Logger log = LoggerFactory.getLogger(BookingRepository.class);
    
    Page<Booking> findAll(Pageable pageable);
    
    Page<Booking> findBookingsByUserId(ObjectId userId, Pageable pageable);
    
    Page<Booking> findBookingsByDoctorId(ObjectId doctorId, Pageable pageable);
    
    Page<Booking> findBookingsByBookingTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
    
    Page<Booking> findBookingsByBookingTimeBefore(LocalDateTime end, Pageable pageable);

    default BookingPage findAllBookings(CustomPageable<Booking> pageable) {
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "bookingTime"));

        Page<Booking> page = findAll(pageableRequest);
        
        List<Booking> content = page.getContent();
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        BookingPage customPage = new BookingPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        log.info("[BookingRepository] Fetching results from CustomPage Object: {}", customPage);
       
        return customPage;
    }
    
    default BookingPage findBookingsByUserId(ObjectId userId, CustomPageable<Booking> pageable) {
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "bookingTime"));

        Page<Booking> page = findBookingsByUserId(userId, pageableRequest);
        
        List<Booking> content = page.getContent();
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        BookingPage customPage = new BookingPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        log.info("[BookingRepository] Fetching results for user with ID <{}> from CustomPage Object: {}", userId, customPage);
       
        return customPage;
    }
    
    default BookingPage findBookingsByDoctorId(ObjectId doctorId, CustomPageable<Booking> pageable) {
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "bookingTime"));

        Page<Booking> page = findBookingsByDoctorId(doctorId, pageableRequest);
        
        List<Booking> content = page.getContent();
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());

        BookingPage customPage = new BookingPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        log.info("[BookingRepository] Fetching results for doctor with ID <{}> from CustomPage Object: {}", doctorId, customPage);
       
        return customPage;
    }
    
    default BookingPage findBookingsByBookingTimeBetween(LocalDateTime start, LocalDateTime end, CustomPageable<Booking> pageable) {
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "bookingTime"));

        Page<Booking> page = findBookingsByBookingTimeBetween(start, end, pageableRequest);
        
        List<Booking> content = page.getContent();
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
        
        BookingPage customPage = new BookingPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        log.info("[BookingRepository] Fetching results for bookings from <{}> to <{}> from CustomPage Object: {}", start, end, customPage);
       
        return customPage;
    }
    
    default BookingPage findBookingsByBookingTimeBefore(LocalDateTime end, CustomPageable<Booking> pageable) {
        Pageable pageableRequest = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "bookingTime"));

        Page<Booking> page = findBookingsByBookingTimeBefore(end, pageableRequest);
        
        List<Booking> content = page.getContent();
        long totalElements = page.getTotalElements();
        int totalPages = (int) Math.ceil((double) totalElements / pageable.getPageSize());
        
        BookingPage customPage = new BookingPage(content, pageable.getPageNumber(), pageable.getPageSize(), totalElements, totalPages);
        
        log.info("[BookingRepository] Fetching results for bookings before <{}> from CustomPage Object: {}", end, customPage);
       
        return customPage;
    }

    
}
