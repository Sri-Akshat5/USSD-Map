package com.application.map.ussd_map.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.application.map.ussd_map.entity.LocationRequest;

public interface LocationRequestRepository extends JpaRepository<LocationRequest, Long> {
}
