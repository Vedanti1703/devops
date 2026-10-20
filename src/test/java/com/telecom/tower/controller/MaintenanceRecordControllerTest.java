package com.telecom.tower.controller;

import com.telecom.tower.model.MaintenanceRecord;
import com.telecom.tower.model.RecordPriority;
import com.telecom.tower.model.RecordStatus;
import com.telecom.tower.repository.MaintenanceRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class MaintenanceRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private MaintenanceRecordRepository repository;

    private Long savedRecordId;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        MaintenanceRecord rec = new MaintenanceRecord(
                "TWR-DEL-101", "Sector 62, Noida", "POWER_FAILURE",
                "Generator malfunction", RecordPriority.HIGH, "tech1", "tech1");
        rec = repository.save(rec);
        savedRecordId = rec.getId();
    }

    @Test
    @DisplayName("Unauthenticated user accessing /records should be redirected to /login")
    void testUnauthenticatedRedirect() throws Exception {
        mockMvc.perform(get("/records"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    @WithMockUser(username = "tech1", roles = {"TECHNICIAN"})
    @DisplayName("Authenticated technician can access /records list view")
    void testAuthenticatedRecordsList() throws Exception {
        mockMvc.perform(get("/records"))
                .andExpect(status().isOk())
                .andExpect(view().name("record-list"))
                .andExpect(model().attributeExists("records"));
    }

    @Test
    @WithMockUser(username = "tech1", roles = {"TECHNICIAN"})
    @DisplayName("Authenticated user can view existing record details")
    void testViewRecordDetails() throws Exception {
        mockMvc.perform(get("/records/" + savedRecordId))
                .andExpect(status().isOk())
                .andExpect(view().name("record-detail"))
                .andExpect(model().attributeExists("record"));
    }

    @Test
    @WithMockUser(username = "tech1", roles = {"TECHNICIAN"})
    @DisplayName("Authenticated user can view dashboard")
    void testDashboardView() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("dashboard"))
                .andExpect(model().attributeExists("stats"));
    }
}
