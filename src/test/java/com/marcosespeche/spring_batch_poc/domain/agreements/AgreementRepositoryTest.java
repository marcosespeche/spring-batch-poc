package com.marcosespeche.spring_batch_poc.domain.agreements;

import com.marcosespeche.spring_batch_poc.entities.Agreement;
import com.marcosespeche.spring_batch_poc.entities.Customer;
import com.marcosespeche.spring_batch_poc.entities.Project;
import com.marcosespeche.spring_batch_poc.enums.AgreementState;
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
class AgreementRepositoryTest {

    @Autowired
    private AgreementRepository agreementRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("findByCustomerNameContainingIgnoreCaseOrProjectNameContainingIgnoreCase method")
    class FindByCustomerNameContainingIgnoreCaseOrProjectNameContainingIgnoreCase {

        @Test
        public void shouldOnlyReturnWhenCustomerNameOrProjectNameLikeFilter() {
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

            // Act
            Page<Agreement> result = agreementRepository
                    .findByCustomerNameContainingIgnoreCaseOrProjectNameContainingIgnoreCaseOrderByAcceptedAtDesc(
                            "ABC", "ABC", PageRequest.of(0, 2));

            // Assert
            assertAll(
                    () -> assertNotNull(result),
                    () -> assertEquals(2, result.getTotalElements()),
                    () -> assertEquals("Customer ABC", result.getContent().getFirst().getCustomer().getName()),
                    () -> assertEquals(starting1, result.getContent().getFirst().getStartingPeriod(), "Starting period should match"),
                    () -> assertEquals("Project ABC", result.getContent().getLast().getProject().getName())
            );
        }



    }

    @Nested
    @DisplayName("existsByCustomerIdAndStateNot method")
    class ExistsByCustomerIdAndStateNot {

        @Test
        void shouldReturnTrueWhenAgreementExistsWithDifferentState() {
            // Arrange
            AgreementState stateToExclude = AgreementState.FINISHED;

            Customer customer = Customer.builder()
                    .name("Customer")
                    .email("customer@gmail.com")
                    .softDeleteDate(null)
                    .build();

            Project project = Project.builder()
                    .name("Project")
                    .description("Description")
                    .customer(customer)
                    .softDeleteDate(null)
                    .build();

            Agreement agreement = Agreement.builder()
                    .state(AgreementState.IN_COURSE)
                    .customer(customer)
                    .project(project)
                    .startingPeriod(YearMonth.of(2025, 3))
                    .endingPeriod(YearMonth.of(2026, 1))
                    .acceptedAt(null)
                    .build();

            entityManager.persist(customer);
            entityManager.persist(project);
            entityManager.persist(agreement);

            entityManager.flush();

            // Act
            boolean result = agreementRepository.existsByCustomerIdAndStateNot(
                    customer.getId(),
                    stateToExclude
            );

            // Assert
            assertTrue(result);
        }

        @Test
        void shouldReturnFalseWhenNoAgreementExistsWithDifferentState() {
            // Arrange
            AgreementState stateToExclude = AgreementState.FINISHED;

            Customer customer = Customer.builder()
                    .name("Customer")
                    .email("customer@gmail.com")
                    .softDeleteDate(null)
                    .build();

            Project project = Project.builder()
                    .name("Project")
                    .description("Description")
                    .customer(customer)
                    .softDeleteDate(null)
                    .build();

            Agreement agreement = Agreement.builder()
                    .state(stateToExclude)
                    .customer(customer)
                    .project(project)
                    .startingPeriod(YearMonth.of(2025, 3))
                    .endingPeriod(YearMonth.of(2026, 1))
                    .acceptedAt(null)
                    .build();

            entityManager.persist(customer);
            entityManager.persist(project);
            entityManager.persist(agreement);

            entityManager.flush();

            // Act
            boolean result = agreementRepository.existsByCustomerIdAndStateNot(
                    customer.getId(),
                    stateToExclude
            );

            // Assert
            assertFalse(result);
        }
    }
}