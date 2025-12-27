package com.marcosespeche.spring_batch_poc.domain.projects;

import com.marcosespeche.spring_batch_poc.entities.Customer;
import com.marcosespeche.spring_batch_poc.entities.Project;
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

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ProjectRepositoryTest {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("findByCustomerIdAndNameContainingIgnoreCase method")
    class findByCustomerIdAndNameContainingIgnoreCaseTest {

        @Test
        void shouldReturnOnlyCustomerProjectsContainingFilter() {
            // Arrange
            Customer customer1 = Customer.builder()
                    .name("Customer 1")
                    .email("customer_1@gmail.com")
                    .softDeleteDate(null)
                    .build();

            Customer customer2 = Customer.builder()
                    .name("Customer 2")
                    .email("customer_2@gmail.com")
                    .softDeleteDate(null)
                    .build();

            Project project1 = Project.builder()
                    .name("Project 1")
                    .description("Project 1")
                    .customer(customer1)
                    .softDeleteDate(null)
                    .build();

            Project project2 = Project.builder()
                    .name("Project 2")
                    .description("Project 2")
                    .customer(customer1)
                    .softDeleteDate(null)
                    .build();

            Project project3 = Project.builder()
                    .name("Project 2")
                    .description("Project 2")
                    .customer(customer2)
                    .softDeleteDate(null)
                    .build();

            entityManager.persist(customer1);
            entityManager.persist(customer2);
            entityManager.persist(project1);
            entityManager.persist(project2);
            entityManager.persist(project3);

            entityManager.flush();

            // Act
            Page<Project> result = projectRepository.findByCustomerIdAndNameContainingIgnoreCase(customer1.getId(), "2", PageRequest.of(0, 2));

            // Assert
            assertAll(
                    () -> assertNotNull(result, "Result should not be null"),
                    () -> assertEquals(1, result.getTotalElements(), "Total elements should be 1"),
                    () -> assertEquals(project2.getName(), result.getContent().getFirst().getName(), "Names should match"),
                    () -> assertEquals(project2.getCustomer().getName(), result.getContent().getFirst().getCustomer().getName(), "Customer name should match")
            );
        }
    }

    @Nested
    @DisplayName("findByCustomerIdAndNameContainingIgnoreCaseAndSoftDeleteDateIsNull method")
    class findByCustomerIdAndNameContainingIgnoreCaseAndSoftDeleteDateIsNullTest {

        @Test
        void shouldReturnOnlyCustomerProjectsContainingFilterAndActive() {
            // Arrange
            Customer customer1 = Customer.builder()
                    .name("Customer 1")
                    .email("customer_1@gmail.com")
                    .softDeleteDate(null)
                    .build();

            Customer customer2 = Customer.builder()
                    .name("Customer 2")
                    .email("customer_2@gmail.com")
                    .softDeleteDate(null)
                    .build();

            Project project1 = Project.builder()
                    .name("Project 1")
                    .description("Project 1")
                    .customer(customer1)
                    .softDeleteDate(null)
                    .build();

            Project project2 = Project.builder()
                    .name("Project 2")
                    .description("Project 2")
                    .customer(customer1)
                    .softDeleteDate(LocalDateTime.now())
                    .build();

            Project project3 = Project.builder()
                    .name("Project 2")
                    .description("Project 2")
                    .customer(customer2)
                    .softDeleteDate(null)
                    .build();

            entityManager.persist(customer1);
            entityManager.persist(customer2);
            entityManager.persist(project1);
            entityManager.persist(project2);
            entityManager.persist(project3);

            entityManager.flush();

            // Act
            List<Project> result = projectRepository.findByCustomerIdAndNameContainingIgnoreCaseAndSoftDeleteDateIsNull(customer1.getId(), "project");

            // Assert
            assertAll(
                    () -> assertNotNull(result, "Result should not be null"),
                    () -> assertEquals(1, result.size(), "Total elements should be 1"),
                    () -> assertEquals(project1.getName(), result.getFirst().getName(), "Names should match"),
                    () -> assertEquals(project1.getCustomer().getName(), result.getFirst().getCustomer().getName(), "Customer name should match")
            );
        }
    }

}