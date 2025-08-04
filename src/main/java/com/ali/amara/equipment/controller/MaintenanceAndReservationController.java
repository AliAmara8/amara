package com.ali.amara.equipment.controller;

import com.ali.amara.equipment.entity.*;
import com.ali.amara.equipment.service.EquipmentReservationService;
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
public class MaintenanceAndReservationController {

    private final EquipmentReservationService reservationService;
    private final EquipmentService equipmentService;
    private final UserService userService;

    // Endpoints pour les réservations
    @PostMapping("/{equipmentId}/reserve")
    public ResponseEntity<EquipmentReservation> createReservation(
            @PathVariable Long equipmentId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam Double deposit) {
        User currentUser = userService.getCurrentUser();
        Equipment equipment = equipmentService.findById(equipmentId);
        return ResponseEntity.ok(reservationService.createReservation(
            equipment, currentUser, startDate, endDate, deposit));
    }

    @PostMapping("/reservations/{reservationId}/confirm-deposit")
    public ResponseEntity<Void> confirmDeposit(@PathVariable Long reservationId) {
        reservationService.confirmDeposit(reservationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reservations/{reservationId}/cancel")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable Long reservationId,
            @RequestParam String reason) {
        reservationService.cancelReservation(reservationId, reason);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/reservations/upcoming")
    public ResponseEntity<List<EquipmentReservation>> getMyUpcomingReservations() {
        User currentUser = userService.getCurrentUser();
        return ResponseEntity.ok(reservationService.getUpcomingReservations(currentUser.getId()));
    }

    @GetMapping("/{equipmentId}/reservations/pending")
    public ResponseEntity<List<EquipmentReservation>> getPendingReservations(
            @PathVariable Long equipmentId) {
        return ResponseEntity.ok(reservationService.getPendingReservations(equipmentId));
    }
}
