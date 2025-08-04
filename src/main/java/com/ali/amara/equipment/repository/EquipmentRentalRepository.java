package com.ali.amara.equipment.repository;

import com.ali.amara.equipment.entity.EquipmentRental;
import com.ali.amara.equipment.enums.RentalStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EquipmentRentalRepository extends JpaRepository<EquipmentRental, Long> {
    List<EquipmentRental> findByRenterId(Long renterId);
    List<EquipmentRental> findByEquipmentId(Long equipmentId);
    List<EquipmentRental> findByStatus(RentalStatus status);

    @Query("SELECT r FROM EquipmentRental r WHERE r.endDate < CURRENT_DATE AND r.status = 'IN_PROGRESS'")
    List<EquipmentRental> findOverdueRentals();

    @Query("SELECT r FROM EquipmentRental r WHERE r.equipment.id = :equipmentId AND r.status = 'IN_PROGRESS'")
    List<EquipmentRental> findActiveRentalsForEquipment(Long equipmentId);

    @Query("SELECT r FROM EquipmentRental r WHERE r.renter.id = :userId AND r.startDate >= :startDate AND r.endDate <= :endDate")
    List<EquipmentRental> findUserRentalsBetweenDates(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM EquipmentRental r WHERE r.equipment.id = :equipmentId " +
           "AND ((r.startDate BETWEEN :startDate AND :endDate) OR " +
           "(r.endDate BETWEEN :startDate AND :endDate) OR " +
           "(r.startDate <= :startDate AND r.endDate >= :endDate))")
    List<EquipmentRental> findOverlappingRentals(Long equipmentId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM EquipmentRental r WHERE r.equipment.user.id = :ownerId " +
           "AND r.startDate > CURRENT_DATE")
    List<EquipmentRental> findUpcomingRentals(Long ownerId);

    @Query("SELECT r FROM EquipmentRental r WHERE r.status = :status AND r.equipment.user.id = :ownerId")
    List<EquipmentRental> findByStatusAndEquipmentOwnerId(RentalStatus status, Long ownerId);
}
