package com.servicebooking.admin.repository;



import com.servicebooking.admin.model.Booking;
import com.servicebooking.admin.model.Services;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Services, Long> {
}
