package com.marcosespeche.spring_batch_poc.domain.customers;

import com.marcosespeche.spring_batch_poc.domain.customers.dtos.CreateCustomerDTO;
import com.marcosespeche.spring_batch_poc.domain.customers.dtos.ReadCustomerDTO;
import com.marcosespeche.spring_batch_poc.domain.customers.dtos.UpdateCustomerDTO;
import com.marcosespeche.spring_batch_poc.entities.Customer;
import com.marcosespeche.spring_batch_poc.mappers.CustomerMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("CustomerService Tests")
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @Spy
    private CustomerMapper customerMapper = Mappers.getMapper(CustomerMapper.class);

    @InjectMocks
    private CustomerService customerService;

    @Nested
    @DisplayName("create method")
    class createTest {

        @Test
        public void shouldThrowExceptionWhenDuplicatedName() {
            // Arrange
            String name = "Software Factory 123";
            String email = "software_factory_123@gmail.com";

            CreateCustomerDTO dto = new CreateCustomerDTO(name, email);

            when(customerRepository.existsByName(name))
                    .thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> customerService.create(dto)
            );

            assertEquals("Customer with that name already exists", exception.getMessage());
        }

        @Test
        public void shouldCreateCustomerWhenUniqueName() {
            // Arrange
            String name = "Software Factory 123";
            String email = "software_factory_123@gmail.com";

            CreateCustomerDTO dto = new CreateCustomerDTO(name, email);

            Customer savedCustomer = Customer.builder()
                    .id(1L)
                    .name(name)
                    .email(email)
                    .softDeleteDate(null)
                    .build();

            when(customerRepository.existsByName(name))
                    .thenReturn(false);

            when(customerRepository.save(ArgumentMatchers.any()))
                    .thenReturn(savedCustomer);

            // Act
            ReadCustomerDTO result = customerService.create(dto);

            // Assert
            assertNotNull(result);
            assertEquals(name, result.name());
            assertEquals(email, result.email());

        }
    }

    @Nested
    @DisplayName("update method")
    class updateTest {

        @Test
        public void shouldThrowExceptionWhenDuplicatedName() {
            // Arrange
            String name = "Software Factory 123";
            String email = "software_factory_123@gmail.com";

            UpdateCustomerDTO dto = new UpdateCustomerDTO(name, email);

            Customer savedCustomer = Customer.builder()
                    .id(1L)
                    .name("Restaurant 123")
                    .email("restaurant@gmail.com")
                    .softDeleteDate(null)
                    .build();

            when(customerRepository.findById(1L))
                    .thenReturn(Optional.of(savedCustomer));

            when(customerRepository.existsByName(name))
                    .thenReturn(true);

            // Act & Assert
            IllegalArgumentException exception = assertThrows(
                    IllegalArgumentException.class,
                    () -> customerService.update(1L, dto)
            );

            assertEquals("Customer with that name already exists", exception.getMessage());
        }

        @Test
        public void shouldUpdateCustomer() {
            // Arrange
            String name = "Software Factory 123";
            String email = "software_factory_123@gmail.com";
            Long id = 1L;

            UpdateCustomerDTO dto = new UpdateCustomerDTO(name, email);

            Customer savedCustomer = Customer.builder()
                    .id(id)
                    .name("Restaurant 123")
                    .email("restaurant@gmail.com")
                    .softDeleteDate(null)
                    .build();

            ReadCustomerDTO expectedResult = new ReadCustomerDTO(id, name, email, null);

            when(customerRepository.findById(id))
                    .thenReturn(Optional.of(savedCustomer));

            when(customerRepository.existsByName(name))
                    .thenReturn(false);

            // Act
            ReadCustomerDTO result = customerService.update(id, dto);

            // Assert
            assertAll(
                    () -> assertEquals(expectedResult.id(), result.id(), "IDs should match"),
                    () -> assertEquals(expectedResult.name(), result.name(), "Name should match"),
                    () -> assertEquals(expectedResult.email(), result.email(), "Email should match")
            );
        }
    }

    @Nested
    @DisplayName("deleteOrRestore method")
    class deleteOrRestoreTest {

        @Test
        void shouldSoftDeleteWhenNotDeleted() {
            // Arrange
            Long id = 1L;
            Customer entity = new Customer();
            entity.setId(id);
            entity.setName("Software Factory 123");
            entity.setEmail("software_factory_123@gmail.com");
            entity.setSoftDeleteDate(null);

            when(customerRepository.findById(id))
                    .thenReturn(Optional.of(entity));

            // Act
            ReadCustomerDTO result = customerService.deleteOrRestore(id);

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

            Customer entity = new Customer();
            entity.setId(id);
            entity.setName("Software Factory 123");
            entity.setEmail("software_factory_123@gmail.com");
            entity.setSoftDeleteDate(deletedDate);

            when(customerRepository.findById(id))
                    .thenReturn(Optional.of(entity));

            // Act
            ReadCustomerDTO result = customerService.deleteOrRestore(id);

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
            when(customerRepository.findById(id))
                    .thenReturn(Optional.empty());

            // Act & Assert
            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> customerService.deleteOrRestore(id),
                    "Should throw EntityNotFoundException"
            );

            assertEquals("Customer not found", exception.getMessage());
        }
    }

}