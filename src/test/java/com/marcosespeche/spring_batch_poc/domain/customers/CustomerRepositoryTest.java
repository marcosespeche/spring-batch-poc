package com.marcosespeche.spring_batch_poc.domain.customers;

import com.marcosespeche.spring_batch_poc.entities.Customer;
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

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("findByNameContainingIgnoreCase method")
    class findByNameContainingIgnoreCaseTest {

        @Test
        public void shouldReturnCustomersContainingName() {
            // Arrange
            Customer customer1 = Customer.builder()
                    .name("Restaurant 123")
                    .email("restaurant123@gmail.com")
                    .softDeleteDate(null)
                    .build();

            Customer customer2 = Customer.builder()
                    .name("Software Factory 123")
                    .email("softwarefactory123@gmail.com")
                    .softDeleteDate(null)
                    .build();

            entityManager.persist(customer1);
            entityManager.persist(customer2);
            entityManager.flush();

            // Act
            Page<Customer> result = customerRepository.findByNameContainingIgnoreCase("software", PageRequest.of(0, 2));

            // Assert
            assertAll(
                    () -> assertNotNull(result, "Result should not be null"),
                    () -> assertEquals(1, result.getTotalElements(), "Should only have 1 element"),
                    () -> assertEquals("Software Factory 123", result.getContent().getFirst().getName(), "Name should match")
            );

        }
    }

    @Nested
    @DisplayName("findByNameContainingIgnoreCaseAndSoftDeleteDateIsNull method")
    class findByNameContainingIgnoreCaseAndSoftDeleteIsNullTest {

        @Test
        public void shouldReturnActiveCustomersContainingName() {
            // Arrange
            Customer customer1 = Customer.builder()
                    .name("Restaurant 123")
                    .email("restaurant123@gmail.com")
                    .softDeleteDate(LocalDateTime.now())
                    .build();

            Customer customer2 = Customer.builder()
                    .name("Software Factory 123")
                    .email("softwarefactory123@gmail.com")
                    .softDeleteDate(null)
                    .build();

            entityManager.persist(customer1);
            entityManager.persist(customer2);
            entityManager.flush();

            // Act
            Page<Customer> result = customerRepository.findByNameContainingIgnoreCaseAndSoftDeleteDateIsNull("123", PageRequest.of(0, 2));

            // Assert
            assertAll(
                    () -> assertNotNull(result, "Result should not be null"),
                    () -> assertEquals(1, result.getTotalElements(), "Should only have 1 element"),
                    () -> assertEquals("Software Factory 123", result.getContent().getFirst().getName(), "Name should match")
            );
        }
    }

    @Nested
    @DisplayName("existsByName method")
    class existsByNameTest {

        @Test
        public void shouldReturnFalseWhenNameDoesNotExist() {
            // Arrange
            String name = "Software Factory 123";

            Customer customer = Customer.builder()
                    .name(name)
                    .email("software_factory_123@gmail.com")
                    .softDeleteDate(null)
                    .build();

            entityManager.persistAndFlush(customer);

            // Act
            boolean result = customerRepository.existsByName("Restaurant 123");

            // Assert
            assertFalse(result);
        }

        @Test
        public void shouldReturnTrueWhenNameExists() {
            // Arrange
            String name = "Software Factory 123";

            Customer customer = Customer.builder()
                    .name(name)
                    .email("software_factory_123@gmail.com")
                    .softDeleteDate(null)
                    .build();

            entityManager.persistAndFlush(customer);

            // Act
            boolean result = customerRepository.existsByName(name);

            // Assert
            assertTrue(result);
        }
    }

}