package com.bussin.bussin_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bussin.bussin_api.entity.Bus;
import com.bussin.bussin_api.entity.BusStatus;

public interface BusRepository extends JpaRepository<Bus, Long> {

    boolean existsByPlateNumber(String plateNumber);

    boolean existsByPlateNumberAndIdNot(String plateNumber, Long id);

    List<Bus> findByStatusOrderByPlateNumberAsc(BusStatus status);

    List<Bus> findAllByOrderByPlateNumberAsc();\n\n    long countByStatus(BusStatus status);
}
