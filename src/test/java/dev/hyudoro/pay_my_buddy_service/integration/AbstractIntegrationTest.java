package dev.hyudoro.pay_my_buddy_service.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * Base class for all integration tests.
 *
 * Each test runs inside a transaction that Spring rolls back on completion,
 * so no manual teardown is needed. {@code @Sql} annotations in subclasses
 * seed data that is visible within that same transaction and disappears
 * automatically after the test.
 */
@SpringBootTest
@ActiveProfiles("integration")
@Import(TestPostgresConfig.class)
@Transactional
public abstract class AbstractIntegrationTest {
}
