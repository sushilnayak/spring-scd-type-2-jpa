package com.nayak.springscdtype2jpa.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "order_table")
public class Order extends SCDType2Entity {

    private String orderNumber;
    private LocalDate orderDate;
    private String customerName;

    @Override
    @Transient
    public Map<String, Object> getBusinessKey() {
        Map<String, Object> key = new HashMap<>();
        key.put("orderNumber", orderNumber);
        key.put("orderDate", orderDate);
        return key;
    }

    @Override
    @Transient
    public Set<String> getBusinessKeyFieldNames() {
        return Set.of("orderNumber", "orderDate");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(getId(), order.getId()) &&
                Objects.equals(orderNumber, order.orderNumber) &&
                Objects.equals(orderDate, order.orderDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), orderNumber, orderDate);
    }
}