package com.nayak.springscdtype2jpa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Entity
@Getter
@Setter
public class Employee extends SCDType2Entity {

    private String name;
    private String employeeId;
    private BigDecimal salary;

    @Override
    @Transient
    public Map<String, Object> getBusinessKey() {
        Map<String, Object> key = new HashMap<>();
        key.put("employeeId", employeeId);
        return key;
    }

    @Override
    @Transient
    public Set<String> getBusinessKeyFieldNames() {
        return Set.of("employeeId");
    }

}
