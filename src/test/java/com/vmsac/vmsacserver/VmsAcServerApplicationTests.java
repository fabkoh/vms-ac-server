package com.vmsac.vmsacserver;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Spring Boot application context smoke test.
 *
 * <p>Verifies that the full Spring application context loads without errors using the
 * "test" profile (H2 in-memory database).  If any bean fails to initialise — missing
 * required property, broken wiring, dialect mismatch — this test will fail and surface
 * the error early rather than as a cryptic failure in another test class.
 *
 * <p>No assertions are needed: a successful context load is the assertion.
 */
@SpringBootTest
@ActiveProfiles("test")
class VmsAcServerApplicationTests {

    @Test
    void contextLoads() {
    }
}
