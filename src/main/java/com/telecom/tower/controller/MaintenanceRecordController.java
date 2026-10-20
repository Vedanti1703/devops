package com.telecom.tower.controller;

import com.telecom.tower.model.MaintenanceRecord;
import com.telecom.tower.model.RecordPriority;
import com.telecom.tower.model.RecordStatus;
import com.telecom.tower.service.MaintenanceRecordService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/records")
public class MaintenanceRecordController {

    private final MaintenanceRecordService service;

    public MaintenanceRecordController(MaintenanceRecordService service) {
        this.service = service;
    }

    @GetMapping
    public String listRecords(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "status", required = false) RecordStatus status,
            @RequestParam(value = "priority", required = false) RecordPriority priority,
            Model model) {

        List<MaintenanceRecord> records = service.searchRecords(keyword, status, priority);
        model.addAttribute("records", records);
        model.addAttribute("keyword", keyword);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedPriority", priority);
        model.addAttribute("allStatuses", RecordStatus.values());
        model.addAttribute("allPriorities", RecordPriority.values());

        return "record-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("record")) {
            model.addAttribute("record", new MaintenanceRecord());
        }
        model.addAttribute("allPriorities", RecordPriority.values());
        return "record-create";
    }

    @PostMapping("/save")
    public String saveRecord(
            @Valid @ModelAttribute("record") MaintenanceRecord record,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allPriorities", RecordPriority.values());
            return "record-create";
        }

        String username = authentication != null ? authentication.getName() : "tech1";
        service.createRecord(record, username);
        redirectAttributes.addFlashAttribute("successMessage", "Maintenance record created successfully with ID: " + record.getTowerId());
        return "redirect:/records";
    }

    @GetMapping("/{id}")
    public String viewRecordDetail(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return service.getRecordById(id)
                .map(record -> {
                    model.addAttribute("record", record);
                    return "record-detail";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Record not found with ID: " + id);
                    return "redirect:/records";
                });
    }

    @PostMapping("/{id}/transition")
    public String transitionStatus(
            @PathVariable("id") Long id,
            @RequestParam("targetStatus") RecordStatus targetStatus,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String username = authentication != null ? authentication.getName() : "anonymous";
        String userRole = authentication != null && !authentication.getAuthorities().isEmpty()
                ? authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .findFirst().orElse("ROLE_USER")
                : "ROLE_USER";

        try {
            service.transitionStatus(id, targetStatus, userRole, username);
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Status successfully transitioned to " + targetStatus);
        } catch (IllegalStateException | IllegalArgumentException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }

        return "redirect:/records/" + id;
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes redirectAttributes) {
        return service.getRecordById(id)
                .map(record -> {
                    model.addAttribute("record", record);
                    model.addAttribute("allPriorities", RecordPriority.values());
                    model.addAttribute("allStatuses", RecordStatus.values());
                    return "record-edit";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Record not found with ID: " + id);
                    return "redirect:/records";
                });
    }

    @PostMapping("/{id}/update")
    public String updateRecord(
            @PathVariable("id") Long id,
            @Valid @ModelAttribute("record") MaintenanceRecord record,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("allPriorities", RecordPriority.values());
            model.addAttribute("allStatuses", RecordStatus.values());
            return "record-edit";
        }

        service.updateRecord(id, record);
        redirectAttributes.addFlashAttribute("successMessage", "Record updated successfully.");
        return "redirect:/records/" + id;
    }
}
