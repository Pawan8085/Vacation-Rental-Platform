package com.app.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.app.entity.Guest;
import com.app.entity.Host;
import com.app.repository.GuestRepository;
import com.app.repository.HostRepository;



@Service
public class CustomUserDetailService implements UserDetailsService{
	
	@Autowired
	private HostRepository hostRepository;
	
	@Autowired
	private GuestRepository guestRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
			
		Optional<Host> optHost = hostRepository.findByEmail(username);
		if(optHost.isPresent()) {
			
			Host host = optHost.get();
			
			List<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(new SimpleGrantedAuthority(host.getRole()));
			
			return new User(host.getEmail(), host.getPassword(), authorities);
			
			
		}
		
		
		Optional<Guest> optGuest = guestRepository.findByEmail(username);
		if(optGuest.isPresent()) {
			
			Guest guest = optGuest.get();
			List<GrantedAuthority> authorities = new ArrayList<>();
			authorities.add(new SimpleGrantedAuthority(guest.getRole()));
			
			return new User(guest.getEmail(), guest.getPassword(), authorities);
			
			
		}
		throw new UsernameNotFoundException("Invalid  User Name!");
		
	}

}
