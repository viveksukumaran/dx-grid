package com.devfactory.processautomation.qa.rwa.service;

import com.devfactory.processautomation.devextreme.LoadOptions;
import com.devfactory.processautomation.devextreme.QueryBuilder;
import com.devfactory.processautomation.logger.Logger;
import com.devfactory.processautomation.qa.rwa.domain.Country;
import com.devfactory.processautomation.qa.rwa.domain.CountryRegion;
import com.devfactory.processautomation.qa.rwa.domain.Region;
import com.devfactory.processautomation.qa.rwa.repository.CountryRegionRepository;
import com.devfactory.processautomation.qa.rwa.repository.RegionRepository;
import com.devfactory.processautomation.qa.rwa.service.dtos.CountryDto;
import com.devfactory.processautomation.qa.rwa.service.dtos.CountryRegionDto;
import com.devfactory.processautomation.qa.rwa.service.dtos.InvalidRequestError;
import com.devfactory.processautomation.qa.rwa.service.dtos.ItemListResponse;
import com.devfactory.processautomation.qa.rwa.service.dtos.PagedResponse;
import com.devfactory.processautomation.qa.rwa.service.dtos.RegionDto;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class CountryServiceImpl extends BaseService implements CountryService {

    private final @NonNull CountryRegionRepository repository;
    private final @NonNull RegionRepository regionRepository;
    private final @NonNull Logger logger;

    public CountryServiceImpl(EntityManager entityManager, QueryBuilder queryBuilder, Logger logger,
            CountryRegionRepository mappingRepository, RegionRepository regionRepo) {
        super(entityManager, queryBuilder, logger);
        this.repository = mappingRepository;
        this.regionRepository = regionRepo;
        this.logger = logger;
    }

    @Override
    public PagedResponse<CountryRegionDto> getCountryRegionMappings(LoadOptions options) {
        PagedResponse<CountryRegionDto> result = new PagedResponse<>();
        PagedResponse<CountryRegion> entities = loadEntities(options, CountryRegion.class);
        if (!entities.isValid()) {
            result.addErrors(entities.getErrors());
            return result;
        }
        result.setItems(entities.getItems().stream().map(entity -> toDto(entity)).collect(Collectors.toList()));
        result.setTotalElements(entities.getTotalElements());
        return result;
    }

    @Override
    public ItemListResponse<CountryRegionDto> updateMappings(List<CountryRegionDto> mappings) {
        ItemListResponse<CountryRegionDto> result = new ItemListResponse<>();
        try {
            List<CountryRegion> updates = new ArrayList<>();
            for (CountryRegionDto mapping : mappings) {
                CountryRegion countryRegion =
                        repository.findCountryRegionByCountryCode(mapping.getCountry().getCode()).get();
                Region region = regionRepository.findByName(mapping.getRegion().getName()).get();
                countryRegion.setRegion(region);
                updates.add(countryRegion);
            }
            result.setItems(
                    repository.saveAll(updates).stream().map(entity -> toDto(entity)).collect(Collectors.toList()));
            return result;
        } catch (RuntimeException e) {
            logger.error("Failed to update mappings", e);
            result.addError(new InvalidRequestError(e.getMessage()));
            return result;
        }
    }

    private static CountryRegionDto toDto(CountryRegion mapping) {
        Country country = mapping.getCountry();
        CountryDto countryDto = new CountryDto(country.getName(), country.getCode());
        RegionDto regionDto = new RegionDto(mapping.getRegion().getName());
        return new CountryRegionDto(regionDto, countryDto);
    }
}
