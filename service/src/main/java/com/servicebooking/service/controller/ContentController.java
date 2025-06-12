package com.servicebooking.service.controller;

import com.servicebooking.service.model.Booking;
import com.servicebooking.service.model.Services;
import com.servicebooking.service.service.BookingService;
import com.servicebooking.service.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
public class ContentController {

    @Autowired
    private ServiceService serviceService;

    @Autowired
    private BookingService bookingService;

    // Home Page
    @GetMapping({"/", "/home"})
    public String home(@AuthenticationPrincipal OAuth2User oauth2User, Model model) {
        model.addAttribute("username", oauth2User != null ? oauth2User.getAttribute("email") : "Guest");
        return "index";
    }

    // Show All Services
    @GetMapping("/services")
    public String getAllServices(Model model, @AuthenticationPrincipal OAuth2User oauth2User) {
        model.addAttribute("services", serviceService.getAllServices());
        model.addAttribute("username", oauth2User != null ? oauth2User.getAttribute("email") : "Guest");
        return "services";
    }

    // Show Service Details
    @GetMapping("/services/{id}")
    public String getServiceDetails(@PathVariable Long id, Model model, @AuthenticationPrincipal OAuth2User oauth2User) {
        serviceService.getServiceById(id).ifPresent(service -> model.addAttribute("service", service));
        model.addAttribute("username", oauth2User != null ? oauth2User.getAttribute("email") : "Guest");
        return "service-details";
    }

    // Booking Form (GET) — protected by Spring Security
    @GetMapping("/book/{serviceId}")
    public String showBookingForm(@PathVariable Long serviceId,
                                  @AuthenticationPrincipal OAuth2User oauth2User,
                                  Model model) {

        serviceService.getServiceById(serviceId).ifPresent(service -> {
            model.addAttribute("service", service);
            model.addAttribute("booking", new Booking());
            model.addAttribute("username", oauth2User != null ? oauth2User.getAttribute("email") : "Guest");
        });

        if (!model.containsAttribute("service")) {
            model.addAttribute("error", "Service not found.");
            return "redirect:/services";
        }

        return "booking-form";
    }



    // Booking Form (POST)
    @PostMapping("/book/{serviceId}")
    public String bookService(
            @PathVariable Long serviceId,
            @ModelAttribute Booking booking,
            @RequestParam("appointmentDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate appointmentDate,
            @RequestParam("appointmentTime") String appointmentTime,
            @AuthenticationPrincipal OAuth2User oauth2User,
            RedirectAttributes redirectAttributes) {

        String[] timeParts = appointmentTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1]);

        LocalDateTime appointmentDateTime = LocalDateTime.of(
                appointmentDate.getYear(),
                appointmentDate.getMonth(),
                appointmentDate.getDayOfMonth(),
                hour,
                minute
        );

        if (appointmentDateTime.isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "Appointment date/time must be in the future.");
            return "redirect:/book/" + serviceId;
        }

        booking.setAppointmentDateTime(appointmentDateTime);
        Booking savedBooking = bookingService.createBooking(booking, serviceId);

        if (savedBooking != null) {
            redirectAttributes.addFlashAttribute("success", "Booking successful!");
            return "redirect:/booking-confirmation/" + savedBooking.getId();
        } else {
            redirectAttributes.addFlashAttribute("error", "Booking failed. Please try again.");
            return "redirect:/book/" + serviceId;
        }
    }

    // Confirmation Page
    @GetMapping("/booking-confirmation/{id}")
    public String showBookingConfirmation(@PathVariable Long id, Model model) {
        bookingService.getBookingById(id).ifPresent(booking -> model.addAttribute("booking", booking));
        return "booking-confirmation";
    }

    @GetMapping("/service/{id}")
    public String getServiceDetails(@PathVariable Long id, Model model) {
        Services services = serviceService.findServiceById(id); // Assume serviceService fetches service details from database
        model.addAttribute("service", services);
        return "service-details"; // Thymeleaf template
    }
}
