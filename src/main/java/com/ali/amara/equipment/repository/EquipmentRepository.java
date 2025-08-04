package com.ali.amara.equipment.repository;

import com.ali.amara.equipment.entity.Equipment;
import com.ali.amara.equipment.enums.EquipmentStatus;
import com.ali.amara.equipment.enums.EquipmentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findByIsAvailableTrue();
    List<Equipment> findByUserId(Long userId);

    @Query("SELECT e FROM Equipment e WHERE e.status = 'OPERATIONAL' AND e.isAvailable = true")
    List<Equipment> findAvailableOperationalEquipment();

    @Query("SELECT e FROM Equipment e WHERE e.hoursOfUse >= e.maintenanceInterval OR e.nextMaintenanceDate <= CURRENT_DATE")
    List<Equipment> findEquipmentNeedingMaintenance();

    @Query("SELECT e FROM Equipment e WHERE e.type = :type AND e.status = 'OPERATIONAL' AND e.isAvailable = true")
    List<Equipment> findAvailableByType(EquipmentType type);

    @Query("SELECT e FROM Equipment e WHERE e.status = :status")
    List<Equipment> findByStatus(EquipmentStatus status);

    @Query("SELECT e FROM Equipment e WHERE e.lastMaintenanceDate < :date")
    List<Equipment> findByLastMaintenanceBeforeDate(LocalDate date);
}
