package com.servicebooking.service.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Provider {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String specialization;
    private String email;
    private String contactnumber;

    @OneToMany(mappedBy = "provider", cascade = CascadeType.ALL)
    private List<Services> services;

    public Provider() {}

    public Provider(String name, String specialization, String email, String contactnumber) {
        this.name = name;
        this.specialization = specialization;
        this.email = email;
        this.contactnumber = contactnumber;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Services> getServices() {
        return services;
    }

    public void setServices(List<Services> services) {
        this.services = services;
    }

    public String getContact() {
        return contactnumber;
    }

    public void setContact(String contactnumber) {
        this.contactnumber = contactnumber;
    }
}