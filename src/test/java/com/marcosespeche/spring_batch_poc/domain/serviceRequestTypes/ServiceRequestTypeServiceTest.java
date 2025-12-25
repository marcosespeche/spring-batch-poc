package com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes;

import com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes.dtos.CreateServiceRequestTypeDTO;
import com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes.dtos.ReadServiceRequestTypeDTO;
import com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes.dtos.UpdateServiceRequestTypeDTO;
import com.marcosespeche.spring_batch_poc.entities.ServiceRequestType;
import com.marcosespeche.spring_batch_poc.mappers.ServiceRequestTypeMapper;
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
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ServiceRequestTypeService Tests")
class ServiceRequestTypeServiceTest {

    @Mock
    private ServiceRequestTypeRepository serviceRequestTypeRepository;

    @Spy
    private ServiceRequestTypeMapper serviceRequestTypeMapper = Mappers.getMapper(ServiceRequestTypeMapper.class);

    @InjectMocks
    private ServiceRequestTypeService serviceRequestTypeService;


    @Nested
    @DisplayName("create method")
    class CreateTests {

        @Test
        void shouldCreateServiceRequestTypeWhenNameDoesNotExist() {
            // Arrange
            CreateServiceRequestTypeDTO dto =
                    new CreateServiceRequestTypeDTO("Development", "Software development", 20.5);

            when(serviceRequestTypeRepository.existsByName(dto.name()))
                    .thenReturn(false);

            when(serviceRequestTypeRepository.save(any(ServiceRequestType.class)))
                    .thenAnswer(invocation -> {
                        ServiceRequestType entity = invocation.getArgument(0);
                        entity.setId(1L);
                        return entity;
                    });

            // Act
            ReadServiceRequestTypeDTO result = serviceRequestTypeService.create(dto);

            // Assert
            assertAll("Verify creation",
                    () -> assertNotNull(result, "Result should not be null"),
                    () -> assertEquals(1L, result.id(), "ID should be generated"),
                    () -> assertEquals(dto.name(), result.name(), "Name should match"),
                    () -> assertEquals(dto.description(), result.description(), "Description should match"),
                    () -> assertNull(result.softDeleteDate(), "Soft delete date should be null")
            );
        }

        @Test
        void shouldThrowExceptionWhenNameAlreadyExists() {
            // Arrange
            CreateServiceRequestTypeDTO dto =
                    new CreateServiceRequestTypeDTO("Development", "Software development", 20.5);

            when(serviceRequestTypeRepository.existsByName(dto.name()))
                    .thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception =
                    assertThrows(IllegalArgumentException.class,
                            () -> serviceRequestTypeService.create(dto),
                            "Should throw IllegalArgumentException");

            assertEquals("Service Request Type's name already exists", exception.getMessage());

        }
    }


    @Nested
    @DisplayName("update method")
    class UpdateTests {

        @Test
        public void shouldThrowExceptionWhenNotFound() {
            // Arrange
            Long id = 1L;
            String name = "Backend development";
            UpdateServiceRequestTypeDTO dto = new UpdateServiceRequestTypeDTO(
                    name,
                    "Backend development",
                    20.5
            );

            when(serviceRequestTypeRepository.findById(id))
                    .thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> serviceRequestTypeService.update(id, dto)
            );

            assertEquals("Service Request Type not found", exception.getMessage());

        }

        @Test
        public void shouldThrowExceptionWhenDuplicateName() {
            // Arrange
            Long id = 1L;
            String name = "Backend development";

            UpdateServiceRequestTypeDTO dto = new UpdateServiceRequestTypeDTO(
                    name,
                    "Backend development",
                    20.5
            );

            ServiceRequestType savedEntity = new ServiceRequestType();
            savedEntity.setId(id);
            savedEntity.setName("Frontend Development");
            savedEntity.setDescription("Frontend development");
            savedEntity.setHourlyFee(22.5);

            when(serviceRequestTypeRepository.findById(id))
                    .thenReturn(Optional.of(savedEntity));

            when(serviceRequestTypeRepository.existsByName(name))
                    .thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> serviceRequestTypeService.update(id, dto)
            );

            assertEquals("Service Request Type's name already exists", exception.getMessage());
        }

        @Test
        public void shouldUpdateServiceRequestType() {
            // Arrange
            Long id = 1L;
            String name = "Backend development";
            String description = "Backend development";
            Double hourlyFee = 20.5;
            UpdateServiceRequestTypeDTO dto = new UpdateServiceRequestTypeDTO(
                    name,
                    description,
                    hourlyFee
            );

            ServiceRequestType savedEntity = new ServiceRequestType();
            savedEntity.setId(id);
            savedEntity.setName("Frontend Development");
            savedEntity.setDescription("Frontend development");
            savedEntity.setHourlyFee(22.5);

            when(serviceRequestTypeRepository.existsByName(name))
                    .thenReturn(false);

            when(serviceRequestTypeRepository.findById(id))
                    .thenReturn(Optional.of(savedEntity));

            // Act
            ReadServiceRequestTypeDTO response = serviceRequestTypeService.update(id, dto);

            // Assert
            assertAll(
                    () -> assertEquals(id, response.id(), "ID should match"),
                    () -> assertEquals(name, response.name(), "Name should match"),
                    () -> assertEquals(description, response.description(), "Description should match"),
                    () -> assertEquals(hourlyFee, response.hourlyFee(), "Hourly fee should match")
            );
        }
    }

    @Nested
    @DisplayName("deleteOrRestore method")
    class DeleteOrRestoreTests {

        @Test
        void shouldSoftDeleteWhenNotDeleted() {
            // Arrange
            Long id = 1L;
            ServiceRequestType entity = new ServiceRequestType();
            entity.setId(id);
            entity.setName("Test type");
            entity.setDescription("Test description");
            entity.setSoftDeleteDate(null);

            when(serviceRequestTypeRepository.findById(id))
                    .thenReturn(Optional.of(entity));

            // Act
            ReadServiceRequestTypeDTO result = serviceRequestTypeService.deleteOrRestore(id);

            // Assert
            assertAll("Verify soft delete",
                    () -> assertNotNull(result, "Result should not be null"),
                    () -> assertEquals(id, result.id(), "ID should match"),
                    () -> assertEquals(entity.getName(), result.name(), "Name should match"),
                    () -> assertNotNull(result.softDeleteDate(), "Soft Delete Date should be set"),
                    () -> assertNotNull(entity.getSoftDeleteDate(), "Soft Delete Date should be set")
            );
        }

        @Test
        @DisplayName("Should restore when entity is already deleted")
        void shouldRestoreWhenDeleted() {
            // Arrange
            Long id = 1L;
            LocalDateTime deletedDate = LocalDateTime.now().minusDays(1);

            ServiceRequestType entity = new ServiceRequestType();
            entity.setId(id);
            entity.setName("Test type");
            entity.setDescription("Test description");
            entity.setSoftDeleteDate(deletedDate);

            when(serviceRequestTypeRepository.findById(id))
                    .thenReturn(Optional.of(entity));

            // Act
            ReadServiceRequestTypeDTO result = serviceRequestTypeService.deleteOrRestore(id);

            // Assert
            assertAll("Verify restore",
                    () -> assertNotNull(result, "Result should not be null"),
                    () -> assertEquals(id, result.id(), "ID should match"),
                    () -> assertNull(result.softDeleteDate(), "Soft Delete Date should be null"),
                    () -> assertNull(entity.getSoftDeleteDate(), "Entity soft delete date should be null")
            );
        }

        @Test
        void shouldThrowExceptionWhenEntityNotFound() {
            // Arrange
            Long id = 999L;
            when(serviceRequestTypeRepository.findById(id))
                    .thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> serviceRequestTypeService.deleteOrRestore(id),
                    "Should throw EntityNotFoundException"
            );

            assertEquals("Service Request Type not found", exception.getMessage());
        }
    }

}