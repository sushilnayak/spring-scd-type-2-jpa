package com.nayak.springscdtype2jpa.repository;

import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface SCDType2Repository<T, K> {

    Optional<T> findActiveByIdentifier(K businessKey);

    T saveOrUpdate(T entity);
}