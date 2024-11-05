package com.nayak.springscdtype2jpa.repository;

import com.nayak.springscdtype2jpa.model.Order;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepositoryImplementation<Order, Long>, SCDType2Repository<Order> {

}
