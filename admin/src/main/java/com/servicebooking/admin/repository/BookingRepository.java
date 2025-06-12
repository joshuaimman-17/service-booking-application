package com.servicebooking.admin.repository;


import com.servicebooking.admin.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
     List<Booking> findByServices_Id(Long serviceId);
    List<Booking> findByAppointmentDateTimeBetween(LocalDateTime start, LocalDateTime end);
}