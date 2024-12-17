package com.app.controller;


import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.objenesis.instantiator.basic.NewInstanceInstantiator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import com.app.entity.Booking;
import com.app.entity.Guest;
import com.app.entity.Host;
import com.app.entity.Property;
import com.app.entity.PropertyType;
import com.app.repository.BookingRepository;
import com.app.repository.GuestRepository;
import com.app.repository.HostRepository;
import com.app.repository.PropertyRepository;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class HomeController {
	
	@Autowired
	private HostRepository hostRepository;
	
	@Autowired
	private PropertyRepository propertyRepository;
	
	@Autowired
	private GuestRepository guestRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private BookingRepository bookingRepository;
	
	// set current user data to session
	@ModelAttribute
	public void checkAuthentication(Principal principal, HttpSession session) {
		
		boolean isAuthenticated = false;
		String currentUser = "";
		
		
		if(principal != null) {
			
			isAuthenticated = true;
			Optional<Host> optHost = hostRepository.findByEmail(principal.getName());
			if(optHost.isPresent()) {
				
				Host host = optHost.get();
				currentUser = host.getName();
					
			}
			
			
			Optional<Guest> optGuest = guestRepository.findByEmail(principal.getName());
			if(optGuest.isPresent()) {
				
				Guest guest = optGuest.get();
				currentUser = guest.getName();
				
					
			}
		}
		
		session.setAttribute("isAuthenticated", isAuthenticated);
		session.setAttribute("currentUser", currentUser);
		
	}
	
	@GetMapping("/")
	public String redirectToHome() {
		
		return "redirect:/0";
	}
	
	@GetMapping("/{page}")
	public String home(@PathVariable("page") Integer page, Model model) {
		
		Pageable pageable = PageRequest.of(page, 9);
		
		// set property info to model attribute
				
		Page<Property> properties = propertyRepository.findAll(pageable);
		
		// set property info to model attribute
		model.addAttribute("url", "");
		model.addAttribute("properties", properties);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages",  properties.getTotalPages());
		
		model.addAttribute("title", "Home");
		
		return "home";
	}
	
	@GetMapping("/{page}/location/{location}")
	public String homeLocationSearch(@PathVariable("page") Integer page, @PathVariable("location") String location, Model model) {
		
		Pageable pageable = PageRequest.of(page, 9);
		
		// set property info to model attribute
				
		Page<Property> properties = propertyRepository.findByLocationContaining(location, pageable);
		
		System.out.println(properties.getTotalElements());
		
		// set property info to model attribute
		model.addAttribute("url", "/location/"+location); // dynamic url for pagination
		model.addAttribute("properties", properties);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages",  properties.getTotalPages());
		
		return "home";
	}
	
	
	@GetMapping("/{page}/host/{host}")
	public String homeHostSearch(@PathVariable("page") Integer page, @PathVariable("host") String host, Model model) {
		
		System.out.println("Calling host method");
		Pageable pageable = PageRequest.of(page, 9);
		
		// set property info to model attribute
				
		Page<Property> properties = propertyRepository.findByHostedByContaining(host, pageable);
		
		// set property info to model attribute
		model.addAttribute("url", "/host/"+host); // dynamic url for pagination
		model.addAttribute("properties", properties);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages",  properties.getTotalPages());
		
		return "home";
	}
	
	@GetMapping("/{page}/property/{property-type}")
	public String homePropertyTypeSearch(@PathVariable("page") Integer page, @PathVariable("property-type") PropertyType propertyType, Model model) {
		Pageable pageable = PageRequest.of(page, 9);
		
		// set property info to model attribute
				
		Page<Property> properties = propertyRepository.findByPropertyType(propertyType, pageable);
		
		// set property info to model attribute
		model.addAttribute("url", "/property/"+propertyType); // dynamic url for pagination
		model.addAttribute("properties", properties);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages",  properties.getTotalPages());
		
		return "home";
	}
	
	
	@GetMapping("/{page}/sort/location")
	public String sortByLocationSearch(@PathVariable("page") Integer page, Model model) {
		
		Pageable pageable = PageRequest.of(page, 9);
		
		// set property info to model attribute
				
		Page<Property> properties = propertyRepository.findByOrderByLocationAsc(pageable);
		
		// set property info to model attribute
		model.addAttribute("url", "/sort/location"); // dynamic url for pagination
		model.addAttribute("properties", properties);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages",  properties.getTotalPages());
		
		return "home";
	}
	
	
	@GetMapping("/{page}/sort/property-type")
	public String sortByPropertyTypeSearch(@PathVariable("page") Integer page, Model model) {
		
		Pageable pageable = PageRequest.of(page, 9);
		
		// set property info to model attribute
				
		Page<Property> properties = propertyRepository.findByOrderByPropertyTypeAsc(pageable);
		
		// set property info to model attribute
		model.addAttribute("url", "/sort/property-type"); // dynamic url for pagination
		model.addAttribute("properties", properties);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages",  properties.getTotalPages());
		
		return "home";
	}
	
	
	@GetMapping("/{page}/search/{query}")
	public String search(@PathVariable("query") String query, @PathVariable("page") Integer page , Model model) {
		System.out.println(page);
		Pageable pageable = PageRequest.of(page, 9);
		
		Page<Property> properties = propertyRepository.findByKeyWord(query, pageable);
		
		
			// set property info to model attribute
			model.addAttribute("url", "/search/"+query); // dynamic url for pagination
			model.addAttribute("properties", properties);
			model.addAttribute("currentPage", page);
			model.addAttribute("totalPages",  properties.getTotalPages());
		
		
		return "home";
				
		
	}
	
	
	@GetMapping("/property/{propertyId}")
	public String viewProperty(@PathVariable("propertyId") long propertyId, Model model, Principal principal, HttpSession session) {
		
		Optional<Property> optProperty = propertyRepository.findById(propertyId);
		model.addAttribute("bad_request", false);
		model.addAttribute("title", "View-Property");
		if(optProperty.isEmpty()) {
			
			
			model.addAttribute("bad_request", true);
			
			return "view_property";
		}
		
		
		// add property to model
		model.addAttribute("property", optProperty.get());
		
		// check for authentication if user is authenticated then show add rating option 
		boolean canRate = false;
		if(principal != null) {
			
		    Optional<Guest> optGuest =	guestRepository.findByEmail(principal.getName());
		    if(optGuest.isPresent()) {
		    	
		    	List<Booking> bookings =	bookingRepository.findByPropertyAndGuest(optProperty.get(), optGuest.get());
		    	
		    	if(bookings.size() > 0) {
		    		
		    		//  user is authenticated and has booked property, add rating id to session for add rating process
		    		session.setAttribute("propertyId", propertyId);
		    		canRate = true;
		    	}
		    	
		    }
		}
		
		model.addAttribute("canRate", canRate);
		return "view_property";
	}
	
	
	@GetMapping("/host-signup")
	public String hostSignUp(Model model) {
		
		model.addAttribute("host", new Host());
		model.addAttribute("title", "Host-SignUp");
		return "host_signup";
	}
	
	
	@PostMapping("/host-signup-process")
	public String hostSignUpProcess(@ModelAttribute("host") Host host) {
		
		System.out.println(host.getHostingSince());
		System.out.println(host.getHostStatus());
		System.out.println(host.getPropertyType());
		host.setRole("HOST");
		host.setPassword(passwordEncoder.encode(host.getPassword()));
		hostRepository.save(host);
		return "redirect:/host-signup";
	}
	
	@GetMapping("/user-signup")
	public String userSignUp(Model model) {
		
		model.addAttribute("user", new Guest());
		model.addAttribute("title", "User-SignUp");
		return "user_signup";
	}
	
	@PostMapping("/user-signup-process")
	public String userSignUpProcess(@Valid @ModelAttribute("user") Guest guest, BindingResult result, Model model) {
		
		if(result.hasErrors()) {
			
			model.addAttribute("user", guest);
			return "user_signup";
		}
		
		guest.setPassword(passwordEncoder.encode(guest.getPassword()));
		guest.setRole("USER");
		
		guestRepository.save(guest);
		
		return "user_signup";
	}
	@GetMapping("/signIn")
	public String signIn(Authentication authentication) {
		
        
		
		return "login";
	}
	
	
	@GetMapping("/user-redirect")
	public String userRedirect(Authentication authentication) {
		
	

		if (authentication != null && authentication.isAuthenticated()) {
            // Check the authorities of the current user
            for (GrantedAuthority authority : authentication.getAuthorities()) {
               System.out.println(authority.getAuthority());
                if ("HOST".equals(authority.getAuthority())) {
                    return "redirect:/host/index";
                }
                
                else if ("USER".equals(authority.getAuthority())) {
                    return "redirect:/user/index";
                }
            }
        }
		
		return "login";
	}
	
	
}
