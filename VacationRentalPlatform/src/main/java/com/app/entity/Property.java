package com.app.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Property {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long porpertyId;
	private String imgLink;
	private String location;
	private String propertyName;
	private PropertyType propertyType;
	private String offers;
	private String hostedBy;
	private Double rating;
	private Double oneNightPrice;
	private Long ratingCount;
	private Long ratingSum;
	
	
	@JsonIgnore
	@ManyToOne
	private Host host;
	
	
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "property")
	private List<Booking> bookings;
	
	@OneToMany(cascade = CascadeType.ALL)
	private List<Review> reviews;
}
