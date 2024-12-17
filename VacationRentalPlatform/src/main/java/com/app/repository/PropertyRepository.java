package com.app.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.app.entity.Host;
import com.app.entity.Property;
import com.app.entity.PropertyType;

public interface PropertyRepository extends JpaRepository<Property, Long>{
	
	Page<Property> findAll(Pageable pageable);
	
	@Query("from Property as p where p.host.hostId = :hostId")
	Page<Property> findPropertyByHost(@Param("hostId") Long hostId, Pageable pageable);
	
	 @Query("SELECT p FROM Property p WHERE (p.location LIKE %:keyword% OR p.hostedBy LIKE %:keyword% OR p.propertyName LIKE %:keyword%) AND p.host = :host")
	 List<Property> findByKeywordAndHost(String keyword, Host host, Pageable pageable);
	 
	 
	 
	 Page<Property> findByLocationContaining(String location, Pageable pageable);
	 
	 Page<Property> findByPropertyType(PropertyType propertyType, Pageable pageable);
	 
	 Page<Property> findByHostedByContaining(String hostedBy, Pageable pageable);
	 
	 Page<Property> findByOrderByLocationAsc(Pageable pageable);
	 Page<Property> findByOrderByPropertyTypeAsc(Pageable pageable);
	 
	 @Query("SELECT p FROM Property p WHERE lower(p.location) LIKE %:keyword% OR lower(p.hostedBy) LIKE %:keyword% OR lower(p.propertyName) LIKE %:keyword%")
	 Page<Property> findByKeyWord(@Param("keyword") String keyword, Pageable pageable);
	 
	 



}
