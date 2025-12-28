package com.marcosespeche.spring_batch_poc.entities;

import com.marcosespeche.spring_batch_poc.enums.AgreementState;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.time.YearMonth;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Agreement extends BaseEntity{

    @Column(nullable = false)
    private YearMonth startingPeriod;

    @Column(nullable = false)
    private YearMonth endingPeriod;

    private LocalDateTime acceptedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", length = 20)
    private AgreementState state;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "customer", nullable = false)
    private Customer customer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "project", nullable = false)
    private Project project;

}
