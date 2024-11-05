package com.nayak.springscdtype2jpa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@MappedSuperclass
public abstract class SCDType2Entity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime startDate;

    @Column(nullable = true)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private boolean active;

    public abstract Map<String, Object> getBusinessKey();     // Returns a map of field names to values for composite keys

    public abstract Set<String> getBusinessKeyFieldNames();   // Returns the names of the fields in the business key

    @PrePersist
    public void prePersist() {
        this.startDate = LocalDateTime.now();
        this.active = true;
    }
}