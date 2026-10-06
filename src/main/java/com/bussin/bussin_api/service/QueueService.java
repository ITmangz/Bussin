package com.bussin.bussin_api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bussin.bussin_api.entity.Booking;
import com.bussin.bussin_api.entity.QueueEntry;
import com.bussin.bussin_api.entity.QueueStatus;
import com.bussin.bussin_api.entity.Role;
import com.bussin.bussin_api.entity.Trip;
import com.bussin.bussin_api.entity.TripStatus;
import com.bussin.bussin_api.entity.User;
import com.bussin.bussin_api.exception.ConflictException;
import com.bussin.bussin_api.exception.ResourceNotFoundException;
import com.bussin.bussin_api.repository.QueueEntryRepository;
import com.bussin.bussin_api.repository.TripRepository;
import com.bussin.bussin_api.repository.UserRepository;
import com.google.firebase.auth.FirebaseToken;

@Service
public class QueueService {

    private final QueueEntryRepository queueEntryRepository;
    private final TripRepository tripRepository;
    private final UserRepository userRepository;

    public QueueService(
            QueueEntryRepository queueEntryRepository,
            TripRepository tripRepository,
            UserRepository userRepository) {

        this.queueEntryRepository = queueEntryRepository;
        this.tripRepository = tripRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public QueueEntry ensureQueueEntryForBooking(
            Trip trip,
            User commuter) {

        return ensureQueueEntryForBooking(trip, commuter, null);
    }

    @Transactional
    public QueueEntry ensureQueueEntryForBooking(
            Trip trip,
            Booking booking) {

        return ensureQueueEntryForBooking(trip, booking.getCommuter(), booking);
    }

    private QueueEntry ensureQueueEntryForBooking(
            Trip trip,
            User commuter,
            Booking booking) {

        trip = lockTripForQueueChange(trip.getId());
        validateJoinableTrip(trip);

        QueueEntry existing = commuter == null
                ? queueEntryRepository.findByBookingId(booking.getId()).orElse(null)
                : queueEntryRepository
                        .findByTripIdAndCommuterId(trip.getId(), commuter.getId())
                        .orElse(null);

        LocalDateTime now = LocalDateTime.now();

        if (existing != null) {
            if (existing.getStatus() != QueueStatus.CANCELLED) {
                if (booking != null) {
                    existing.setBooking(booking);
                    existing.setPassengerName(booking.getPassengerName());
                    existing.setPassengerEmail(booking.getPassengerEmail());
                    return queueEntryRepository.save(existing);
                }
                return existing;
            }

            int nextQueueNumber = getNextQueueNumber(trip.getId());
            existing.setCommuter(commuter);
            existing.setBooking(booking);
            existing.setUserId(commuter == null ? null : commuter.getId());
            existing.setPassengerName(booking == null ? null : booking.getPassengerName());
            existing.setPassengerEmail(booking == null ? null : booking.getPassengerEmail());
            existing.setQueueNumber(nextQueueNumber);
            existing.setPosition(nextQueueNumber);
            existing.setStatus(QueueStatus.WAITING);
            existing.setCreatedAt(now);
            existing.setJoinedAt(now);
            existing.setCalledAt(null);
            existing.setBoardedAt(null);
            existing.setUpdatedAt(now);

            return queueEntryRepository.save(existing);
        }

        QueueEntry entry = new QueueEntry();
        entry.setTrip(trip);
        entry.setCommuter(commuter);
        entry.setBooking(booking);
        entry.setUserId(commuter == null ? null : commuter.getId());
        entry.setPassengerName(booking == null ? null : booking.getPassengerName());
        entry.setPassengerEmail(booking == null ? null : booking.getPassengerEmail());
        int nextQueueNumber = getNextQueueNumber(trip.getId());
        entry.setQueueNumber(nextQueueNumber);
        entry.setPosition(nextQueueNumber);
        entry.setStatus(QueueStatus.WAITING);
        entry.setCreatedAt(now);
        entry.setJoinedAt(now);
        entry.setUpdatedAt(now);

        return queueEntryRepository.save(entry);
    }

    @Transactional
    public QueueEntry joinQueue(Long tripId) {

        Trip trip = tripRepository.findByIdForUpdate(tripId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trip not found with ID: " + tripId));

        User commuter = getAuthenticatedUser();

        if (commuter.getRole() != Role.COMMUTER) {
            throw new ConflictException(
                    "Only commuters can join a queue");
        }

        validateJoinableTrip(trip);

        QueueEntry existing = queueEntryRepository
                .findByTripIdAndCommuterId(
                        tripId,
                        commuter.getId())
                .orElse(null);

        if (existing != null && existing.getStatus() != QueueStatus.CANCELLED) {
            throw new ConflictException(
                    "You have already joined this trip's queue");
        }

        return ensureQueueEntryForBooking(trip, commuter);
    }

    @Transactional(readOnly = true)
    public List<QueueEntry> getTripQueue(
            Long tripId) {
        User staff = requireStaffUser();
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trip not found with ID: " + tripId));
        requireAssignedTripAccess(staff, trip);

        return queueEntryRepository
                .findByTripIdOrderByQueueNumberAsc(
                        tripId);
    }

    @Transactional(readOnly = true)
    public List<QueueEntry> getEmployeeQueue() {
        User employee = getAuthenticatedUser();
        if (employee.getRole() != Role.EMPLOYEE) {
            throw new ConflictException("Employee access is required");
        }
        return queueEntryRepository
                .findAssignedToEmployee(employee.getId());
    }

    @Transactional(readOnly = true)
    public QueueEntry getQueueEntry(
            Long queueEntryId) {
        User staff = requireStaffUser();
        QueueEntry entry = findQueueEntryById(queueEntryId);
        requireAssignedTripAccess(staff, entry.getTrip());
        return entry;
    }

    @Transactional(readOnly = true)
    public QueueEntry findQueueEntry(Long tripId, Long commuterId) {
        if (commuterId == null) {
            return null;
        }
        return queueEntryRepository
                .findByTripIdAndCommuterId(tripId, commuterId)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public QueueEntry findQueueEntryForBooking(Booking booking) {
        if (booking.getCommuter() == null) {
            return booking.getId() == null
                    ? null
                    : queueEntryRepository.findByBookingId(booking.getId()).orElse(null);
        }
        return findQueueEntry(booking.getTrip().getId(), booking.getCommuter().getId());
    }

    @Transactional(readOnly = true)
    public QueueEntry getMyQueueEntry(Long tripId) {
        User commuter = requireCommuterUser();

        return queueEntryRepository
                .findByTripIdAndCommuterId(
                        tripId,
                        commuter.getId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "You are not in this trip's queue"));
    }

    @Transactional
    public void cancelQueueEntryForBooking(Trip trip, User commuter) {
        if (commuter == null) {
            return;
        }
        trip = lockTripForQueueChange(trip.getId());
        QueueEntry entry = queueEntryRepository
                .findByTripIdAndCommuterId(trip.getId(), commuter.getId())
                .orElse(null);
        cancelQueueEntryForBooking(trip, entry);
    }

    @Transactional
    public void cancelQueueEntryForBooking(Booking booking) {
        Trip trip = lockTripForQueueChange(booking.getTrip().getId());
        QueueEntry entry = booking.getCommuter() == null
                ? queueEntryRepository.findByBookingId(booking.getId()).orElse(null)
                : queueEntryRepository
                        .findByTripIdAndCommuterId(trip.getId(), booking.getCommuter().getId())
                        .orElse(null);
        cancelQueueEntryForBooking(trip, entry);
    }

    @Transactional
    public void detachBookingFromQueueEntry(Booking booking) {
        if (booking.getId() == null) {
            return;
        }

        queueEntryRepository.findByBookingId(booking.getId()).ifPresent(entry -> {
            entry.setPassengerName(booking.getPassengerName());
            entry.setPassengerEmail(booking.getPassengerEmail());
            entry.setBooking(null);
            queueEntryRepository.save(entry);
        });
    }

    private void cancelQueueEntryForBooking(Trip trip, QueueEntry entry) {
        if (entry == null) {
            return;
        }

        if (entry.getStatus() == QueueStatus.CANCELLED) {
            compactQueueNumbers(trip.getId(), LocalDateTime.now());
            return;
        }

        if (entry.getStatus() == QueueStatus.BOARDED) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();
        entry.setStatus(QueueStatus.CANCELLED);
        entry.setUpdatedAt(now);
        queueEntryRepository.save(entry);
        compactQueueNumbers(trip.getId(), now);
    }

    @Transactional
    public void completeQueueEntryForBooking(Booking booking) {
        QueueEntry entry = findQueueEntryForBooking(booking);
        completeQueueEntryForBooking(entry);
    }

    @Transactional
    public void completeQueueEntryForBooking(Trip trip, User commuter) {
        QueueEntry entry = commuter == null
                ? null
                : queueEntryRepository
                        .findByTripIdAndCommuterId(trip.getId(), commuter.getId())
                        .orElse(null);
        completeQueueEntryForBooking(entry);
    }

    private void completeQueueEntryForBooking(QueueEntry entry) {
        if (entry == null || entry.getStatus() == QueueStatus.BOARDED) {
            return;
        }

        if (entry.getStatus() == QueueStatus.WAITING
                || entry.getStatus() == QueueStatus.CALLED) {
            LocalDateTime now = LocalDateTime.now();
            entry.setStatus(QueueStatus.BOARDED);
            entry.setBoardedAt(now);
            entry.setUpdatedAt(now);
            queueEntryRepository.save(entry);
        }
    }

    @Transactional
    public QueueEntry cancelQueueEntry(Long queueEntryId) {

        Trip trip = lockTripForQueueEntry(queueEntryId);
        QueueEntry entry = findQueueEntryById(queueEntryId);

        User commuter = requireCommuterUser();

        if (entry.getCommuter() == null
                || !entry.getCommuter().getId().equals(commuter.getId())) {

            throw new ConflictException(
                    "You can only cancel your own queue entry");
        }

        if (entry.getStatus() == QueueStatus.BOARDED) {
            throw new ConflictException(
                    "A boarded queue entry cannot be cancelled");
        }

        if (entry.getStatus() == QueueStatus.CANCELLED) {
            throw new ConflictException(
                    "Queue entry is already cancelled");
        }

        LocalDateTime now = LocalDateTime.now();
        entry.setStatus(QueueStatus.CANCELLED);
        entry.setUpdatedAt(now);

        QueueEntry cancelledEntry = queueEntryRepository.save(entry);
        compactQueueNumbers(trip.getId(), now);
        return cancelledEntry;
    }

    @Transactional
    public QueueEntry updateQueueStatus(
            Long queueEntryId,
            QueueStatus newStatus) {

        User staff = requireStaffUser();

        Trip trip = lockTripForQueueEntry(queueEntryId);
        requireAssignedTripAccess(staff, trip);
        QueueEntry entry = findQueueEntryById(queueEntryId);

        QueueStatus currentStatus = entry.getStatus();

        if (!isValidTransition(
                currentStatus,
                newStatus)) {

            throw new ConflictException(
                    "Invalid queue status transition from "
                            + currentStatus
                            + " to "
                            + newStatus);
        }

        LocalDateTime now = LocalDateTime.now();

        entry.setStatus(newStatus);
        entry.setUpdatedAt(now);

        if (newStatus == QueueStatus.CALLED) {
            entry.setCalledAt(now);
        }

        if (newStatus == QueueStatus.BOARDED) {
            entry.setBoardedAt(now);
        }

        QueueEntry updatedEntry = queueEntryRepository.save(entry);
        if (newStatus == QueueStatus.CANCELLED) {
            compactQueueNumbers(trip.getId(), now);
        }
        return updatedEntry;
    }

    private void validateJoinableTrip(
            Trip trip) {

        TripStatus status = trip.getStatus();

        if (status != TripStatus.SCHEDULED
                && status != TripStatus.BOARDING) {

            throw new ConflictException(
                    "This trip is not accepting queue entries");
        }
    }

    private int getNextQueueNumber(
            Long tripId) {

        return queueEntryRepository
                .findByTripIdOrderByQueueNumberAsc(tripId)
                .stream()
                .filter(entry -> entry.getStatus() != QueueStatus.CANCELLED)
                .mapToInt(QueueEntry::getQueueNumber)
                .max()
                .orElse(0) + 1;
    }

    private Trip lockTripForQueueChange(Long tripId) {
        return tripRepository.findByIdForUpdate(tripId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Trip not found with ID: " + tripId));
    }

    private Trip lockTripForQueueEntry(Long queueEntryId) {
        Long tripId = queueEntryRepository.findTripIdByQueueEntryId(queueEntryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Queue entry not found: " + queueEntryId));
        return lockTripForQueueChange(tripId);
    }

    private void compactQueueNumbers(Long tripId, LocalDateTime updatedAt) {
        List<QueueEntry> entries = new ArrayList<>(
                queueEntryRepository.findByTripIdOrderByQueueNumberAsc(tripId));
        entries.sort(Comparator.comparing(QueueEntry::getQueueNumber)
                .thenComparing(QueueEntry::getJoinedAt)
                .thenComparing(QueueEntry::getId));
        List<QueueEntry> renumberedEntries = new ArrayList<>();
        int nextQueueNumber = 1;

        for (QueueEntry entry : entries) {
            if (entry.getStatus() == QueueStatus.CANCELLED) {
                continue;
            }

            if (entry.getQueueNumber() != nextQueueNumber
                    || entry.getPosition() != nextQueueNumber) {
                entry.setQueueNumber(nextQueueNumber);
                entry.setPosition(nextQueueNumber);
                entry.setUpdatedAt(updatedAt);
                renumberedEntries.add(entry);
            }

            nextQueueNumber++;
        }

        if (!renumberedEntries.isEmpty()) {
            queueEntryRepository.saveAll(renumberedEntries);
        }
    }

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || !(authentication.getPrincipal() instanceof FirebaseToken firebaseToken)) {
            throw new ConflictException("Authenticated Firebase user is required");
        }

        return getUserByFirebaseUid(firebaseToken.getUid());
    }

    private User requireCommuterUser() {
        User commuter = getAuthenticatedUser();
        if (commuter.getRole() != Role.COMMUTER) {
            throw new ConflictException("Only commuters can manage their queue entries");
        }
        return commuter;
    }

    private User requireStaffUser() {
        User staff = getAuthenticatedUser();
        if (staff.getRole() != Role.ADMIN && staff.getRole() != Role.EMPLOYEE) {
            throw new ConflictException("Administrator or employee access is required");
        }
        return staff;
    }

    private void requireAssignedTripAccess(User staff, Trip trip) {
        if (staff.getRole() == Role.EMPLOYEE
                && (trip.getEmployee() == null
                        || !trip.getEmployee().getId().equals(staff.getId()))) {
            throw new ResourceNotFoundException("Trip not found");
        }
    }

    private QueueEntry findQueueEntryById(Long queueEntryId) {
        return queueEntryRepository.findByIdWithDetails(queueEntryId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Queue entry not found with ID: " + queueEntryId));
    }

    private User getUserByFirebaseUid(
            String firebaseUid) {

        return userRepository
                .findByFirebaseUid(firebaseUid)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User profile not found"));
    }

    private boolean isValidTransition(
            QueueStatus current,
            QueueStatus next) {

        if (current == QueueStatus.WAITING) {
            return next == QueueStatus.CALLED
                    || next == QueueStatus.CANCELLED;
        }

        if (current == QueueStatus.CALLED) {
            return next == QueueStatus.BOARDED
                    || next == QueueStatus.CANCELLED;
        }

        return false;
    }
}
