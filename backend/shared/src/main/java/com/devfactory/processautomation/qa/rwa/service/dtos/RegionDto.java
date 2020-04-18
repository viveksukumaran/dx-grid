package com.devfactory.processautomation.qa.rwa.service.dtos;

import com.devfactory.processautomation.validator.ValidationBase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class RegionDto extends ValidationBase {
    private String name;
}
