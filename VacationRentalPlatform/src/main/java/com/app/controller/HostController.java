package com.app.controller;

import java.security.Principal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.objenesis.instantiator.basic.NewInstanceInstantiator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.app.entity.Guest;
import com.app.entity.Host;
import com.app.entity.HostStatus;
import com.app.entity.Property;
import com.app.helper.Message;
import com.app.repository.HostRepository;
import com.app.repository.PropertyRepository;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/host")
public class HostController {

	@Autowired
	private HostRepository hostRepository;

	@Autowired
	private PropertyRepository propertyRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@ModelAttribute // this method will get called for each below methods
	public void addCommonDataToModel(Model model, Principal principal) {
		String userName = principal.getName();

		// get user by user email
		Host host = hostRepository.findByEmail(userName).get();

		model.addAttribute("currentHost", host);
	}

	@GetMapping("/index")
	public String hostDashboard(Model model) {

		model.addAttribute("title", "Host-Home");

		return "host/host_dashboard";
	}

	@GetMapping("/show-properties/{page}")
	public String showProperties(@PathVariable("page") int page, Principal principal, Model model) {
		System.out.println("pawan");
		// get current host by host name
		Host host = hostRepository.findByEmail(principal.getName()).get();

		Pageable pageable = PageRequest.of(page, 5);
		Page<Property> properties = propertyRepository.findPropertyByHost(host.getHostId(), pageable);

		// set property info to model attribute
		model.addAttribute("properties", properties);
		model.addAttribute("currentPage", page);
		model.addAttribute("totalPages", properties.getTotalPages());

		model.addAttribute("title", "Host-Show-Properties");

		return "host/show_properties";
	}

	@GetMapping("/add-property")
	public String addProperty(Model model) {

		model.addAttribute("property", new Property());
		model.addAttribute("title", "Host-Add-Property");
		return "host/add_property_form";
	}

	@PostMapping("/process-property")
	public String processPorperty(@ModelAttribute("property") Property property, Principal principal,
			HttpSession session) {

		Host host = hostRepository.findByEmail(principal.getName()).get();

		property.setPropertyType(host.getPropertyType());
		property.setHostedBy(host.getName());
		property.setHost(host);
		host.getProperties().add(property);

		hostRepository.save(host);

		session.setAttribute("message", new Message("Your property is added", "success"));
		return "redirect:/host/add-property";
	}

	@GetMapping("/property/{propertyId}")
	public String viewMyProperty(@PathVariable("propertyId") long propertyId, @RequestParam("currentPage") int currentPage, Principal principal, HttpSession session, Model model) {

		// Get the current host
		Host host = hostRepository.findByEmail(principal.getName()).get();

		// check for valid propertyId
		Optional<Property> optProperty = propertyRepository.findById(propertyId);
		if (optProperty.isEmpty()) {

			session.setAttribute("message", new Message("Invalid property id !", "danger"));
			return "redirect:/host/show-properties/" + currentPage;
		}

		// check if property belongs to current host
		if (optProperty.get().getHost().getHostId() != host.getHostId()) {

			session.setAttribute("message", new Message("Invalid property id !", "danger"));
			return "redirect:/host/show-properties/" + currentPage;
		}
		
		
		model.addAttribute("property", optProperty.get());
		
		return "host/host_view_property";
	}

	@GetMapping("/profile")
	public String profile(Model model, Principal principal) {

		Host host = hostRepository.findByEmail(principal.getName()).get();
		model.addAttribute("host", new Host());
		model.addAttribute("currentHost", host);

		model.addAttribute("title", "Host-Profile");
		return "host/profile";
	}

	@PostMapping("/profile-update")
	public String hostProfileUpdate(@ModelAttribute("host") Host host, Principal principal) {

		// get the current host
		Host currentHost = hostRepository.findByEmail(principal.getName()).get();

		// update the updated value
		if (host.getName() != null)
			currentHost.setName(host.getName());
		if (host.getEmail() != null)
			currentHost.setEmail(host.getEmail());
		if (host.getHostStatus() != null)
			currentHost.setHostStatus(host.getHostStatus());
		if (host.getLocation() != null)
			currentHost.setLocation(host.getLocation());
		if (host.getPropertyType() != null)
			currentHost.setPropertyType(host.getPropertyType());
		if (host.getAbout() != null)
			currentHost.setAbout(host.getAbout());
		if (host.getHostingSince() != null)
			currentHost.setHostingSince(host.getHostingSince());

		hostRepository.save(currentHost);

		return "redirect:/host/profile";
	}

	@GetMapping("/setting")
	public String setting(Model model) {

		model.addAttribute("title", "Host-Setting");
		return "host/setting";
	}

	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword") String oldPassword,
			@RequestParam("newPassword") String newPassword, Principal principal, HttpSession session) {

		// get current user
		String userName = principal.getName();
		Host currentHost = hostRepository.findByEmail(userName).get();

		if (newPassword.length() < 6) {

			session.setAttribute("message", new Message("Password must have atleast 6 characters...", "danger"));
			return "redirect:/host/setting";
		}

		else if (passwordEncoder.matches(oldPassword, currentHost.getPassword())) {

			// change the password
			currentHost.setPassword(passwordEncoder.encode(newPassword));
			hostRepository.save(currentHost);
			session.setAttribute("message", new Message("Your password changed successfully...", "success"));

		} else {

			// error
			session.setAttribute("message", new Message("Enter valid old password...", "danger"));
			return "redirect:/host/setting";
		}

//		System.out.println("OldPassword : "+oldPassword);
//		System.out.println("NewPassword : "+newPassword);
		return "redirect:/host/setting";
	}

	@GetMapping("/update-property/{propertyId}")
	public String updateProperty(@PathVariable("propertyId") Long propertyId,
			@RequestParam("currentPage") int currentPage, Model model, HttpSession session, Principal principal) {

		// Get the current host
		Host host = hostRepository.findByEmail(principal.getName()).get();

		// check for valid propertyId
		Optional<Property> optProperty = propertyRepository.findById(propertyId);
		if (optProperty.isEmpty()) {

			session.setAttribute("message", new Message("don't modify the url !!!", "danger"));
			return "redirect:/host/show-properties/" + currentPage;
		}

		// check if property belongs to current host
		System.out.println(optProperty.get().getHost().getHostId() + " " + host.getHostId());
		if (optProperty.get().getHost().getHostId() != host.getHostId()) {

			session.setAttribute("message", new Message("don't modify the url !!!", "danger"));
			return "redirect:/host/show-properties/" + currentPage;
		}

		session.setAttribute("propertyId", propertyId);
		session.setAttribute("currentPage", currentPage);
		model.addAttribute("property", optProperty.get());
		return "host/update_property";
	}

	@PostMapping("/process-update-property")
	public String processUpdateProperty(@ModelAttribute("property") Property property, HttpSession session) {

		// get current propertyId and currentPage from session
		long pId = (long) session.getAttribute("propertyId");
		session.removeAttribute("propertyId");
		int currentPage = (int) session.getAttribute("currentPage");
		session.removeAttribute("currentPage");

		// get the current Property
		Property currentProperty = propertyRepository.findById(pId).get();

		// update the update value
		if (property.getImgLink() != null)
			currentProperty.setImgLink(property.getImgLink());
		if (property.getLocation() != null)
			currentProperty.setLocation(property.getLocation());
		if (property.getPropertyName() != null)
			currentProperty.setPropertyName(property.getPropertyName());
		if (property.getOneNightPrice() != null)
			currentProperty.setOneNightPrice(property.getOneNightPrice());
		if (property.getOffers() != null)
			currentProperty.setOffers(property.getOffers());

		propertyRepository.save(currentProperty);

		session.setAttribute("message", new Message("Property updated successfully..", "success"));

		return "redirect:/host/update-property/" + pId + "?currentPage=" + currentPage;
	}

	@GetMapping("/delete-property/{propertyId}")
	public String deleteProperty(@PathVariable("propertyId") long propertyId,
			@RequestParam("currentPage") int currentPage, HttpSession session, Principal principal) {

		// Get the current host
		Host host = hostRepository.findByEmail(principal.getName()).get();

		// check if propertyId exist

		Optional<Property> optProperty = propertyRepository.findById(propertyId);

		if (optProperty.isEmpty()) {

			session.setAttribute("message", new Message("Wrong propertyId !", "danger"));
			return "redirect:/host/show-properties/" + currentPage;

		}

		// check if property belongs to current host
		if (optProperty.get().getHost().getHostId() != host.getHostId()) {

			session.setAttribute("message", new Message("Wrong propertyId !", "danger"));
			return "redirect:/host/show-properties/" + currentPage;
		}

		// remove property
		host.getProperties().remove(optProperty.get());
		hostRepository.save(host);

		session.setAttribute("message", new Message("Property deleted successfully..", "success"));
		return "redirect:/host/show-properties/" + currentPage;
	}
}
