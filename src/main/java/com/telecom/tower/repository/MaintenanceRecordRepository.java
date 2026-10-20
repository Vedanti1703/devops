package com.telecom.tower.repository;

import com.telecom.tower.model.MaintenanceRecord;
import com.telecom.tower.model.RecordPriority;
import com.telecom.tower.model.RecordStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRecordRepository extends JpaRepository<MaintenanceRecord, Long> {

    List<MaintenanceRecord> findByTowerIdContainingIgnoreCase(String towerId);

    List<MaintenanceRecord> findByLocationContainingIgnoreCase(String location);

    List<MaintenanceRecord> findByStatus(RecordStatus status);

    List<MaintenanceRecord> findByPriority(RecordPriority priority);

    long countByStatus(RecordStatus status);

    long countByPriority(RecordPriority priority);

    @Query("SELECT r FROM MaintenanceRecord r WHERE " +
           "(:keyword IS NULL OR LOWER(r.towerId) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(r.location) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:priority IS NULL OR r.priority = :priority) " +
           "ORDER BY r.createdAt DESC")
    List<MaintenanceRecord> searchRecords(@Param("keyword") String keyword,
                                          @Param("status") RecordStatus status,
                                          @Param("priority") RecordPriority priority);
}
