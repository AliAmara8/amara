package com.ali.amara.equipment.service;

import com.ali.amara.equipment.entity.*;
import com.ali.amara.equipment.enums.EquipmentStatus;
import com.ali.amara.equipment.enums.EquipmentType;
import com.ali.amara.equipment.enums.RentalStatus;
import com.ali.amara.equipment.repository.EquipmentRepository;
import com.ali.amara.equipment.repository.EquipmentRentalRepository;
import com.ali.amara.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentRentalRepository rentalRepository;

    @Transactional
    public Equipment addEquipment(Equipment equipment) {
        equipment.calculateDepreciation();
        equipment.scheduleNextMaintenance();
        return equipmentRepository.save(equipment);
    }

    @Transactional
    public void recordMaintenance(Long equipmentId, MaintenanceRecord maintenance) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        equipment.addMaintenanceRecord(maintenance);
        equipment.setStatus(EquipmentStatus.OPERATIONAL);
        equipmentRepository.save(equipment);
    }

    public List<Equipment> findEquipmentNeedingMaintenance() {
        return equipmentRepository.findEquipmentNeedingMaintenance();
    }

    @Transactional
    public EquipmentRental rentEquipment(Long equipmentId, User renter, LocalDate startDate, LocalDate endDate) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));

        if (!equipment.isAvailable()) {
            throw new RuntimeException("Equipment is not available for rent");
        }

        List<EquipmentRental> overlappingRentals = rentalRepository.findOverlappingRentals(
            equipmentId, startDate, endDate);
        if (!overlappingRentals.isEmpty()) {
            throw new RuntimeException("Equipment is already rented for this period");
        }

        EquipmentRental rental = EquipmentRental.builder()
                .equipment(equipment)
                .renter(renter)
                .startDate(startDate)
                .endDate(endDate)
                .dailyRate(equipment.getRentalRate())
                .status(RentalStatus.PENDING)
                .build();

        return rentalRepository.save(rental);
    }

    @Transactional
    public void approveRental(Long rentalId) {
        EquipmentRental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found"));

        rental.setStatus(RentalStatus.APPROVED);
        rental.getEquipment().setAvailable(false);
        rentalRepository.save(rental);
    }

    @Transactional
    public void completeRental(Long rentalId, Integer hoursUsed) {
        EquipmentRental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RuntimeException("Rental not found"));

        rental.setStatus(RentalStatus.COMPLETED);
        rental.setHoursUsed(hoursUsed);

        Equipment equipment = rental.getEquipment();
        equipment.setHoursOfUse(equipment.getHoursOfUse() + hoursUsed);
        equipment.setAvailable(true);

        rentalRepository.save(rental);
    }

    public double calculateEquipmentValue(Long equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found"));
        return equipment.calculateDepreciation();
    }

    public List<Equipment> findAvailableEquipmentForRent(LocalDate startDate, LocalDate endDate) {
        return equipmentRepository.findAvailableOperationalEquipment().stream()
                .filter(e -> rentalRepository.findOverlappingRentals(e.getId(), startDate, endDate).isEmpty())
                .toList();
    }

    public List<EquipmentRental> getUpcomingRentals(Long equipmentId) {
        return rentalRepository.findUpcomingRentals(equipmentId);
    }

    public List<EquipmentRental> getPendingRentalsForOwner(Long ownerId) {
        return rentalRepository.findByStatusAndEquipmentOwnerId(RentalStatus.PENDING, ownerId);
    }

    public List<Equipment> findAvailableByType(String type) {
        try {
            EquipmentType equipmentType = EquipmentType.valueOf(type.toUpperCase());
            return equipmentRepository.findAvailableByType(equipmentType);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid equipment type: " + type);
        }
    }

    public Equipment findById(Long equipmentId) {
        return equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new RuntimeException("Equipment not found with id: " + equipmentId));
    }
}
