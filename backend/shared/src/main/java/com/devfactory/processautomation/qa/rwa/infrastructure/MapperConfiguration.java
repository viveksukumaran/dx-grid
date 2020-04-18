package com.devfactory.processautomation.qa.rwa.infrastructure;

import com.devfactory.processautomation.util.Mapper;
import com.devfactory.processautomation.util.MapperImpl;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MapperConfiguration {

    @Bean
    public Mapper mapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return new MapperImpl(modelMapper);
    }
}
