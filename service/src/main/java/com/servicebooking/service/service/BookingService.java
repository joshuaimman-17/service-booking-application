package com.servicebooking.service.service;

import com.servicebooking.service.model.Booking;
import com.servicebooking.service.model.Services;
import com.servicebooking.service.repository.BookingRepository;
import com.servicebooking.service.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BookingService {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ServiceRepository serviceRepository;


    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    public Optional<Booking> getBookingById(Long id) {
        return bookingRepository.findById(id);
    }

    public List<Booking> getBookingsByServiceId(Long serviceId) {
        return bookingRepository.findByServices_Id(serviceId);
    }

    public List<Booking> getBookingsBetweenDates(LocalDateTime start, LocalDateTime end) {
        return bookingRepository.findByAppointmentDateTimeBetween(start, end);
    }

    public Booking createBooking(Booking booking, Long serviceId) {
        Optional<Services> serviceOpt = serviceRepository.findById(serviceId);
        if (serviceOpt.isPresent()) {
            booking.setServices(serviceOpt.get());
            return bookingRepository.save(booking);
        }
        return null;
    }

    public Optional<Booking> updateBookingStatus(Long id, String status) {
        Optional<Booking> bookingOpt = bookingRepository.findById(id);
        if (bookingOpt.isPresent()) {
            Booking booking = bookingOpt.get();
            booking.setStatus(status);
            return Optional.of(bookingRepository.save(booking));
        }
        return Optional.empty();
    }

    public void deleteBooking(Long id) {
        bookingRepository.deleteById(id);
    }

    public Booking saveBooking(Booking booking) {
        return bookingRepository.save(booking);
    }
}

