package com.nayak.springscdtype2jpa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Employee employee = (Employee) o;
        return Objects.equals(getId(), employee.getId()) &&
                Objects.equals(employeeId, employee.employeeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), employeeId);
    }
}
