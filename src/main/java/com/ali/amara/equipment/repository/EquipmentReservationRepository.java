package com.ali.amara.equipment.repository;

import com.ali.amara.equipment.entity.EquipmentReservation;
import com.ali.amara.equipment.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EquipmentReservationRepository extends JpaRepository<EquipmentReservation, Long> {
    List<EquipmentReservation> findByUserId(Long userId);
    List<EquipmentReservation> findByEquipmentId(Long equipmentId);
    List<EquipmentReservation> findByStatus(ReservationStatus status);

    @Query("SELECT r FROM EquipmentReservation r WHERE r.equipment.id = :equipmentId " +
           "AND ((r.startDate BETWEEN :start AND :end) OR (r.endDate BETWEEN :start AND :end))")
    List<EquipmentReservation> findOverlappingReservations(Long equipmentId, LocalDate start, LocalDate end);

    @Query("SELECT r FROM EquipmentReservation r WHERE r.status = 'CONFIRMED' " +
           "AND r.startDate <= CURRENT_DATE AND r.endDate >= CURRENT_DATE")
    List<EquipmentReservation> findActiveReservations();

    @Query("SELECT r FROM EquipmentReservation r WHERE r.status = 'CONFIRMED' " +
           "AND r.endDate < CURRENT_DATE AND r.status != 'COMPLETED'")
    List<EquipmentReservation> findOverdueReservations();

    @Query("SELECT r FROM EquipmentReservation r WHERE r.user.id = :userId " +
           "AND r.startDate >= :startDate AND r.endDate <= :endDate")
    List<EquipmentReservation> findUserReservationsBetweenDates(Long userId, LocalDate startDate, LocalDate endDate);

    @Query("SELECT r FROM EquipmentReservation r WHERE r.status = 'DEPOSIT_PENDING' " +
           "AND r.startDate < :expiryTime")
    List<EquipmentReservation> findExpiredDepositPendingReservations(LocalDateTime expiryTime);

    @Query("SELECT r FROM EquipmentReservation r WHERE r.status = 'CONFIRMED' " +
           "AND r.startDate = :startDate")
    List<EquipmentReservation> findStartingReservations(LocalDate startDate);

    @Query("SELECT r FROM EquipmentReservation r WHERE r.equipment.user.id = :ownerId " +
           "AND r.startDate >= :fromDate AND r.status = 'CONFIRMED'")
    List<EquipmentReservation> findUpcomingReservations(Long ownerId, LocalDate fromDate);

    @Query("SELECT r FROM EquipmentReservation r WHERE r.equipment.user.id = :ownerId " +
           "AND r.status = 'DEPOSIT_PENDING'")
    List<EquipmentReservation> findPendingReservations(Long ownerId);
}
