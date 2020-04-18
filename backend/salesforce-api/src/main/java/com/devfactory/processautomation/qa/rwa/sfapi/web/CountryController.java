package com.devfactory.processautomation.qa.rwa.sfapi.web;

import com.devfactory.processautomation.devextreme.LoadOptions;
import com.devfactory.processautomation.qa.rwa.service.CountryService;
import com.devfactory.processautomation.qa.rwa.service.dtos.CountryRegionDto;
import com.devfactory.processautomation.qa.rwa.service.dtos.ItemListResponse;
import com.devfactory.processautomation.qa.rwa.service.dtos.PagedResponse;
import java.util.List;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/countries")
public class CountryController {

    private final @NonNull CountryService service;

    @PostMapping("/regions")
    public PagedResponse<CountryRegionDto> loadMappings(@RequestBody LoadOptions request) {
        return service.getCountryRegionMappings(request);
    }

    @PutMapping("/regions")
    public ItemListResponse<CountryRegionDto> updateMappings(@RequestBody List<CountryRegionDto> mappings) {
        return service.updateMappings(mappings);
    }
}
