package com.devfactory.processautomation.qa.rwa.service;

import com.devfactory.processautomation.qa.rwa.service.dtos.ItemListResponse;
import com.devfactory.processautomation.qa.rwa.service.dtos.RegionDto;

public interface RegionService {
    ItemListResponse<RegionDto> getAllRegions();
}
