package com.marcosespeche.spring_batch_poc.entities;

import com.marcosespeche.spring_batch_poc.enums.AgreementState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.YearMonth;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agreement extends BaseEntity{

    @Column(nullable = false)
    private YearMonth startingPeriod;

    @Column(nullable = false)
    private YearMonth endingPeriod;

    private LocalDateTime acceptedAt;

    @Enumerated(EnumType.STRING)
    private AgreementState state;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project", nullable = false)
    private Project project;
}
