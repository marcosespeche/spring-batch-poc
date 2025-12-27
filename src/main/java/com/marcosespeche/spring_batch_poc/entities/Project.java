package com.marcosespeche.spring_batch_poc.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Project extends BaseEntity {

    @Column(nullable = false)
    private String name;

    private String description;

    private LocalDateTime softDeleteDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "customer")
    private Customer customer;


}
