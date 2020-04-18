package com.devfactory.processautomation.qa.rwa.repository;

import com.devfactory.processautomation.qa.rwa.domain.Region;
import com.devfactory.processautomation.repository.base.PaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends PaRepository<Region, Integer> {

    @Query("SELECT r FROM Region r WHERE r.name = :name")
    Optional<Region> findByName(@Param("name") String name);
}
