package com.devfactory.processautomation.qa.rwa.service;

import com.devfactory.processautomation.devextreme.QueryBuilder;
import com.devfactory.processautomation.logger.Logger;
import com.devfactory.processautomation.qa.rwa.domain.Region;
import com.devfactory.processautomation.qa.rwa.repository.CountryRegionRepository;
import com.devfactory.processautomation.qa.rwa.repository.RegionRepository;
import com.devfactory.processautomation.qa.rwa.service.dtos.InvalidRequestError;
import com.devfactory.processautomation.qa.rwa.service.dtos.ItemListResponse;
import com.devfactory.processautomation.qa.rwa.service.dtos.RegionDto;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import lombok.NonNull;
import org.springframework.stereotype.Service;

@Service
public class RegionServiceImpl extends BaseService implements RegionService {

    private final @NonNull CountryRegionRepository repository;
    private final @NonNull RegionRepository regionRepository;
    private final @NonNull Logger logger;

    public RegionServiceImpl(EntityManager entityManager, QueryBuilder queryBuilder, Logger logger,
            CountryRegionRepository mappingRepository, RegionRepository regionRepo) {
        super(entityManager, queryBuilder, logger);
        this.repository = mappingRepository;
        this.regionRepository = regionRepo;
        this.logger = logger;
    }

    @Override
    public ItemListResponse<RegionDto> getAllRegions() {
        ItemListResponse<RegionDto> result = new ItemListResponse<>();
        try {
            result.setItems(
                    regionRepository.findAll().stream().map(region -> toDto(region)).collect(Collectors.toList()));
            return result;
        } catch (RuntimeException e) {
            logger.error("Failed to get all regions", e);
            result.addError(new InvalidRequestError(e.getMessage()));
            return result;
        }
    }

    private static RegionDto toDto(Region region) {
        return new RegionDto(region.getName());
    }
}
