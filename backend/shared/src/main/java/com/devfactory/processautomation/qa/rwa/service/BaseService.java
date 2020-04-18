package com.devfactory.processautomation.qa.rwa.service;

import com.devfactory.processautomation.devextreme.LoadOptions;
import com.devfactory.processautomation.devextreme.QueryBuilder;
import com.devfactory.processautomation.devextreme.TypedQueryResponse;
import com.devfactory.processautomation.logger.Logger;
import com.devfactory.processautomation.qa.rwa.service.dtos.InvalidRequestError;
import com.devfactory.processautomation.qa.rwa.service.dtos.PagedResponse;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
abstract class BaseService {

    private final @NonNull EntityManager entityManager;
    private final @NonNull QueryBuilder queryBuilder;
    private final @NonNull Logger logger;

    protected <T> PagedResponse<T> loadEntities(LoadOptions options, Class<T> entityClass) {
        PagedResponse<T> result = new PagedResponse<>();
        try {
            TypedQueryResponse<T> response = queryBuilder.getQuery(options, entityClass, entityManager);
            if (!response.isValid()) {
                logger.error("Query generation failed. Reason = {}",
                        response.getErrors().get(0).getError().getMessage());
                result.addErrors(response.getErrors());
                return result;
            }
            TypedQuery<T> jpql = response.getQuery();
            if (isUnpaged(options) || options.isRequireTotalCount()) {
                List<T> results = jpql.getResultList();
                result.setTotalElements(results.size());
                result.setItems(results);
            }
            if (isUnpaged(options)) {
                return result;
            }
            if (options.getSkip() != null) {
                jpql.setFirstResult(options.getSkip());
            }
            if (options.getTake() != null) {
                jpql.setMaxResults(options.getTake());
            }
            result.setItems(jpql.getResultList());
            return result;
        } catch (RuntimeException e) {
            logger.error("Failed to load entities", e);
            result.addError(new InvalidRequestError("Failed to load entities. Reason = " + e.getMessage()));
            return result;
        }
    }

    private static boolean isUnpaged(LoadOptions options) {
        return options.getSkip() == null && options.getTake() == null;
    }
}
