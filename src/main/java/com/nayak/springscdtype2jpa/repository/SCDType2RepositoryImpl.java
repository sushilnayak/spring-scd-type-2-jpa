package com.nayak.springscdtype2jpa.repository;

import com.nayak.springscdtype2jpa.model.SCDType2Entity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.StringJoiner;

public class SCDType2RepositoryImpl<T extends SCDType2Entity, K>
        extends SimpleJpaRepository<T, K> implements SCDType2Repository<T> {

    private final EntityManager entityManager;

    public SCDType2RepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public T saveOrUpdate(T entity) {
        Map<String, Object> businessKey = entity.getBusinessKey();
        Set<String> businessKeyFieldNames = entity.getBusinessKeyFieldNames();
        Optional<T> existingEntityOpt = findActiveByIdentifier(businessKey, businessKeyFieldNames);

        if (existingEntityOpt.isPresent()) {
            T existingEntity = existingEntityOpt.get();
            existingEntity.setActive(false);
            existingEntity.setEndDate(LocalDateTime.now());
            entityManager.merge(existingEntity);
        }

        // Set default values for the new record
        entity.setStartDate(LocalDateTime.now());
        entity.setActive(true);
        entity.setEndDate(null);
        return entityManager.merge(entity);
    }

    @Override
    public Optional<T> findActiveByIdentifier(Map<String, Object> businessKey, Set<String> businessKeyFieldNames) {
        // Build a dynamic JPQL query with multiple conditions for composite keys
        String baseQuery = "SELECT e FROM " + getDomainClass().getSimpleName() + " e WHERE e.active = true";
        StringJoiner keyConditions = new StringJoiner(" AND ");

        for (String fieldName : businessKeyFieldNames) {
            keyConditions.add("e." + fieldName + " = :" + fieldName);
        }

        String query = baseQuery + " AND " + keyConditions;

        var queryObj = entityManager.createQuery(query, getDomainClass());
        for (Map.Entry<String, Object> entry : businessKey.entrySet()) {
            queryObj.setParameter(entry.getKey(), entry.getValue());
        }

        return queryObj.getResultStream().findFirst();
    }
}


