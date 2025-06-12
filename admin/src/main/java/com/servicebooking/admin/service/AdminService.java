package com.servicebooking.admin.service;

import com.servicebooking.admin.model.Booking;
import com.servicebooking.admin.model.Provider;
import com.servicebooking.admin.model.Services;
import com.servicebooking.admin.repository.BookingRepository;
import com.servicebooking.admin.repository.ProviderRepository;
import com.servicebooking.admin.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Scanner;

@Service
public class AdminService {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ProviderRepository providerRepository;
    @Autowired
    private BookingRepository bookingRepository;

    // Service CRUD
    public Services addService(Services service) {
        return serviceRepository.save(service);
    }

    public Services updateService(Long id, Services updatedServiceEntity) {
        return serviceRepository.findById(id).map(service -> {
            service.setName(updatedServiceEntity.getName());
            service.setDescription(updatedServiceEntity.getDescription());
            service.setProvider(updatedServiceEntity.getProvider());
            return serviceRepository.save(service);
        }).orElseThrow(() -> new RuntimeException("Service not found"));
    }

    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }

    // Provider CRUD
    public Provider addProvider(Provider provider) {
        return providerRepository.save(provider);
    }

    public Provider updateProvider(Long id, Provider updatedProvider) {
        return providerRepository.findById(id).map(provider -> {
            provider.setName(updatedProvider.getName());
            provider.setContact(updatedProvider.getContact());
            provider.setSpecialization(updatedProvider.getSpecialization());
            provider.setEmail(updatedProvider.getEmail());
            return providerRepository.save(provider);
        }).orElseThrow(() -> new RuntimeException("Provider not found"));
    }

    public void deleteProvider(Long id) {
        providerRepository.deleteById(id);
    }

    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
    }

    public Services getServiceById(Long id) {
        return serviceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Service not found"));
    }

    public List<Services> getAllServices() {
        return serviceRepository.findAll();
    }

    public Provider getProviderById(Long id) {
        return providerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Provider not found"));
    }
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

}
