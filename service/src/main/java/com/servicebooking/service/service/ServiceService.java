package com.servicebooking.service.service;


import com.servicebooking.service.model.Services;
import com.servicebooking.service.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;

@Component
public class ServiceService {

    @Autowired
    private ServiceRepository serviceRepository;

    public List<Services> getAllServices() {
        return serviceRepository.findAll();
    }

    public Optional<Services> getServiceById(Long id) {
        return serviceRepository.findById(id);
    }

    public Services saveService(Services services) {
        return serviceRepository.save(services);
    }

    public void deleteService(Long id) {
        serviceRepository.deleteById(id);
    }

    public Services findServiceById(Long id) {
        return serviceRepository.findById(id).orElse(null);
    }
}
