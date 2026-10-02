package com.bussin.bussin_api.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bussin.bussin_api.entity.Route;

public interface RouteRepository extends JpaRepository<Route, Long> {

    boolean existsByRouteIdentifier(String routeIdentifier);

    boolean existsByRouteIdentifierAndIdNot(String routeIdentifier, Long id);

    List<Route> findByActiveTrueOrderByRouteIdentifierAsc();

    List<Route> findAllByOrderByRouteIdentifierAsc();
}