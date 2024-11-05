package com.nayak.springscdtype2jpa.repository;

import org.springframework.data.repository.NoRepositoryBean;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

@NoRepositoryBean
public interface SCDType2Repository<T> {

    Optional<T> findActiveByIdentifier(Map<String, Object> businessKey, Set<String> businessKeyFieldNames);

    T saveOrUpdate(T entity);
}