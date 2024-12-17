package com.app.controller;

import java.security.Principal;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.app.entity.Booking;
import com.app.entity.Gender;
import com.app.entity.Guest;
import com.app.entity.Property;
import com.app.entity.Review;
import com.app.helper.Message;
import com.app.repository.GuestRepository;
import com.app.repository.PropertyRepository;
import com.fasterxml.jackson.core.sym.Name;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	private PropertyRepository propertyRepository;

	@Autowired
	private GuestRepository guestRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@GetMapping("/index")
	public String userHome(Model model, Principal principal) {

		Guest guest = guestRepository.findByEmail(principal.getName()).get();
		model.addAttribute("guest", guest);
		model.addAttribute("user", new Guest());
		model.addAttribute("title", "User-Home");

		return "guest/user_dashboard";
	}

	@PostMapping("/book-property/{propertyId}")
	public String bookProperty(@PathVariable("propertyId") Long propertyId,
			@RequestParam("totalNights") int totalNights, Principal principal, HttpSession session) {

		Optional<Property> optProperty = propertyRepository.findById(propertyId);
		if (optProperty.isEmpty()) {

			return "redirect:/property/" + propertyId;

		}

//		System.out.println(totalNights);
		Property property = optProperty.get();

		Guest guest = guestRepository.findByEmail(principal.getName()).get();

		// create Booking object
		Booking book = new Booking();
		book.setUserId(guest.getGuestId());
		book.setHostedBy(property.getHostedBy());
		book.setPropertyName(property.getPropertyName());
		book.setOneNightPrice(property.getOneNightPrice());
		book.setNumberOfNights(totalNights);
		book.setImg(property.getImgLink());

		book.setGuest(guest);
		book.setProperty(property);

		property.getBookings().add(book);
		guest.getBookings().add(book);

		propertyRepository.save(property);
		guestRepository.save(guest);

		return "redirect:/property/" + propertyId;
	}

	@GetMapping("/change-password")
	public String updateGuestProfile(Model model, Principal principal) {

		Guest guest = guestRepository.findByEmail(principal.getName()).get();
		model.addAttribute("guest", guest);
		model.addAttribute("title", "User-Change-Password");
		return "guest/user_change_password";
	}

	@GetMapping("/bookings")
	public String guestBookings(Model model, Principal principal) {

		Guest guest = guestRepository.findByEmail(principal.getName()).get();
		model.addAttribute("guest", guest);
		model.addAttribute("bookings", guest.getBookings());
		model.addAttribute("title", "User-Bookings");
		return "guest/user_bookings";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {

		// get current user
		String userName = principal.getName();
		Guest currentUser = guestRepository.findByEmail(userName).get();

		if (newPassword.length() < 6) {

			session.setAttribute("message", new Message("Password must have atleast 6 characters...", "danger"));
			return "redirect:/user/change-password";
		}

		else if (passwordEncoder.matches(oldPassword, currentUser.getPassword())) {

			// change the password
			currentUser.setPassword(passwordEncoder.encode(newPassword));
			guestRepository.save(currentUser);
			session.setAttribute("message", new Message("Your password changed successfully...", "success"));

		} else {

			// error
			session.setAttribute("message", new Message("Enter valid old password...", "danger"));
			return "redirect:/user/change-password";
		}

//		System.out.println("OldPassword : "+oldPassword);
//		System.out.println("NewPassword : "+newPassword);
		return "redirect:/user/change-password";
	}

	@PostMapping("/profile-update")
	public String guestProfileUpate(@ModelAttribute("user") Guest guest, Principal principal, HttpSession session) {

		// Get the current guest

		Guest currentGuest = guestRepository.findByEmail(principal.getName()).get();

		// update the updated value

		if (guest.getName() != null)
			currentGuest.setName(guest.getName());
		if (guest.getEmail() != null)
			currentGuest.setEmail(guest.getEmail());
		if (guest.getGender() != null)
			currentGuest.setGender(guest.getGender());
		if (guest.getDateOfBirth() != null)
			currentGuest.setDateOfBirth(guest.getDateOfBirth());
		if (guest.getBio() != null)
			currentGuest.setBio(guest.getBio());

		// save updated guest
		guestRepository.save(currentGuest);

		session.setAttribute("message", new Message("Your details updated successfully...", "success"));

		return "redirect:/user/index";

	}

	// method for calculating rating
	public String formatRating(long ratingCount, long ratingSum) {
		if (ratingCount == 0) {
			return "0.0"; // No ratings, return 0.0
		}

		double averageRating = (double) ratingSum / ratingCount;
		DecimalFormat df = new DecimalFormat("0.0"); // Round to one decimal place
		return df.format(averageRating);
	}

	@PostMapping("/property/add-review")
	public String addReview(@RequestParam("rating") String rating, @RequestParam("comment") String comment, Model model,
			HttpSession session, Principal principal) {

		// validate rating
		try {
			int userRating = Integer.parseInt(rating);

			if (userRating >= 1 && userRating <= 5) {

				// Get the current guest
				Guest currentGuest = guestRepository.findByEmail(principal.getName()).get();

				// create review obj
				Review review = new Review();
				review.setReviewerId(currentGuest.getGuestId());
				review.setReviewerName(currentGuest.getName());
				review.setReviewerEmail(currentGuest.getEmail());
				review.setComment(comment);
				review.setRating(userRating);

				// get the property
				Property property = propertyRepository.findById((long) session.getAttribute("propertyId")).get();

				// increase rating count & rating sum
				property.setRatingCount(property.getRatingCount() + 1);
				property.setRatingSum(property.getRatingSum() + userRating);

				// update the rating
				property.setRating(
						Double.parseDouble(formatRating(property.getRatingCount(), property.getRatingSum())));

				// add review to the property
				property.getReviews().add(review);
				propertyRepository.save(property);

				session.setAttribute("message", new Message("Comment added successfully...", "success"));
				return "redirect:/property/" + session.getAttribute("propertyId");

			} else {
				session.setAttribute("message", new Message("Select a valid rating !", "danger"));
				return "redirect:/property/" + session.getAttribute("propertyId");
			}

		} catch (NumberFormatException e) {

			session.setAttribute("message", new Message("Select a valid rating !", "danger"));
			return "redirect:/property/" + session.getAttribute("propertyId");
		}

	}

}
