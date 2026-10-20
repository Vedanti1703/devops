package com.telecom.tower.service;

import com.telecom.tower.model.MaintenanceRecord;
import com.telecom.tower.model.RecordPriority;
import com.telecom.tower.model.RecordStatus;
import com.telecom.tower.repository.MaintenanceRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class MaintenanceRecordService {

    private final MaintenanceRecordRepository repository;

    public MaintenanceRecordService(MaintenanceRecordRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<MaintenanceRecord> getAllRecords() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<MaintenanceRecord> getRecordById(Long id) {
        return repository.findById(id);
    }

    public MaintenanceRecord createRecord(MaintenanceRecord record, String username) {
        if (record.getStatus() == null) {
            record.setStatus(RecordStatus.OPEN);
        }
        record.setCreatedBy(username != null ? username : "anonymous");
        record.setCreatedAt(LocalDateTime.now());
        record.setUpdatedAt(LocalDateTime.now());
        return repository.save(record);
    }

    public MaintenanceRecord updateRecord(Long id, MaintenanceRecord updated) {
        MaintenanceRecord existing = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record not found with ID: " + id));

        existing.setTowerId(updated.getTowerId());
        existing.setLocation(updated.getLocation());
        existing.setIssueType(updated.getIssueType());
        existing.setDescription(updated.getDescription());
        existing.setPriority(updated.getPriority());
        existing.setAssignedTechnician(updated.getAssignedTechnician());
        existing.setUpdatedAt(LocalDateTime.now());

        return repository.save(existing);
    }

    public MaintenanceRecord transitionStatus(Long id, RecordStatus targetStatus, String role, String username) {
        MaintenanceRecord record = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Record not found with ID: " + id));

        RecordStatus currentStatus = record.getStatus();

        if (currentStatus == targetStatus) {
            return record;
        }

        validateTransition(currentStatus, targetStatus, role);

        record.setStatus(targetStatus);
        record.setUpdatedAt(LocalDateTime.now());
        return repository.save(record);
    }

    public void validateTransition(RecordStatus currentStatus, RecordStatus targetStatus, String role) {
        boolean isAdmin = role != null && (role.contains("ADMIN") || role.equals("ROLE_ADMIN"));
        boolean isSupervisor = role != null && (role.contains("SUPERVISOR") || role.equals("ROLE_SUPERVISOR"));
        boolean isTechnician = role != null && (role.contains("TECHNICIAN") || role.equals("ROLE_TECHNICIAN"));

        if (isAdmin) {
            // Admin can execute valid business transitions including VERIFIED -> CLOSED
            if (currentStatus == RecordStatus.VERIFIED && targetStatus == RecordStatus.CLOSED) {
                return;
            }
            // Admin can also approve supervisor or tech actions if needed
            if (currentStatus == RecordStatus.OPEN && targetStatus == RecordStatus.IN_PROGRESS) return;
            if (currentStatus == RecordStatus.IN_PROGRESS && targetStatus == RecordStatus.COMPLETED) return;
            if (currentStatus == RecordStatus.COMPLETED && targetStatus == RecordStatus.VERIFIED) return;
            if (currentStatus == RecordStatus.COMPLETED && targetStatus == RecordStatus.IN_PROGRESS) return;
            if (targetStatus == RecordStatus.CLOSED) return;

            throw new IllegalStateException("Admin transition from " + currentStatus + " to " + targetStatus + " is not permitted.");
        }

        if (isTechnician) {
            if (currentStatus == RecordStatus.OPEN && targetStatus == RecordStatus.IN_PROGRESS) {
                return;
            }
            if (currentStatus == RecordStatus.IN_PROGRESS && targetStatus == RecordStatus.COMPLETED) {
                return;
            }
            throw new IllegalStateException("Technicians can only advance OPEN -> IN_PROGRESS -> COMPLETED. Cannot transition from " 
                    + currentStatus + " to " + targetStatus);
        }

        if (isSupervisor) {
            if (currentStatus == RecordStatus.COMPLETED && targetStatus == RecordStatus.VERIFIED) {
                return; // Approved
            }
            if (currentStatus == RecordStatus.COMPLETED && targetStatus == RecordStatus.IN_PROGRESS) {
                return; // Rejected back to technician
            }
            throw new IllegalStateException("Supervisors can only approve COMPLETED -> VERIFIED or reject COMPLETED -> IN_PROGRESS. Cannot transition from " 
                    + currentStatus + " to " + targetStatus);
        }

        throw new IllegalStateException("Role " + role + " is not authorized to transition status from " 
                + currentStatus + " to " + targetStatus);
    }

    @Transactional(readOnly = true)
    public List<MaintenanceRecord> searchRecords(String keyword, RecordStatus status, RecordPriority priority) {
        String cleanKeyword = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        return repository.searchRecords(cleanKeyword, status, priority);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        long total = repository.count();
        long openCount = repository.countByStatus(RecordStatus.OPEN);
        long inProgressCount = repository.countByStatus(RecordStatus.IN_PROGRESS);
        long completedCount = repository.countByStatus(RecordStatus.COMPLETED);
        long verifiedCount = repository.countByStatus(RecordStatus.VERIFIED);
        long closedCount = repository.countByStatus(RecordStatus.CLOSED);

        long pending = openCount + inProgressCount;
        long resolved = completedCount + verifiedCount + closedCount;

        stats.put("total", total);
        stats.put("openCount", openCount);
        stats.put("inProgressCount", inProgressCount);
        stats.put("completedCount", completedCount);
        stats.put("verifiedCount", verifiedCount);
        stats.put("closedCount", closedCount);
        stats.put("pending", pending);
        stats.put("resolved", resolved);

        // Priority breakdown
        Map<RecordPriority, Long> priorityCounts = new HashMap<>();
        for (RecordPriority p : RecordPriority.values()) {
            priorityCounts.put(p, repository.countByPriority(p));
        }
        stats.put("priorityCounts", priorityCounts);

        return stats;
    }
}
