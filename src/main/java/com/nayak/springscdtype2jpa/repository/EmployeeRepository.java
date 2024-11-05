package com.nayak.springscdtype2jpa.repository;

import com.nayak.springscdtype2jpa.model.Employee;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepositoryImplementation<Employee, Long>, SCDType2Repository<Employee, String> {

}
