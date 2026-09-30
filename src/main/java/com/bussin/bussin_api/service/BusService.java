package com.bussin.bussin_api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import com.bussin.bussin_api.dto.BusResponse;
import com.bussin.bussin_api.dto.CreateBusRequest;
import com.bussin.bussin_api.dto.UpdateBusRequest;
import com.bussin.bussin_api.entity.Bus;
import com.bussin.bussin_api.entity.BusStatus;
import com.bussin.bussin_api.exception.ConflictException;
import com.bussin.bussin_api.exception.ResourceNotFoundException;
import com.bussin.bussin_api.repository.BusRepository;

@Service
public class BusService {

    private final BusRepository busRepository;

    public BusService(BusRepository busRepository) {
        this.busRepository = busRepository;
    }

    // ============================================================
    // GET ALL BUSES (optionally filtered by status)
    // ============================================================

    public List<BusResponse> getAllBuses(BusStatus status) {

        List<Bus> buses = (status == null)
                ? busRepository.findAllByOrderByPlateNumberAsc()
                : busRepository.findByStatusOrderByPlateNumberAsc(status);

        return buses.stream()
                .map(this::toResponse)
                .toList();
    }

    // ============================================================
    // GET BUS BY ID
    // ============================================================

    public BusResponse getBus(Long busId) {

        return toResponse(findBus(busId));
    }

    // ============================================================
    // CREATE BUS
    // ============================================================

    public BusResponse createBus(CreateBusRequest request) {

        String plateNumber = normalizePlate(request.getPlateNumber());

        if (busRepository.existsByPlateNumber(plateNumber)) {
            throw new ConflictException(
                    "A bus with this plate number already exists");
        }

        Bus bus = new Bus();

        bus.setPlateNumber(plateNumber);
        bus.setModel(normalizeOptional(request.getModel()));
        bus.setCapacity(request.getCapacity());
        bus.setStatus(
                request.getStatus() != null
                        ? request.getStatus()
                        : BusStatus.ACTIVE);

        LocalDateTime now = LocalDateTime.now();

        bus.setCreatedAt(now);
        bus.setUpdatedAt(now);

        return toResponse(saveOrConflict(bus));
    }

    // ============================================================
    // UPDATE BUS
    // ============================================================

    public BusResponse updateBus(
            Long busId,
            UpdateBusRequest request) {

        Bus bus = findBus(busId);

        String plateNumber = normalizePlate(request.getPlateNumber());

        if (busRepository.existsByPlateNumberAndIdNot(plateNumber, busId)) {
            throw new ConflictException(
                    "A bus with this plate number already exists");
        }

        bus.setPlateNumber(plateNumber);
        bus.setModel(normalizeOptional(request.getModel()));
        bus.setCapacity(request.getCapacity());
        bus.setStatus(request.getStatus());
        bus.setUpdatedAt(LocalDateTime.now());

        return toResponse(saveOrConflict(bus));
    }

    // ============================================================
    // DELETE BUS
    // ============================================================

    public void deleteBus(Long busId) {

        Bus bus = findBus(busId);

        try {
            busRepository.delete(bus);
            busRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            // Future-proofing: once trips reference buses, a bus that is
            // still in use cannot be deleted.
            throw new ConflictException(
                    "Bus cannot be deleted because it is referenced by other records");
        }
    }

    // ============================================================
    // HELPERS
    // ============================================================

    private Bus findBus(Long busId) {

        return busRepository
                .findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Bus not found"));
    }

    // Catches the race where two requests pass the exists-check together;
    // the unique constraint on plate_number is the final authority.
    private Bus saveOrConflict(Bus bus) {

        try {
            return busRepository.saveAndFlush(bus);
        } catch (DataIntegrityViolationException exception) {
            throw new ConflictException(
                    "A bus with this plate number already exists");
        }
    }

    private String normalizePlate(String plateNumber) {

        return plateNumber.trim().replaceAll("\\s+", " ")
                .toUpperCase(Locale.ROOT);
    }

    private String normalizeOptional(String value) {

        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }

    private BusResponse toResponse(Bus bus) {

        return new BusResponse(
                bus.getId(),
                bus.getPlateNumber(),
                bus.getModel(),
                bus.getCapacity(),
                bus.getStatus().name(),
                bus.getCreatedAt(),
                bus.getUpdatedAt());
    }
}
