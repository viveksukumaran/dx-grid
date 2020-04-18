package com.devfactory.processautomation.qa.rwa.repository;

import com.devfactory.processautomation.qa.rwa.domain.Assessment;
import com.devfactory.processautomation.repository.base.PaRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AssessmentRepository extends PaRepository<Assessment, Integer> {
    @Query("SELECT a FROM Assessment a WHERE a.provisioningStatus IN :statuses")
    List<Assessment> findByStatus(@Param("statuses") List<String> statuses);

    @Query("SELECT a FROM Assessment a WHERE a.uuid = :uuid")
    Optional<Assessment> findByUuid(@Param("uuid") String uuid);
    
    @Query("SELECT a FROM Assessment a WHERE a.candidateEmail = :email")
    List<Assessment> findByEmail(@Param("email") String email);
    
    @Query("SELECT a FROM Assessment a WHERE a.salesforceOrderId = :orderId")
    List<Assessment> findByOrderId(@Param("orderId") String orderId);
}
