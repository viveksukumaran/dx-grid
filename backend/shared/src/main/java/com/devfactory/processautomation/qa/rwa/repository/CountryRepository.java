package com.devfactory.processautomation.qa.rwa.repository;

import com.devfactory.processautomation.qa.rwa.domain.Country;
import com.devfactory.processautomation.repository.base.PaRepository;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends PaRepository<Country, Integer> {

    @Query("SELECT c FROM Country c WHERE c.code = :countryCode")
    Optional<Country> findByCode(@Param("countryCode") String countryCode);
}
