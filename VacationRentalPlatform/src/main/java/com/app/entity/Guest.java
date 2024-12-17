package com.app.entity;

import java.time.LocalDate;
import java.util.List;



import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Guest {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long guestId;
	
	@NotBlank(message = "name is required")
	@Size(min = 2, max = 20)
	private String name;
	
	@NotBlank(message = "email is required")
	@Column(unique = true)
	private String email;
	
	@NotNull
	@Size(min = 6, message = "Password must be at least 6 characters")
	private String password;
	
	@NotNull
	private Gender gender;
	
	@NotNull
	private LocalDate dateOfBirth;
	private String bio;
	private String role;
	
	@OneToMany(mappedBy = "guest")
	private List<Booking> bookings;
}
