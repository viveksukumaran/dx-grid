package com.devfactory.processautomation.qa.rwa.service;

import com.devfactory.processautomation.devextreme.LoadOptions;
import com.devfactory.processautomation.qa.rwa.service.dtos.CountryRegionDto;
import com.devfactory.processautomation.qa.rwa.service.dtos.ItemListResponse;
import com.devfactory.processautomation.qa.rwa.service.dtos.PagedResponse;
import java.util.List;

public interface CountryService {

    PagedResponse<CountryRegionDto> getCountryRegionMappings(LoadOptions options);

    ItemListResponse<CountryRegionDto> updateMappings(List<CountryRegionDto> mappings);

}
