package com.team10.servicebooking.login.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.team10.servicebooking.login.model.Users;

@Repository
public interface UserRepository extends JpaRepository<Users, Long>{

    Optional<Users> findByUsername(String username);

    Users findByEmail(String email);
    boolean existsByUsername(String username);


}