package com.nayak.springscdtype2jpa.repository;

import com.nayak.springscdtype2jpa.model.Order;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

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
public class OrderRepositoryTest {
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
    private OrderRepository orderRepository;

    @BeforeEach
    public void setup() {
        orderRepository.deleteAll(); // Clear previous test data
    }

    @Test
    @Transactional
    public void testSaveOrUpdateWithCompositeKey() {
        // Create a new order (initial insertion)
        Order order = new Order();
        order.setOrderNumber("ORD001");
        order.setOrderDate(LocalDate.of(2024, 11, 5));
        order.setCustomerName("Alice");

        orderRepository.saveOrUpdate(order);

        // Verify the order was saved and is active
        Map<String, Object> businessKey = Map.of("orderNumber", "ORD001", "orderDate", LocalDate.of(2024, 11, 5));
        Optional<Order> activeOrderOpt = orderRepository.findActiveByIdentifier(businessKey, order.getBusinessKeyFieldNames());
        assertTrue(activeOrderOpt.isPresent(), "Expected an active order record");
        Order activeOrder = activeOrderOpt.get();
        assertEquals("ORD001", activeOrder.getOrderNumber());
        assertEquals(LocalDate.of(2024, 11, 5), activeOrder.getOrderDate());
        assertTrue(activeOrder.isActive());

        // Update the order (should trigger SCD Type 2 behavior)
        Order updatedOrder = new Order();
        updatedOrder.setOrderNumber("ORD001");
        updatedOrder.setOrderDate(LocalDate.of(2024, 11, 5)); // Same composite key
        updatedOrder.setCustomerName("Alice Updated");

        orderRepository.saveOrUpdate(updatedOrder);

        // Verify the old record is inactive and the new record is active
        Optional<Order> previousOrderOpt = orderRepository.findById(activeOrder.getId());
        assertTrue(previousOrderOpt.isPresent());
        Order previousOrder = previousOrderOpt.get();
        assertFalse(previousOrder.isActive(), "The previous record should be inactive");

        // Verify the new active record
        Optional<Order> newActiveOrderOpt = orderRepository.findActiveByIdentifier(businessKey, updatedOrder.getBusinessKeyFieldNames());
        assertTrue(newActiveOrderOpt.isPresent(), "Expected a new active order record");
        Order newActiveOrder = newActiveOrderOpt.get();
        assertEquals("Alice Updated", newActiveOrder.getCustomerName());
        assertTrue(newActiveOrder.isActive());
        assertNotEquals(activeOrder.getId(), newActiveOrder.getId(), "The new record should have a different ID");

        // Verify only one active record exists for this composite key
        long activeCount = orderRepository.findAll().stream().filter(Order::isActive).count();
        assertEquals(1, activeCount, "Only one active record should exist for the order");
    }
}
