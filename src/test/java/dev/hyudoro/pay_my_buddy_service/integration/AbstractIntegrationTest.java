package dev.hyudoro.pay_my_buddy_service.integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("integration")
@Import(TestPostgresConfig.class)
@Transactional
public abstract class AbstractIntegrationTest {
}
