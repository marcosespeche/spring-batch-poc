package com.marcosespeche.spring_batch_poc.domain.serviceRequests;

import com.marcosespeche.spring_batch_poc.entities.*;
import com.marcosespeche.spring_batch_poc.enums.AgreementState;
import com.marcosespeche.spring_batch_poc.enums.ServiceRequestState;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.time.YearMonth;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(properties = {
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
class ServiceRequestRepositoryTest {

    @Autowired
    private ServiceRequestRepository serviceRequestRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("findByCustomerOrProject method")
    class FindByCustomerOrProjectTest {

        @Test
        public void shouldReturnServiceRequestsWithProjectNameOrCustomerNameLikeFilter() {
// Arrange
            Customer customer1 = Customer.builder()
                    .name("Customer ABC")
                    .email("customer_abc@gmail.com")
                    .build();

            Customer customer2 = Customer.builder()
                    .name("Customer 1")
                    .email("customer_1@gmail.com")
                    .build();

            entityManager.persist(customer1);
            entityManager.persist(customer2);
            entityManager.flush();

            Project project1 = Project.builder()
                    .customer(customer1)
                    .name("Project 1")
                    .description("Description")
                    .build();

            Project project2 = Project.builder()
                    .customer(customer2)
                    .name("Project ABC")
                    .description("Description")
                    .build();

            entityManager.persist(project1);
            entityManager.persist(project2);
            entityManager.flush();

            YearMonth starting1 = YearMonth.of(2025, 3);
            YearMonth ending1 = YearMonth.of(2025, 12);

            Agreement agreement1 = Agreement.builder()
                    .customer(customer1)
                    .project(project1)
                    .state(AgreementState.ACCEPTED)
                    .acceptedAt(LocalDateTime.now())
                    .startingPeriod(starting1)
                    .endingPeriod(ending1)
                    .build();

            YearMonth starting2 = YearMonth.of(2025, 3);
            YearMonth ending2 = YearMonth.of(2026, 7);

            Agreement agreement2 = Agreement.builder()
                    .customer(customer2)
                    .project(project2)
                    .state(AgreementState.PROVISIONAL)
                    .startingPeriod(starting2)
                    .endingPeriod(ending2)
                    .build();

            entityManager.persist(agreement1);
            entityManager.persist(agreement2);
            entityManager.flush();

            ServiceRequestType type = ServiceRequestType.builder()
                    .name("Backend development")
                    .description("Description")
                    .hourlyFee(10.0)
                    .softDeleteDate(null)
                    .build();

            entityManager.persist(type);
            entityManager.flush();

            ServiceRequest service1 = ServiceRequest.builder()
                    .agreement(agreement1)
                    .description("Service 1")
                    .state(ServiceRequestState.TO_DO)
                    .registeredAt(LocalDateTime.now())
                    .type(type)
                    .build();

            ServiceRequest service2 = ServiceRequest.builder()
                    .agreement(agreement2)
                    .description("Service 2")
                    .state(ServiceRequestState.TO_DO)
                    .registeredAt(LocalDateTime.now())
                    .type(type)
                    .build();

            entityManager.persist(service1);
            entityManager.persist(service2);
            entityManager.flush();

            // Act
            Page<ServiceRequest> result = serviceRequestRepository.findByCustomerOrProject(
                    "ABC",
                    "ABC",
                    PageRequest.of(0, 2));

            // Assert
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals(2, result.getTotalElements()),
                    () -> assertEquals(customer1.getName(), result.getContent().getFirst().getAgreement().getCustomer().getName(), "Customer name should match"),
                    () -> assertEquals(project1.getName(), result.getContent().getFirst().getAgreement().getProject().getName(), "Project name should match"),
                    () -> assertEquals(customer2.getName(), result.getContent().getLast().getAgreement().getCustomer().getName(), "Customer name should match"),
                    () -> assertEquals(project2.getName(), result.getContent().getLast().getAgreement().getProject().getName(), "Project name should match")
            );
        }
    }

}