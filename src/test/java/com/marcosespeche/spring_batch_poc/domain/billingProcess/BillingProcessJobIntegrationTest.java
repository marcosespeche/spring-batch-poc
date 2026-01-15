package com.marcosespeche.spring_batch_poc.domain.billingProcess;

import com.marcosespeche.spring_batch_poc.config.SyncTaskExecutorConfiguration;
import com.marcosespeche.spring_batch_poc.entities.*;
import com.marcosespeche.spring_batch_poc.enums.AgreementState;
import com.marcosespeche.spring_batch_poc.enums.BillingProcessState;
import com.marcosespeche.spring_batch_poc.enums.ServiceRequestState;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersBuilder;
import org.springframework.batch.test.JobOperatorTestUtils;
import org.springframework.batch.test.JobRepositoryTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBatchTest
@SpringBootTest
@Import(SyncTaskExecutorConfiguration.class)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@TestPropertySource(properties = {
        "spring.main.allow-bean-definition-overriding=true",
        "spring.batch.job.enabled=false", // Prevents automatic execution
        "spring.batch.jdbc.initialize-schema=always",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
public class BillingProcessJobIntegrationTest {

    @Autowired
    private JobOperatorTestUtils jobOperatorTestUtils;

    @Autowired
    private JobRepositoryTestUtils jobRepositoryTestUtils;

    @Autowired
    private Job job;

    @PersistenceContext
    private EntityManager entityManager;

    @BeforeEach
    void setUp() {
        jobRepositoryTestUtils.removeJobExecutions();
        jobOperatorTestUtils.setJob(job);
    }

    @Test
    @Transactional
    public void testJob() throws Exception {
        // Arrange
        YearMonth actualPeriod = YearMonth.now();

        BillingProcess billingProcess = BillingProcess.builder()
                .state(BillingProcessState.REGISTERED)
                .totalAmountBillingProcess(0.0)
                .period(actualPeriod)
                .registeredAt(LocalDateTime.now())
                .billingProcessCustomerList(new ArrayList<>())
                .build();

        entityManager.persist(billingProcess);

        Long billingProcessId = billingProcess.getId();

        Customer customer = Customer.builder()
                .email("customer@gmail.com")
                .name("Customer 1")
                .softDeleteDate(null)
                .build();

        entityManager.persist(customer);

        Project project = Project.builder()
                .description("Project description")
                .name("Project name")
                .customer(customer)
                .softDeleteDate(null)
                .build();

        entityManager.persist(project);

        Agreement agreement = Agreement.builder()
                .acceptedAt(LocalDateTime.now().minusMonths(2))
                .state(AgreementState.ACCEPTED)
                .endingPeriod(actualPeriod.plusMonths(2))
                .startingPeriod(actualPeriod.minusMonths(2))
                .customer(customer)
                .project(project)
                .build();

        entityManager.persist(agreement);

        ServiceRequestType serviceRequestType = ServiceRequestType.builder()
                .softDeleteDate(null)
                .hourlyFee(10.0)
                .description("Description")
                .name("Name")
                .build();

        entityManager.persist(serviceRequestType);

        ServiceRequest serviceRequest1 = ServiceRequest.builder()
                .finishedAt(LocalDateTime.now())
                .type(serviceRequestType)
                .state(ServiceRequestState.DONE)
                .description("Description")
                .registeredAt(LocalDateTime.now().minusDays(15))
                .agreement(agreement)
                .build();

        ServiceRequest serviceRequest2 = ServiceRequest.builder()
                .finishedAt(LocalDateTime.now())
                .type(serviceRequestType)
                .state(ServiceRequestState.DONE)
                .description("Description")
                .registeredAt(LocalDateTime.now().minusDays(15))
                .agreement(agreement)
                .build();

        entityManager.persist(serviceRequest1);
        entityManager.persist(serviceRequest2);

        entityManager.flush();

        JobParameters jobParameters = new JobParametersBuilder()
                .addString("period", actualPeriod.toString())
                .addLong("billingProcessId", billingProcessId)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        // Act
        JobExecution jobExecution = jobOperatorTestUtils.startJob(jobParameters);

        await()
                .atMost(30, SECONDS)
                .pollInterval(200, MILLISECONDS)
                .untilAsserted(() -> {
                    assertFalse(jobExecution.isRunning());
                });

        // Assert
        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
        assertEquals(ExitStatus.COMPLETED, jobExecution.getExitStatus());
    }
}
