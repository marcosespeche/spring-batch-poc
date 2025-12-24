package com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes;

import com.marcosespeche.spring_batch_poc.entities.ServiceRequestType;
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
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
class ServiceRequestTypeRepositoryTest {

    @Autowired
    private ServiceRequestTypeRepository repository;

    @Autowired
    private TestEntityManager entityManager;

    @Nested
    @DisplayName("existsByName method")
    class existsByNameTests {

        @Test
        void shouldReturnTrueWhenNameExists() {
            // Arrange
            ServiceRequestType entity = ServiceRequestType.builder()
                    .name("Development")
                    .description("Dev requests")
                    .hourlyFee(20.5)
                    .softDeleteDate(null)
                    .build();

            entityManager.persistAndFlush(entity);

            // Act
            boolean exists = repository.existsByName("Development");

            // Assert
            assertTrue(exists);
        }

        @Test
        void shouldReturnFalseWhenNameDoesNotExist() {
            // Arrange
            ServiceRequestType entity = ServiceRequestType.builder()
                    .name("Development")
                    .description("Dev requests")
                    .hourlyFee(20.5)
                    .softDeleteDate(null)
                    .build();

            entityManager.persistAndFlush(entity);

            // Act
            boolean exists = repository.existsByName("Support");

            // Assert
            assertFalse(exists);
        }
    }

    @Nested
    @DisplayName("findByNameContainingIgnoreCaseAndSoftDeleteDateIsNull method")
    class findByNameContainingIgnoreCaseAndSoftDeleteDateIsNullTests {

        @Test
        void shouldFindOnlyNotDeletedMatchingNameIgnoringCase() {
            // Arrange
            String activeName = "Development";

            ServiceRequestType active = ServiceRequestType.builder()
                    .name(activeName)
                    .description("Active")
                    .hourlyFee(20.5)
                    .softDeleteDate(null)
                    .build();

            ServiceRequestType deleted = ServiceRequestType.builder()
                    .name("Development Support")
                    .description("Deleted")
                    .hourlyFee(20.5)
                    .softDeleteDate(LocalDateTime.now())
                    .build();

            entityManager.persist(active);
            entityManager.persist(deleted);
            entityManager.flush();

            // Act
            List<ServiceRequestType> result =
                    repository.findByNameContainingIgnoreCaseAndSoftDeleteDateIsNull("dev");

            // Assert
            assertAll(
                    () -> assertEquals(1, result.size()),
                    () -> assertEquals(activeName, result.getFirst().getName())
            );
        }

    }

    @Nested
    @DisplayName("findByNameContainingIgnoreCase method")
    class findByNameContainingIgnoreCaseTests {

        @Test
        void shouldReturnPagedResultIgnoringCase() {
            // Arrange
            for (int i = 1; i <= 5; i++) {
                entityManager.persist(
                        ServiceRequestType.builder()
                                .name("Development " + i)
                                .description("Desc " + i)
                                .hourlyFee(20.5)
                                .softDeleteDate(null)
                                .build()
                );
            }

            entityManager.flush();

            Pageable pageable = PageRequest.of(0, 2);

            // Act
            Page<ServiceRequestType> page =
                    repository.findByNameContainingIgnoreCase("DEV", pageable);

            // Assert
            assertAll(
                    () -> assertEquals(2, page.getContent().size()),
                    () -> assertEquals(5, page.getTotalElements()),
                    () -> assertEquals(3, page.getTotalPages())
            );
        }

        @Test
        void shouldReturnAll() {
            // Arrange
            for (int i = 1; i <= 2; i++) {
                entityManager.persist(
                        ServiceRequestType.builder()
                                .name("Development " + i)
                                .description("Desc " + i)
                                .hourlyFee(20.5)
                                .softDeleteDate(null)
                                .build()
                );
            }

            entityManager.flush();

            Pageable pageable = PageRequest.of(0, 2);

            // Act
            Page<ServiceRequestType> page =
                    repository.findByNameContainingIgnoreCase("", pageable);

            // Assert
            assertAll(
                    () -> assertEquals(2, page.getContent().size()),
                    () -> assertEquals(2, page.getTotalElements()),
                    () -> assertEquals(1, page.getTotalPages())
            );
        }
    }
}