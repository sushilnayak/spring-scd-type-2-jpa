package com.nayak.springscdtype2jpa.model;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Getter
@Setter
public class Employee extends SCDType2Entity<BigDecimal> {

    private String name;
    private String employeeId;
    private BigDecimal salary;

    @Override
    public BigDecimal getBusinessKey() {
        return salary;
    }
}
