package com.marcosespeche.spring_batch_poc.domain.projects;

import com.marcosespeche.spring_batch_poc.domain.customers.CustomerService;
import com.marcosespeche.spring_batch_poc.domain.projects.dtos.CreateProjectDTO;
import com.marcosespeche.spring_batch_poc.domain.projects.dtos.ReadProjectDTO;
import com.marcosespeche.spring_batch_poc.domain.projects.dtos.UpdateProjectDTO;
import com.marcosespeche.spring_batch_poc.entities.Customer;
import com.marcosespeche.spring_batch_poc.entities.Project;
import com.marcosespeche.spring_batch_poc.mappers.ProjectMapper;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @Mock
    private CustomerService customerService;

    @Spy
    private ProjectMapper projectMapper = Mappers.getMapper(ProjectMapper.class);

    @InjectMocks
    private ProjectService projectService;

    @Nested
    @DisplayName("create method")
    class CreateTest {

        @Test
        public void shouldThrowExceptionWhenCustomerDoesNotExist(){
            // Arrange
            Long customerId = 1L;
            CreateProjectDTO dto =
                    new CreateProjectDTO("Project", "Desc", customerId);

            when(customerService.findActiveById(customerId))
                    .thenThrow(new EntityNotFoundException("Customer not found"));

            // Act & Assert
            assertThrows(EntityNotFoundException.class,
                    () -> projectService.create(dto));
        }

        @Test
        public void shouldThrowExceptionWhenDuplicatedNameAndCustomer(){
            // Arrange
            Long customerId = 1L;
            String projectName = "Project 1";

            CreateProjectDTO dto = new CreateProjectDTO(projectName, "Description", customerId);

            when(projectRepository.existsByNameAndCustomerId(projectName, customerId))
                    .thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> projectService.create(dto)
            );

            assertEquals("Project with that name already exists for that customer", exception.getMessage());
        }

        @Test
        public void shouldCreateProjectWithUniqueNameAndCustomer(){
            // Arrange
            Long customerId = 1L;
            CreateProjectDTO dto =
                    new CreateProjectDTO("Project", "Desc", customerId);

            Customer savedCustomer = Customer.builder()
                    .id(customerId)
                    .name("Customer 1")
                    .email("customer1@gmail.com")
                    .softDeleteDate(null)
                    .build();

            when(customerService.findActiveById(customerId))
                    .thenReturn(savedCustomer);

            when((projectRepository.existsByNameAndCustomerId("Project", customerId)))
                    .thenReturn(false);

            // Act
            ReadProjectDTO result = projectService.create(dto);

            // Assert
            assertAll(
                    () -> assertNotNull(result, "Result should not be null"),
                    () -> assertEquals("Project", result.name(), "Name should match")
            );
        }
    }

    @Nested
    @DisplayName("update method")
    class UpdateTest{

        @Test
        public void shouldThrowExceptionWhenProjectDoesNotExist() {
            // Arrange
            Long projectId = 1L;
            UpdateProjectDTO dto = new UpdateProjectDTO("Project 1", "Description");

            when(projectRepository.findById(projectId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> projectService.update(projectId, dto)
            );

            assertEquals("Project not found", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionWhenProjectNameDuplicatedForCustomer() {
            // Arrange
            Long projectId = 1L;
            Long customerId = 1L;
            UpdateProjectDTO dto = new UpdateProjectDTO("Project 2", "Description");

            Customer customer = Customer.builder()
                    .id(customerId)
                    .name("Customer 1")
                    .email("customer1@gmail.com")
                    .softDeleteDate(null)
                    .build();

            Project project = Project.builder()
                    .id(projectId)
                    .name("Project 1")
                    .description("Description")
                    .customer(customer)
                    .softDeleteDate(null)
                    .build();

            when(projectRepository.findById(projectId))
                    .thenReturn(Optional.of(project));

            when(projectRepository.existsByNameAndCustomerId("Project 2", customerId))
                    .thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> projectService.update(projectId, dto)
            );

            assertEquals("Project with that name already exists for that customer", exception.getMessage());
        }

        @Test
        public void shouldUpdateProject() {
            // Arrange
            Long projectId = 1L;
            Long customerId = 1L;
            UpdateProjectDTO dto = new UpdateProjectDTO("Project 2", "Description");

            Customer customer = Customer.builder()
                    .id(customerId)
                    .name("Customer 1")
                    .email("customer1@gmail.com")
                    .softDeleteDate(null)
                    .build();

            Project project = Project.builder()
                    .id(projectId)
                    .name("Project 1")
                    .description("Description")
                    .customer(customer)
                    .softDeleteDate(null)
                    .build();

            when(projectRepository.findById(projectId))
                    .thenReturn(Optional.of(project));

            when(projectRepository.existsByNameAndCustomerId("Project 2", customerId))
                    .thenReturn(false);

            // Act
            ReadProjectDTO result = projectService.update(projectId, dto);

            // Assert
            assertAll(
                    () -> assertNotNull(result, "Result should not be null"),
                    () -> assertEquals("Project 2", result.name(), "Name should match")
            );
        }
    }

    @Nested
    @DisplayName("deleteOrRestore method")
    class DeleteOrRestoreTest{

        @Test
        public void shouldThrowExceptionWhenProjectNotFound() {
            // Arrange
            Long projectId = 1L;
            UpdateProjectDTO dto = new UpdateProjectDTO("Project 1", "Description");

            when(projectRepository.findById(projectId))
                    .thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> projectService.update(projectId, dto)
            );

            assertEquals("Project not found", exception.getMessage());
        }

        @Test
        public void shouldDeleteWhenNotDeleted() {
            // Arrange
            Long projectId = 1L;

            Project project = Project.builder()
                    .id(projectId)
                    .name("Project 1")
                    .description("Description")
                    .softDeleteDate(null)
                    .build();

            when(projectRepository.findById(projectId))
                    .thenReturn(Optional.of(project));

            // Act
            ReadProjectDTO result = projectService.deleteOrRestore(projectId);

            // Assert
            assertAll(
                    () -> assertNotNull(result, "Result should not be null"),
                    () -> assertNotNull(result.softDeleteDate(), "Project should be deleted")
            );
        }

        @Test
        public void shouldRestoreWhenDeleted() {
            // Arrange
            Long projectId = 1L;

            Project project = Project.builder()
                    .id(projectId)
                    .name("Project 1")
                    .description("Description")
                    .softDeleteDate(LocalDateTime.now())
                    .build();

            when(projectRepository.findById(projectId))
                    .thenReturn(Optional.of(project));

            // Act
            ReadProjectDTO result = projectService.deleteOrRestore(projectId);

            // Assert
            assertAll(
                    () -> assertNotNull(result, "Result should not be null"),
                    () -> assertNull(result.softDeleteDate(), "Project should be restored")
            );
        }
    }

    @Nested
    @DisplayName("findActiveById method")
    class FindActiveByIdTest {

        @Test
        public void shouldThrowExceptionWhenProjectNotFound() {
            // Arrange
            Long projectId = 1L;

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> projectService.findActiveById(projectId)
            );

            assertEquals("Project not found", exception.getMessage());
        }

        @Test
        public void shouldThrowExceptionWhenProjectIsNotActive() {
            // Arrange
            Long projectId = 1L;

            Customer customer = Customer.builder()
                    .name("Customer")
                    .email("customer@gmail.com")
                    .softDeleteDate(null)
                    .build();

            Project project = Project.builder()
                    .id(projectId)
                    .customer(customer)
                    .name("Project")
                    .description("Description")
                    .softDeleteDate(LocalDateTime.now())
                    .build();

            when(projectRepository.findById(projectId))
                    .thenReturn(Optional.of(project));

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> projectService.findActiveById(projectId)
            );

            assertEquals("Project is not active", exception.getMessage());
        }

        @Test
        public void shouldReturnProject() {
            // Arrange
            Long projectId = 1L;

            Customer customer = Customer.builder()
                    .name("Customer")
                    .email("customer@gmail.com")
                    .softDeleteDate(null)
                    .build();

            Project project = Project.builder()
                    .id(projectId)
                    .customer(customer)
                    .name("Project")
                    .description("Description")
                    .softDeleteDate(null)
                    .build();

            when(projectRepository.findById(projectId))
                    .thenReturn(Optional.of(project));

            // Act
            Project result = projectService.findActiveById(projectId);

            assertAll(
                    () -> assertNotNull(result, "Result should not be null"),
                    () -> assertEquals(project.getName(), result.getName(), "Name should match"),
                    () -> assertEquals(project.getDescription(), result.getDescription(), "Description should match")
            );
        }
    }

}