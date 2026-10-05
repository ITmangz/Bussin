package com.bussin.bussin_api.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bussin.bussin_api.dto.DashboardResponse;
import com.bussin.bussin_api.entity.BookingStatus;
import com.bussin.bussin_api.entity.BusStatus;
import com.bussin.bussin_api.entity.QueueStatus;
import com.bussin.bussin_api.entity.Role;
import com.bussin.bussin_api.repository.BookingRepository;
import com.bussin.bussin_api.repository.BusRepository;
import com.bussin.bussin_api.repository.QueueEntryRepository;
import com.bussin.bussin_api.repository.RouteRepository;
import com.bussin.bussin_api.repository.TripRepository;
import com.bussin.bussin_api.repository.UserRepository;

@Service
public class DashboardService {

    private final UserRepository userRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final TripRepository tripRepository;
    private final BookingRepository bookingRepository;
    private final QueueEntryRepository queueEntryRepository;

    public DashboardService(
            UserRepository userRepository,
            BusRepository busRepository,
            RouteRepository routeRepository,
            TripRepository tripRepository,
            BookingRepository bookingRepository,
            QueueEntryRepository queueEntryRepository) {
        this.userRepository = userRepository;
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
        this.tripRepository = tripRepository;
        this.bookingRepository = bookingRepository;
        this.queueEntryRepository = queueEntryRepository;
    }

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        return new DashboardResponse(
                userRepository.countByRole(Role.COMMUTER),
                userRepository.countByRole(Role.EMPLOYEE),
                userRepository.countByRole(Role.ADMIN),
                busRepository.count(),
                busRepository.countByStatus(BusStatus.ACTIVE),
                routeRepository.count(),
                routeRepository.countByActiveTrue(),
                tripRepository.count(),
                tripRepository.countByScheduledDepartureBetween(start, end),
                bookingRepository.count(),
                bookingRepository.countByCreatedAtBetween(start, end),
                queueEntryRepository.count(),
                queueEntryRepository.countByStatus(QueueStatus.WAITING),
                bookingRepository.countByStatus(BookingStatus.CONFIRMED),
                bookingRepository.countByStatus(BookingStatus.CANCELLED));
    }
}
