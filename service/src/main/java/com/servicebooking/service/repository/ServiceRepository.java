package com.servicebooking.service.repository;



import com.servicebooking.service.model.Services;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<Services, Long> {
}
