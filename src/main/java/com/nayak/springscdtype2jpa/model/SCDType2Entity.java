package com.nayak.springscdtype2jpa.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class SCDType2Entity<K> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private LocalDateTime startDate;

    @Column(nullable = true)
    private LocalDateTime endDate;

    @Column(nullable = false)
    private boolean active;

    public abstract K getBusinessKey();

    @PrePersist
    public void prePersist() {
        this.startDate = LocalDateTime.now();
        this.active = true;
    }
}