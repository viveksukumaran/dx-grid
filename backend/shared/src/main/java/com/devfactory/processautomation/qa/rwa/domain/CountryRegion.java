package com.devfactory.processautomation.qa.rwa.domain;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

@Data
@Entity
public class CountryRegion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(value = AccessLevel.NONE)
    private int id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country")
    private Country country;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region")
    private Region region;
}
