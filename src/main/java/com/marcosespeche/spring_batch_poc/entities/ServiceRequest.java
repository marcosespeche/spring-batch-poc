package com.marcosespeche.spring_batch_poc.entities;

import com.marcosespeche.spring_batch_poc.enums.ServiceRequestState;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceRequest extends BaseEntity {

    @Column(nullable = false)
    private String description;

    private LocalDateTime registeredAt;

    private LocalDateTime finishedAt;

    @Enumerated(EnumType.STRING)
    private ServiceRequestState state;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agreement", nullable = false)
    private Agreement agreement;

    private Customer getCustomer() {
        return this.agreement.getCustomer();
    }

}
