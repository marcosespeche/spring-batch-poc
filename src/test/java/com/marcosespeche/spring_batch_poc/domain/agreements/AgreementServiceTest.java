package com.marcosespeche.spring_batch_poc.domain.agreements;

import com.marcosespeche.spring_batch_poc.domain.agreements.dtos.CreateAgreementDTO;
import com.marcosespeche.spring_batch_poc.domain.agreements.dtos.ReadAgreementDTO;
import com.marcosespeche.spring_batch_poc.domain.agreements.dtos.UpdateAgreementDTO;
import com.marcosespeche.spring_batch_poc.domain.projects.ProjectService;
import com.marcosespeche.spring_batch_poc.entities.Agreement;
import com.marcosespeche.spring_batch_poc.entities.Customer;
import com.marcosespeche.spring_batch_poc.entities.Project;
import com.marcosespeche.spring_batch_poc.enums.AgreementState;
import com.marcosespeche.spring_batch_poc.mappers.AgreementMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgreementServiceTest {

    @Mock
    private AgreementRepository agreementRepository;

    @Mock
    private ProjectService projectService;

    @Spy
    private AgreementMapper agreementMapper = Mappers.getMapper(AgreementMapper.class);

    @InjectMocks
    private AgreementService agreementService;

    @Nested
    @DisplayName("create method")
    class CreateTest{

        @Test
        public void shouldThrowExceptionWhenStartingYearLesserThanEndingYear() {
            // Arrange
            CreateAgreementDTO dto = new CreateAgreementDTO(
                    1, 2025, 12, 2024, 1L
            );

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> agreementService.create(dto)
            );

            assertEquals("Starting year can not be later than ending year", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionWhenStartingYearEqualsEndingYearAndStartingMonthLesserOrEqualsThanEndingMonth() {
            // Arrange
            CreateAgreementDTO dto = new CreateAgreementDTO(
                    6, 2025, 6, 2025, 1L
            );

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> agreementService.create(dto)
            );

            assertEquals("Starting month can not be later than ending month", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionWhenProjectNotFound() {
            // Arrange
            Long projectId = 1L;

            CreateAgreementDTO dto = new CreateAgreementDTO(
                    6, 2025, 6, 2026, projectId
            );

            when(projectService.findActiveById(projectId))
                    .thenThrow(new EntityNotFoundException("Project not found"));

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> agreementService.create(dto)
            );

            assertEquals("Project not found", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionWhenCustomerNotActive() {
            // Arrange
            Long projectId = 1L;

            Customer customer = Customer.builder()
                    .id(1L)
                    .name("Customer")
                    .email("customer@gmail.com")
                    .softDeleteDate(LocalDateTime.now())
                    .build();

            Project project = Project.builder()
                    .id(projectId)
                    .name("Project")
                    .description("Description")
                    .softDeleteDate(null)
                    .customer(customer)
                    .build();

            CreateAgreementDTO dto = new CreateAgreementDTO(
                    1, 2025, 1, 2026, projectId);

            when(projectService.findActiveById(projectId))
                    .thenReturn(project);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> agreementService.create(dto)
            );

            assertEquals("Customer is not active", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionWhenPreviousAgreementIsActive() {
            // Arrange
            Long projectId = 1L;

            Customer customer = Customer.builder()
                    .id(1L)
                    .name("Customer")
                    .email("customer@gmail.com")
                    .softDeleteDate(null)
                    .build();

            Project project = Project.builder()
                    .id(projectId)
                    .name("Project")
                    .description("Description")
                    .softDeleteDate(null)
                    .customer(customer)
                    .build();

            CreateAgreementDTO dto = new CreateAgreementDTO(
                    1, 2025, 1, 2026, projectId);

            when(agreementRepository.existsByCustomerIdAndStateNot(1L, AgreementState.FINISHED))
                    .thenReturn(true);

            when(projectService.findActiveById(projectId))
                    .thenReturn(project);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> agreementService.create(dto)
            );

            assertEquals("Agreement already registered for that customer. Finish it or delete it first", exception.getMessage());
        }

        @Test
        public void shouldCreateAgreement() {
            // Arrange
            Long projectId = 1L;

            Customer customer = Customer.builder()
                    .id(1L)
                    .name("Customer")
                    .email("customer@gmail.com")
                    .softDeleteDate(null)
                    .build();

            Project project = Project.builder()
                    .id(projectId)
                    .name("Project")
                    .description("Description")
                    .softDeleteDate(null)
                    .customer(customer)
                    .build();

            CreateAgreementDTO dto = new CreateAgreementDTO(
                    1, 2025, 1, 2026, projectId);

            when(agreementRepository.existsByCustomerIdAndStateNot(1L, AgreementState.FINISHED))
                    .thenReturn(false);

            when(projectService.findActiveById(projectId))
                    .thenReturn(project);

            // Act & Assert
            ReadAgreementDTO result = agreementService.create(dto);

            assertAll(
                    () -> assertNotNull(result, "Result should not be null"),
                    () -> assertEquals(1, result.startingPeriod().getMonth().getValue(), "Starting month should match"),
                    () -> assertEquals(1, result.endingPeriod().getMonth().getValue(), "Ending month should match"),
                    () -> assertEquals(2025, result.startingPeriod().getYear(), "Starting month should match"),
                    () -> assertEquals(2026, result.endingPeriod().getYear(), "Starting month should match"),
                    () -> assertEquals(project.getName(), result.projectName(), "Project name should match"),
                    () -> assertEquals(customer.getName(), result.customerName(), "Customer name should match")
            );
        }

    }

    @Nested
    @DisplayName("update method")
    class UpdateTest {

        @Test
        public void shouldThrowExceptionWhenProjectNotFound() {
            // Arrange
            Long agreementId = 1L;
            when(agreementRepository.findById(agreementId))
                    .thenReturn(Optional.empty());

            UpdateAgreementDTO dto = new UpdateAgreementDTO(
                    1, 2025, 12, 2026
            );

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> agreementService.update(agreementId, dto)
            );

            assertEquals("Agreement not found", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionWhenProjectStateIsNotProvisional() {
            // Arrange
            Long agreementId = 1L;

            Agreement agreement = Agreement.builder()
                    .id(agreementId)
                    .acceptedAt(null)
                    .state(AgreementState.IN_COURSE)
                    .startingPeriod(YearMonth.of(2025, 1))
                    .endingPeriod(YearMonth.of(2026, 1))
                    .build();

            when(agreementRepository.findById(agreementId))
                    .thenReturn(Optional.of(agreement));

            UpdateAgreementDTO dto = new UpdateAgreementDTO(
                    1, 2025, 12, 2026
            );

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> agreementService.update(agreementId, dto)
            );

            assertEquals("Agreement already accepted", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionWhenStartingYearIsGreaterThanEndingYear() {
            // Arrange
            Long agreementId = 1L;

            Agreement agreement = Agreement.builder()
                    .id(agreementId)
                    .acceptedAt(null)
                    .state(AgreementState.PROVISIONAL)
                    .startingPeriod(YearMonth.of(2025, 1))
                    .endingPeriod(YearMonth.of(2026, 1))
                    .build();

            when(agreementRepository.findById(agreementId))
                    .thenReturn(Optional.of(agreement));

            UpdateAgreementDTO dto = new UpdateAgreementDTO(
                    1, 2027, 12, 2026
            );

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> agreementService.update(agreementId, dto)
            );

            assertEquals("Starting year can not be later than ending year", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionWhenStartingYearEqualsEndingYearAndStartingMonthIsGreaterThanEndingMonth() {
            // Arrange
            Long agreementId = 1L;

            Agreement agreement = Agreement.builder()
                    .id(agreementId)
                    .acceptedAt(null)
                    .state(AgreementState.PROVISIONAL)
                    .startingPeriod(YearMonth.of(2025, 1))
                    .endingPeriod(YearMonth.of(2026, 1))
                    .build();

            when(agreementRepository.findById(agreementId))
                    .thenReturn(Optional.of(agreement));

            UpdateAgreementDTO dto = new UpdateAgreementDTO(
                    10, 2025, 2, 2025
            );

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> agreementService.update(agreementId, dto)
            );

            assertEquals("Starting month can not be later than ending month", exception.getMessage());
        }

        @Test
        public void shouldUpdateAgreement() {
            // Arrange
            Long agreementId = 1L;

            Agreement agreement = Agreement.builder()
                    .id(agreementId)
                    .acceptedAt(null)
                    .state(AgreementState.PROVISIONAL)
                    .startingPeriod(YearMonth.of(2025, 1))
                    .endingPeriod(YearMonth.of(2026, 1))
                    .build();

            when(agreementRepository.findById(agreementId))
                    .thenReturn(Optional.of(agreement));


            UpdateAgreementDTO dto = new UpdateAgreementDTO(
                    10, 2026, 12, 2026
            );

            // Act
            ReadAgreementDTO result = agreementService.update(agreementId, dto);

            // Assert
            assertAll(
                    () -> assertNotNull(result, "Result should not be null"),
                    () -> assertEquals(dto.startingMonth(), result.startingPeriod().getMonth().getValue(), "Starting month should match"),
                    () -> assertEquals(dto.startingYear(), result.startingPeriod().getYear(), "Starting year should match"),
                    () -> assertEquals(dto.endingMonth(), result.endingPeriod().getMonth().getValue(), "Ending month should match"),
                    () -> assertEquals(dto.endingYear(), result.endingPeriod().getYear(), "Ending year should match")
            );
        }
    }

    @Nested
    @DisplayName("delete method")
    class DeleteTest {

        @Test
        public void shouldThrowExceptionWhenAgreementNotFound() {
            // Arrange
            Long agreementId = 1L;

            when(agreementRepository.findById(agreementId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> agreementService.delete(agreementId)
            );

            assertEquals("Agreement not found", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionWhenAgreementStateIsNotProvisional() {
            // Arrange
            Long agreementId = 1L;

            Agreement agreement = Agreement.builder()
                    .id(agreementId)
                    .startingPeriod(YearMonth.of(2025, 1))
                    .endingPeriod(YearMonth.of(2026, 1))
                    .acceptedAt(LocalDateTime.now())
                    .state(AgreementState.IN_COURSE)
                    .build();

            when(agreementRepository.findById(agreementId))
                    .thenReturn(Optional.of(agreement));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> agreementService.delete(agreementId)
            );

            assertEquals("Agreement already accepted", exception.getMessage());
        }

        @Test
        public void shouldDeleteAgreementWhenStateIsProvisional() {
            // Arrange
            Long agreementId = 1L;

            Agreement agreement = Agreement.builder()
                    .id(agreementId)
                    .startingPeriod(YearMonth.of(2025, 1))
                    .endingPeriod(YearMonth.of(2026, 1))
                    .acceptedAt(LocalDateTime.now())
                    .state(AgreementState.PROVISIONAL)
                    .build();

            when(agreementRepository.findById(agreementId))
                    .thenReturn(Optional.of(agreement));

            // Act
            agreementService.delete(agreementId);

            // Assert
            verify(agreementRepository).deleteById(agreementId);
        }
    }

    @Nested
    @DisplayName("accept method")
    class AcceptTest {

        @Test
        public void shouldThrowExceptionWhenAgreementNotFound() {
            // Arrange
            Long agreementId = 1L;

            when(agreementRepository.findById(agreementId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> agreementService.accept(agreementId)
            );

            assertEquals("Agreement not found", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionWhenStartingPeriodBeforeActualPeriod() {
            // Arrange
            Long agreementId = 1L;

            Agreement agreement = Agreement.builder()
                    .id(agreementId)
                    .startingPeriod(YearMonth.now().minusMonths(1))
                    .endingPeriod(YearMonth.now().plusMonths(12))
                    .acceptedAt(null)
                    .state(AgreementState.PROVISIONAL)
                    .build();

            when(agreementRepository.findById(agreementId))
                    .thenReturn(Optional.of(agreement));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> agreementService.accept(agreementId)
            );

            assertEquals("Starting period already passed", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionWhenAgreementStateIsNotProvisional() {
            // Arrange
            Long agreementId = 1L;

            Agreement agreement = Agreement.builder()
                    .id(agreementId)
                    .startingPeriod(YearMonth.now().plusMonths(1))
                    .endingPeriod(YearMonth.now().plusMonths(12))
                    .acceptedAt(null)
                    .state(AgreementState.ACCEPTED)
                    .build();

            when(agreementRepository.findById(agreementId))
                    .thenReturn(Optional.of(agreement));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> agreementService.accept(agreementId)
            );

            assertEquals("Agreement already accepted", exception.getMessage());
        }

        @Test
        public void shouldAcceptAgreement() {
            // Arrange
            Long agreementId = 1L;

            Agreement agreement = Agreement.builder()
                    .id(agreementId)
                    .startingPeriod(YearMonth.now().plusMonths(1))
                    .endingPeriod(YearMonth.now().plusMonths(12))
                    .acceptedAt(null)
                    .state(AgreementState.PROVISIONAL)
                    .build();

            when(agreementRepository.findById(agreementId))
                    .thenReturn(Optional.of(agreement));

            // Act
            ReadAgreementDTO result = agreementService.accept(agreementId);

            // Assert
            assertAll(
                    () -> assertNotNull(result, "Result should not be null"),
                    () -> assertEquals(AgreementState.ACCEPTED, result.state(), "State should be 'Accepted'")
            );
        }
    }
}