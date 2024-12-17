package com.app.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.app.entity.Host;

public interface HostRepository extends JpaRepository<Host, Long>{
	
	Optional<Host> findByEmail(String email);
}
