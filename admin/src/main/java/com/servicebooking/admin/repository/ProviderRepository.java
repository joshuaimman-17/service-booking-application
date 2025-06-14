package com.servicebooking.admin.repository;

import com.servicebooking.admin.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProviderRepository extends JpaRepository<Provider, Long> {
}
