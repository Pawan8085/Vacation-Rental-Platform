package com.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.Guest;

public interface GuestRepository extends JpaRepository<Guest, Long>{
	
	Optional<Guest> findByEmail(String email);
}
