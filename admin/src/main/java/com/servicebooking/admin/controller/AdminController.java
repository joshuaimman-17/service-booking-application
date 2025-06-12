package com.servicebooking.admin.controller;

import com.servicebooking.admin.model.Provider;
import com.servicebooking.admin.model.Services;
import com.servicebooking.admin.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("services", adminService.getAllServices());
        return "admin-dashboard";
    }

    // Add Service Form
    @GetMapping("/add-service")
    public String addServiceForm(Model model) {
        model.addAttribute("service", new Services());
        model.addAttribute("providers", adminService.getAllProviders());
        return "service-form";
    }

    // Save Service
    @PostMapping("/add-service")
    public String saveService(@ModelAttribute Services service, RedirectAttributes redirectAttributes) {
        adminService.addService(service);
        redirectAttributes.addFlashAttribute("success", "Service added successfully!");
        return "redirect:/admin/dashboard";  // Correct redirect after adding service
    }

    // Edit Service Form
    @GetMapping("/edit-service/{id}")
    public String editServiceForm(@PathVariable Long id, Model model) {
        Services service = adminService.getServiceById(id);
        model.addAttribute("service", service);
        model.addAttribute("providers", adminService.getAllProviders());
        return "service-form";
    }

    // Update Service
    @PostMapping("/update-service/{id}")
    public String updateService(@PathVariable Long id, @ModelAttribute Services service, RedirectAttributes redirectAttributes) {
        adminService.updateService(id, service);
        redirectAttributes.addFlashAttribute("success", "Service updated successfully!");
        return "redirect:/admin/dashboard";
    }

    // Show Providers
    @GetMapping("/providers")
    public String showProviders(Model model) {
        model.addAttribute("providers", adminService.getAllProviders());
        return "providers";
    }

    // Add Provider Form
    @GetMapping("/add-provider")
    public String addProviderForm(Model model) {
        model.addAttribute("provider", new Provider());
        return "provider-form";
    }

    // Save Provider
    @PostMapping("/add-provider")
    public String saveProvider(@ModelAttribute Provider provider, RedirectAttributes redirectAttributes) {
        adminService.addProvider(provider);
        redirectAttributes.addFlashAttribute("success", "Provider added successfully!");
        return "redirect:/admin/providers";  // Correct redirect after adding provider
    }

    // Edit Provider Form
    @GetMapping("/edit-provider/{id}")
    public String editProviderForm(@PathVariable Long id, Model model) {
        model.addAttribute("provider", adminService.getProviderById(id));
        return "provider-form";
    }

    // Update Provider
    @PostMapping("/update-provider/{id}")
    public String updateProvider(@PathVariable Long id, @ModelAttribute Provider provider, RedirectAttributes redirectAttributes) {
        adminService.updateProvider(id, provider);
        redirectAttributes.addFlashAttribute("success", "Provider updated successfully!");
        return "redirect:/admin/providers";
    }

    // Delete Provider
    @GetMapping("/delete-provider/{id}")
    public String deleteProvider(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteProvider(id);
            redirectAttributes.addFlashAttribute("success", "Provider deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting provider: " + e.getMessage());
        }
        return "redirect:/admin/providers";
    }

    // Delete Service
    @GetMapping("/delete-service/{id}")
    public String deleteService(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        adminService.deleteService(id);
        redirectAttributes.addFlashAttribute("success", "Service deleted successfully!");
        return "redirect:/admin/dashboard";
    }
    // View Bookings
    @GetMapping("/admin-booking")
    public String viewBookings(Model model) {
        model.addAttribute("bookings", adminService.getAllBookings());
        return "admin-booking";
    }

}