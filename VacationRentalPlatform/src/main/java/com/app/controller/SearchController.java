package com.app.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.app.entity.Host;
import com.app.entity.Property;
import com.app.repository.HostRepository;
import com.app.repository.PropertyRepository;

@Controller
public class SearchController {

	@Autowired
	private HostRepository hostRepository;

	@Autowired
	private PropertyRepository propertyRepository;

	// search handler
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query, Principal principal) {

		Host host = hostRepository.findByEmail(principal.getName()).get();

		List<Property> properties = propertyRepository.findByKeywordAndHost(query, host, PageRequest.of(0, 10));

		return ResponseEntity.ok(properties);

	}
	
	
	

}
