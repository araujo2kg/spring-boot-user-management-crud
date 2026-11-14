package com.example.usermanagement;

import java.time.Duration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.mysql.MySQLContainer;

/**
 * One disposable database per test JVM; Testcontainers' resource reaper owns final
 * cleanup.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class MySqlIntegrationTest {

    private static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.4.8")
        .withDatabaseName("user_management_test")
        .withStartupTimeout(Duration.ofSeconds(90));

    static {
        MYSQL.start();
    }

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry properties) {
        properties.add("spring.datasource.url", MYSQL::getJdbcUrl);
        properties.add("spring.datasource.username", MYSQL::getUsername);
        properties.add("spring.datasource.password", MYSQL::getPassword);
    }

}
