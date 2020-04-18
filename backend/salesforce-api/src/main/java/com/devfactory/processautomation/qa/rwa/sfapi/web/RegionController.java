package com.devfactory.processautomation.qa.rwa.sfapi.web;

import com.devfactory.processautomation.qa.rwa.service.RegionService;
import com.devfactory.processautomation.qa.rwa.service.dtos.ItemListResponse;
import com.devfactory.processautomation.qa.rwa.service.dtos.RegionDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/regions")
public class RegionController {

    private final @NonNull RegionService service;

    @GetMapping()
    public ItemListResponse<RegionDto> getAllRegions() {
        return service.getAllRegions();
    }
}
