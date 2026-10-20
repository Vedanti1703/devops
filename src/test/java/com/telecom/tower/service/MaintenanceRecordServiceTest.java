package com.telecom.tower.service;

import com.telecom.tower.model.MaintenanceRecord;
import com.telecom.tower.model.RecordPriority;
import com.telecom.tower.model.RecordStatus;
import com.telecom.tower.repository.MaintenanceRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MaintenanceRecordServiceTest {

    @Mock
    private MaintenanceRecordRepository repository;

    @InjectMocks
    private MaintenanceRecordService service;

    private MaintenanceRecord sampleRecord;

    @BeforeEach
    void setUp() {
        sampleRecord = new MaintenanceRecord(
                "TWR-DEL-101",
                "Sector 62, Noida",
                "POWER_FAILURE",
                "Diesel generator fuel pump defect",
                RecordPriority.CRITICAL,
                "tech1",
                "tech1"
        );
        sampleRecord.setId(1L);
        sampleRecord.setStatus(RecordStatus.OPEN);
    }

    @Test
    @DisplayName("Should create maintenance record with default OPEN status")
    void testCreateRecord() {
        when(repository.save(any(MaintenanceRecord.class))).thenReturn(sampleRecord);

        MaintenanceRecord saved = service.createRecord(sampleRecord, "tech1");

        assertNotNull(saved);
        assertEquals(RecordStatus.OPEN, saved.getStatus());
        assertEquals("tech1", saved.getCreatedBy());
        verify(repository, times(1)).save(sampleRecord);
    }

    @Test
    @DisplayName("Technician transition: OPEN -> IN_PROGRESS should succeed")
    void testTechnicianStartWorkSuccess() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleRecord));
        when(repository.save(any(MaintenanceRecord.class))).thenAnswer(i -> i.getArgument(0));

        MaintenanceRecord updated = service.transitionStatus(1L, RecordStatus.IN_PROGRESS, "ROLE_TECHNICIAN", "tech1");

        assertEquals(RecordStatus.IN_PROGRESS, updated.getStatus());
    }

    @Test
    @DisplayName("Technician transition: IN_PROGRESS -> COMPLETED should succeed")
    void testTechnicianCompleteWorkSuccess() {
        sampleRecord.setStatus(RecordStatus.IN_PROGRESS);
        when(repository.findById(1L)).thenReturn(Optional.of(sampleRecord));
        when(repository.save(any(MaintenanceRecord.class))).thenAnswer(i -> i.getArgument(0));

        MaintenanceRecord updated = service.transitionStatus(1L, RecordStatus.COMPLETED, "ROLE_TECHNICIAN", "tech1");

        assertEquals(RecordStatus.COMPLETED, updated.getStatus());
    }

    @Test
    @DisplayName("Technician transition: OPEN -> VERIFIED should fail with IllegalStateException")
    void testTechnicianDirectVerifyFail() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleRecord));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                service.transitionStatus(1L, RecordStatus.VERIFIED, "ROLE_TECHNICIAN", "tech1"));

        assertTrue(ex.getMessage().contains("Technicians can only advance OPEN -> IN_PROGRESS -> COMPLETED"));
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Supervisor transition: COMPLETED -> VERIFIED should succeed")
    void testSupervisorApproveSuccess() {
        sampleRecord.setStatus(RecordStatus.COMPLETED);
        when(repository.findById(1L)).thenReturn(Optional.of(sampleRecord));
        when(repository.save(any(MaintenanceRecord.class))).thenAnswer(i -> i.getArgument(0));

        MaintenanceRecord updated = service.transitionStatus(1L, RecordStatus.VERIFIED, "ROLE_SUPERVISOR", "sup1");

        assertEquals(RecordStatus.VERIFIED, updated.getStatus());
    }

    @Test
    @DisplayName("Supervisor transition: COMPLETED -> IN_PROGRESS (Reject) should succeed")
    void testSupervisorRejectSuccess() {
        sampleRecord.setStatus(RecordStatus.COMPLETED);
        when(repository.findById(1L)).thenReturn(Optional.of(sampleRecord));
        when(repository.save(any(MaintenanceRecord.class))).thenAnswer(i -> i.getArgument(0));

        MaintenanceRecord updated = service.transitionStatus(1L, RecordStatus.IN_PROGRESS, "ROLE_SUPERVISOR", "sup1");

        assertEquals(RecordStatus.IN_PROGRESS, updated.getStatus());
    }

    @Test
    @DisplayName("Supervisor transition: OPEN -> VERIFIED should fail")
    void testSupervisorApproveOpenFail() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleRecord));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () ->
                service.transitionStatus(1L, RecordStatus.VERIFIED, "ROLE_SUPERVISOR", "sup1"));

        assertTrue(ex.getMessage().contains("Supervisors can only approve COMPLETED -> VERIFIED"));
    }

    @Test
    @DisplayName("Admin transition: VERIFIED -> CLOSED should succeed")
    void testAdminCloseSuccess() {
        sampleRecord.setStatus(RecordStatus.VERIFIED);
        when(repository.findById(1L)).thenReturn(Optional.of(sampleRecord));
        when(repository.save(any(MaintenanceRecord.class))).thenAnswer(i -> i.getArgument(0));

        MaintenanceRecord updated = service.transitionStatus(1L, RecordStatus.CLOSED, "ROLE_ADMIN", "admin");

        assertEquals(RecordStatus.CLOSED, updated.getStatus());
    }

    @Test
    @DisplayName("Dashboard stats should aggregate counts accurately")
    void testDashboardStats() {
        when(repository.count()).thenReturn(10L);
        when(repository.countByStatus(RecordStatus.OPEN)).thenReturn(4L);
        when(repository.countByStatus(RecordStatus.IN_PROGRESS)).thenReturn(2L);
        when(repository.countByStatus(RecordStatus.COMPLETED)).thenReturn(2L);
        when(repository.countByStatus(RecordStatus.VERIFIED)).thenReturn(1L);
        when(repository.countByStatus(RecordStatus.CLOSED)).thenReturn(1L);

        Map<String, Object> stats = service.getDashboardStats();

        assertEquals(10L, stats.get("total"));
        assertEquals(6L, stats.get("pending")); // 4 + 2
        assertEquals(4L, stats.get("resolved")); // 2 + 1 + 1
    }
}
