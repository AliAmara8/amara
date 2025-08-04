package com.ali.amara.equipment.controller;

import com.ali.amara.equipment.entity.Equipment;
import com.ali.amara.equipment.entity.EquipmentRental;
import com.ali.amara.equipment.entity.MaintenanceRecord;
import com.ali.amara.equipment.service.EquipmentService;
import com.ali.amara.user.entity.User;
import com.ali.amara.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/equipment")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final UserService userService;

    @PostMapping
    public ResponseEntity<Equipment> addEquipment(@RequestBody Equipment equipment) {
        User currentUser = userService.getCurrentUser();
        equipment.setUser(currentUser);
        return ResponseEntity.ok(equipmentService.addEquipment(equipment));
    }

    @PostMapping("/{equipmentId}/maintenance")
    public ResponseEntity<Void> recordMaintenance(
            @PathVariable Long equipmentId,
            @RequestBody MaintenanceRecord maintenance) {
        equipmentService.recordMaintenance(equipmentId, maintenance);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/maintenance-needed")
    public ResponseEntity<List<Equipment>> getEquipmentNeedingMaintenance() {
        return ResponseEntity.ok(equipmentService.findEquipmentNeedingMaintenance());
    }

    @PostMapping("/{equipmentId}/rent")
    public ResponseEntity<EquipmentRental> rentEquipment(
            @PathVariable Long equipmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(equipmentService.rentEquipment(equipmentId, currentUser, startDate, endDate));
    }

    @PostMapping("/rentals/{rentalId}/approve")
    public ResponseEntity<Void> approveRental(@PathVariable Long rentalId) {
        equipmentService.approveRental(rentalId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rentals/{rentalId}/complete")
    public ResponseEntity<Void> completeRental(
            @PathVariable Long rentalId,
            @RequestParam Integer hoursUsed) {
        equipmentService.completeRental(rentalId, hoursUsed);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/available")
    public ResponseEntity<List<Equipment>> getAvailableEquipment(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(equipmentService.findAvailableEquipmentForRent(startDate, endDate));
    }

    @GetMapping("/{equipmentId}/value")
    public ResponseEntity<Double> getEquipmentValue(@PathVariable Long equipmentId) {
        return ResponseEntity.ok(equipmentService.calculateEquipmentValue(equipmentId));
    }

    @GetMapping("/{equipmentId}/upcoming-rentals")
    public ResponseEntity<List<EquipmentRental>> getUpcomingRentals(@PathVariable Long equipmentId) {
        return ResponseEntity.ok(equipmentService.getUpcomingRentals(equipmentId));
    }
}
