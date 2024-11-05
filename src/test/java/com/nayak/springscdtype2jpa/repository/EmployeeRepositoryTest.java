package com.nayak.springscdtype2jpa.repository;

import com.nayak.springscdtype2jpa.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestPropertySource(properties = {
        "spring.jpa.show-sql=true",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
class EmployeeRepositoryTest {
    // Define the PostgreSQL container with Testcontainers
    @Container
    private static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    static {
        postgresContainer.start();
        // Set Spring datasource properties to use the Testcontainers PostgreSQL instance
        System.setProperty("spring.datasource.url", postgresContainer.getJdbcUrl());
        System.setProperty("spring.datasource.username", postgresContainer.getUsername());
        System.setProperty("spring.datasource.password", postgresContainer.getPassword());
    }

    @Autowired
    private EmployeeRepository employeeRepository;

    @BeforeEach
    public void setup() {
        employeeRepository.deleteAll();  // Clear previous test data
    }

    @Test
    @Transactional
    public void testSaveOrUpdateCreatesNewActiveRecord() {
        // Create a new employee (initial insertion)
        Employee employee = new Employee();
        employee.setName("John Doe");
        employee.setEmployeeId("abcdef");
        employee.setSalary(BigDecimal.ONE);

        employeeRepository.saveOrUpdate(employee);

        // Verify the employee was saved and is active
        Optional<Employee> activeEmployeeOpt = employeeRepository.findActiveByIdentifier(Map.of("employeeId", "abcdef"), Set.of("employeeId"));
        assertTrue(activeEmployeeOpt.isPresent(), "Expected an active employee record");
        Employee activeEmployee = activeEmployeeOpt.get();
        assertEquals("John Doe", activeEmployee.getName());
        assertEquals("abcdef", activeEmployee.getEmployeeId());
        assertEquals(BigDecimal.ONE, activeEmployee.getSalary());
        assertTrue(activeEmployee.isActive());

        // Update the employee (should trigger SCD Type 2 behavior)
        Employee updatedEmployee = new Employee();
        updatedEmployee.setName("John Doe");
        updatedEmployee.setEmployeeId("abcdef");
        updatedEmployee.setSalary(BigDecimal.TEN);
        employeeRepository.saveOrUpdate(updatedEmployee);

        // Verify that the old record is inactive and the new record is active
        Optional<Employee> previousEmployeeOpt = employeeRepository.findById(activeEmployee.getId());
        assertTrue(previousEmployeeOpt.isPresent());
        Employee previousEmployee = previousEmployeeOpt.get();
        assertFalse(previousEmployee.isActive(), "The previous record should be inactive");

        // Verify the new active record
        Optional<Employee> newActiveEmployeeOpt = employeeRepository.findActiveByIdentifier(Map.of("employeeId", "abcdef"), Set.of("employeeId"));
        assertTrue(newActiveEmployeeOpt.isPresent(), "Expected a new active employee record");
        Employee newActiveEmployee = newActiveEmployeeOpt.get();
        assertEquals("abcdef", newActiveEmployee.getEmployeeId());
        assertEquals(BigDecimal.TEN, newActiveEmployee.getSalary());
        assertTrue(newActiveEmployee.isActive());
        assertNotEquals(activeEmployee.getId(), newActiveEmployee.getId(), "The new record should have a different ID");

        // Verify only one active record exists for this business key
        long activeCount = employeeRepository.findAll().stream().filter(Employee::isActive).count();
        assertEquals(1, activeCount, "Only one active record should exist for the employee");
    }
}