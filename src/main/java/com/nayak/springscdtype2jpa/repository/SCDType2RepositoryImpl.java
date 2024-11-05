package com.nayak.springscdtype2jpa.repository;

import com.nayak.springscdtype2jpa.model.SCDType2Entity;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;

import java.time.LocalDateTime;
import java.util.Optional;

public class SCDType2RepositoryImpl<T extends SCDType2Entity<K>, K> implements SCDType2Repository<T, K> {
    private final EntityManager entityManager;
    private final JpaEntityInformation<T, ?> entityInformation;

    public SCDType2RepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager) {
        this.entityInformation = entityInformation;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public T saveOrUpdate(T entity) {
        K businessKey = entity.getBusinessKey();
        Optional<T> existingEntityOpt = findActiveByIdentifier(businessKey);

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
    public Optional<T> findActiveByIdentifier(K businessKey) {
        String query = "SELECT e FROM " + entityInformation.getEntityName() + " e WHERE e.active = true AND e.getBusinessKey() = :businessKey";
        return entityManager.createQuery(query, entityInformation.getJavaType())
                .setParameter("businessKey", businessKey)
                .getResultStream()
                .findFirst();
    }
}


