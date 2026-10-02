package com.bussin.bussin_api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bussin.bussin_api.entity.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {

    Optional<Route> findByRouteIdentifier(String routeIdentifier);

    List<Route> findByActiveTrue();

    List<Route> findByOriginIgnoreCaseAndDestinationIgnoreCaseAndActiveTrue(
            String origin,
            String destination);
}