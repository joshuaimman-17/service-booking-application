package com.servicebooking.service;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;

@SpringBootApplication
public class ServiceBookingApplication implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public static void main(String[] args) {
        SpringApplication.run(ServiceBookingApplication.class, args);
    }

    @Override
    public void run(String... args) {
        createProviderTable();
        createServicesTable();
        createBookingTable();
    }

    private void createProviderTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS provider (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    specialization VARCHAR(100),
                    email VARCHAR(100) UNIQUE NOT NULL,
                    contactnumber VARCHAR(100) NOT NULL
                );
                """;
        jdbcTemplate.execute(sql);
        System.out.println("Provider table created or already exists.");
    }

    private void createServicesTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS services (
                    id SERIAL PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    description TEXT,
                    provider_id BIGINT,
                    CONSTRAINT fk_provider FOREIGN KEY (provider_id) REFERENCES provider(id) ON DELETE CASCADE
                );
                """;
        jdbcTemplate.execute(sql);
        System.out.println("Services table created or already exists.");
    }

    private void createBookingTable() {
        String sql = """
                CREATE TABLE IF NOT EXISTS booking (
                    id SERIAL PRIMARY KEY,
                    customer_name VARCHAR(100) NOT NULL,
                    customer_email VARCHAR(100) NOT NULL,
                    customer_phone VARCHAR(15),
                    appointment_date_time TIMESTAMP NOT NULL,
                    status VARCHAR(20) DEFAULT 'PENDING',
                    service_id BIGINT,
                    CONSTRAINT fk_service FOREIGN KEY (service_id) REFERENCES services(id) ON DELETE CASCADE
                );
                """;
        jdbcTemplate.execute(sql);
        System.out.println("Booking table created or already exists.");
    }
}
