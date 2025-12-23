package com.marcosespeche.spring_batch_poc.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServiceRequestType extends BaseEntity{

    @Column(nullable = false, unique = true)
    private String name;

    private String description;

    private LocalDateTime softDeleteDate;

    @Column(nullable = false)
    private Double hourlyFee;
}
