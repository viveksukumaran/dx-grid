package com.devfactory.processautomation.qa.rwa.repository;

import com.devfactory.processautomation.qa.rwa.domain.CountryRegion;
import com.devfactory.processautomation.qa.rwa.domain.Region;
import com.devfactory.processautomation.repository.base.PaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRegionRepository extends PaRepository<CountryRegion, Integer> {

    @Query("SELECT cr.region FROM CountryRegion cr WHERE cr.country.code = :countryCode")
    Optional<Region> findRegion(@Param("countryCode") String countryCode);

    @Query("SELECT cr FROM CountryRegion cr WHERE cr.country.code = :countryCode")
    Optional<CountryRegion> findCountryRegionByCountryCode(@Param("countryCode") String countryCode);
}
