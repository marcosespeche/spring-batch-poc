package com.marcosespeche.spring_batch_poc.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    private LocalDateTime softDeleteDate;
}
