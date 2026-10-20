package com.telecom.tower.controller;

import com.telecom.tower.service.MaintenanceRecordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

@Controller
public class DashboardController {

    private final MaintenanceRecordService service;

    public DashboardController(MaintenanceRecordService service) {
        this.service = service;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        Map<String, Object> stats = service.getDashboardStats();
        model.addAttribute("stats", stats);
        return "dashboard";
    }
}
