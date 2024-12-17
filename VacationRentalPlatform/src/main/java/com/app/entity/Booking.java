package com.app.entity;


import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long bookingId;
	private Long userId;
	private String hostedBy;
	private String propertyName;
	private String img;
	private Double oneNightPrice;
	private Integer numberOfNights;
	
	@JsonIgnore
	@ManyToOne
	private Property property;
	
	@JsonIgnore
	@ManyToOne
	private Guest guest;
}
