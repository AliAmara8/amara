package com.ali.amara.equipment.service;

import com.ali.amara.equipment.entity.*;
import com.ali.amara.equipment.enums.EquipmentStatus;
import com.ali.amara.equipment.enums.ReservationStatus;
import com.ali.amara.equipment.repository.EquipmentReservationRepository;
import com.ali.amara.notification.service.NotificationService;
import com.ali.amara.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EquipmentReservationService {

    private final EquipmentReservationRepository reservationRepository;
    private final NotificationService notificationService;

    @Transactional
    public EquipmentReservation createReservation(Equipment equipment, User user,
            LocalDate startDate, LocalDate endDate, Double deposit) {

        // Vérifier la disponibilité
        if (!isEquipmentAvailable(equipment.getId(), startDate, endDate)) {
            throw new RuntimeException("Equipment not available for these dates");
        }

        EquipmentReservation reservation = EquipmentReservation.builder()
                .equipment(equipment)
                .user(user)
                .startDate(startDate)
                .endDate(endDate)
                .status(ReservationStatus.DEPOSIT_PENDING)

                .deposit(deposit)  // Utilisation directe du champ deposit
                .build();

        reservation = reservationRepository.save(reservation);

        // Envoyer notification au propriétaire
        notificationService.sendReservationRequestNotification(
            equipment.getUser().getId(),     // ID du propriétaire
            user.getId(),                    // ID du demandeur
            equipment.getName(),             // Nom de l'équipement
            startDate.atStartOfDay(),        // Date de début
            endDate.atStartOfDay(),          // Date de fin
            equipment.getId()                // ID de l'équipement
        );

        return reservation;
    }

    @Transactional
    public void confirmDeposit(Long reservationId) {
        EquipmentReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(ReservationStatus.CONFIRMED);

        // Mettre à jour le statut de l'équipement
        Equipment equipment = reservation.getEquipment();
        equipment.setStatus(EquipmentStatus.RESERVED);

        reservationRepository.save(reservation);
    }

    @Scheduled(cron = "0 0 */1 * * *") // Vérifier toutes les heures
    public void checkAndUpdateReservations() {
        LocalDateTime now = LocalDateTime.now();

        // Annuler les réservations sans dépôt après le délai
        List<EquipmentReservation> expiredReservations = reservationRepository
                .findExpiredDepositPendingReservations(now);

        expiredReservations.forEach(reservation -> {
            reservation.setStatus(ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);

            notificationService.sendReservationCancellationNotification(
                reservation.getUser().getId(),
                reservation.getEquipment().getUser().getId(),
                reservation.getEquipment().getName(),
                "Réservation annulée",
                reservation.getEquipment().getId()
            );
        });

        // Démarrer les réservations confirmées
        List<EquipmentReservation> startingReservations = reservationRepository
                .findStartingReservations(LocalDate.now());

        startingReservations.forEach(this::startReservation);
    }

    private void startReservation(EquipmentReservation reservation) {
        Equipment equipment = reservation.getEquipment();
        equipment.setStatus(EquipmentStatus.IN_USE);
        reservation.setStatus(ReservationStatus.CONFIRMED);

        notificationService.sendReservationStartNotification(
            reservation.getUser().getId(),
            reservation.getEquipment().getName(),
            reservation.getId()
        );
    }

    private boolean isEquipmentAvailable(Long equipmentId, LocalDate startDate, LocalDate endDate) {
        return reservationRepository
                .findOverlappingReservations(equipmentId, startDate, endDate)
                .isEmpty();
    }

    public List<EquipmentReservation> getUpcomingReservations(Long userId) {
        return reservationRepository.findUpcomingReservations(userId, LocalDate.now());
    }

    public List<EquipmentReservation> getPendingReservations(Long equipmentId) {
        return reservationRepository.findPendingReservations(equipmentId);
    }

    @Transactional
    public void cancelReservation(Long reservationId, String reason) {
        EquipmentReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setNotes(reason);

        // Si la réservation était confirmée, remettre l'équipement disponible
        if (reservation.getEquipment().getStatus() == EquipmentStatus.RESERVED) {
            reservation.getEquipment().setStatus(EquipmentStatus.OPERATIONAL);
        }

        reservationRepository.save(reservation);

        // Notifier les deux parties
        notificationService.sendReservationCancellationNotification(
            reservation.getUser().getId(),
            reservation.getEquipment().getUser().getId(),
            reservation.getEquipment().getName(),
            "Réservation annulée : " + reason,
            reservation.getEquipment().getId()
        );

        notificationService.sendReservationCancellationNotification(
            reservation.getEquipment().getUser().getId(),
            reservation.getUser().getId(),
            reservation.getEquipment().getName(),
            "Réservation annulée : " + reason,
            reservation.getEquipment().getId()
        );
    }
}
