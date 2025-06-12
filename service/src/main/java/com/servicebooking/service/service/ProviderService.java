package com.servicebooking.service.service;

import com.servicebooking.service.model.Provider;
import com.servicebooking.service.repository.ProviderRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class ProviderService {
    @Autowired
    private ProviderRepository providerRepository;


    public List<Provider> getAllProviders() {
        return providerRepository.findAll();
    }

    public Optional<Provider> getProviderById(Long id) {
        return providerRepository.findById(id);
    }



    public Provider saveProvider(Provider provider) {
        return providerRepository.save(provider);
    }

    public void deleteProvider(Long id) {
        providerRepository.deleteById(id);
    }
}