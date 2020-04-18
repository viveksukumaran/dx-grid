package com.devfactory.processautomation.qa.rwa.infrastructure;

import com.devfactory.processautomation.devextreme.QueryBuilder;
import com.devfactory.processautomation.devextreme.QueryBuilderImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DxUtilConfiguration {

    @Bean
    public QueryBuilder queryBuilder() {
        return new QueryBuilderImpl();
    }
}
