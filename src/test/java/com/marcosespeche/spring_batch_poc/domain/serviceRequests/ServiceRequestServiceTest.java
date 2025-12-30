package com.marcosespeche.spring_batch_poc.domain.serviceRequests;

import com.marcosespeche.spring_batch_poc.domain.agreements.AgreementService;
import com.marcosespeche.spring_batch_poc.domain.serviceRequestTypes.ServiceRequestTypeService;
import com.marcosespeche.spring_batch_poc.domain.serviceRequests.dtos.CreateServiceRequestDTO;
import com.marcosespeche.spring_batch_poc.domain.serviceRequests.dtos.ReadServiceRequestDTO;
import com.marcosespeche.spring_batch_poc.entities.Agreement;
import com.marcosespeche.spring_batch_poc.entities.ServiceRequestType;
import com.marcosespeche.spring_batch_poc.enums.AgreementState;
import com.marcosespeche.spring_batch_poc.mappers.ServiceRequestMapper;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceRequestServiceTest {

    @Mock
    private ServiceRequestRepository serviceRequestRepository;

    @Spy
    private ServiceRequestMapper serviceRequestMapper = Mappers.getMapper(ServiceRequestMapper.class);

    @Mock
    private AgreementService agreementService;

    @Mock
    private ServiceRequestTypeService serviceRequestTypeService;

    @InjectMocks
    private ServiceRequestService service;

    @Nested
    @DisplayName("create method")
    class CreateTest {

        @Test
        public void shouldThrowExceptionWhenAgreementStateIsNotInCourseOrAccepted() {
            // Arrange
            Long agreementId = 1L;
            Agreement agreement = Agreement.builder()
                    .state(AgreementState.PROVISIONAL)
                    .build();

            CreateServiceRequestDTO dto = new CreateServiceRequestDTO(
                    1L, agreementId, "Description"
            );

            when(agreementService.findById(agreementId))
                    .thenReturn(agreement);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.create(dto)
            );

            assertEquals("Agreement is not available for creating service requests", exception.getMessage());
        }


        @Test
        public void shouldThrowExceptionWhenTypeNotAvailable() {
            // Arrange
            Long agreementId = 1L;
            Agreement agreement = Agreement.builder()
                    .state(AgreementState.IN_COURSE)
                    .build();

            Long typeId = 1L;
            ServiceRequestType type = ServiceRequestType.builder()
                    .hourlyFee(10.0)
                    .name("Type")
                    .description("Description")
                    .softDeleteDate(LocalDateTime.now())
                    .build();

            CreateServiceRequestDTO dto = new CreateServiceRequestDTO(
                    1L, agreementId, "Description"
            );

            when(agreementService.findById(agreementId))
                    .thenReturn(agreement);

            when(serviceRequestTypeService.findById(typeId))
                    .thenReturn(type);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> service.create(dto)
            );

            assertEquals("Service Request Type not available", exception.getMessage());
        }


        @Test
        public void shouldCreateServiceRequest() {
            // Arrange
            Long agreementId = 1L;
            Agreement agreement = Agreement.builder()
                    .state(AgreementState.ACCEPTED)
                    .build();

            Long typeId = 1L;
            ServiceRequestType type = ServiceRequestType.builder()
                    .hourlyFee(10.0)
                    .name("Type")
                    .description("Description")
                    .softDeleteDate(null)
                    .build();

            CreateServiceRequestDTO dto = new CreateServiceRequestDTO(
                    1L, agreementId, "Description"
            );

            when(agreementService.findById(agreementId))
                    .thenReturn(agreement);

            when(serviceRequestTypeService.findById(typeId))
                    .thenReturn(type);

            // Act
            ReadServiceRequestDTO result = service.create(dto);

            // Assert
            assertAll(
                    () -> assertNotNull(result, "Result should not  be null"),
                    () -> assertEquals(dto.description(), result.description(), "Description should match")
            );
        }
    }

}