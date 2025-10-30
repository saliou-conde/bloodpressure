package ch.bloodpressure;

import ch.bloodpressure.service.BloodpressureService;
import ch.bloodpressure.service.PatientService;
import ch.bloodpressure.testdata.BloodpressurePersistedTestDataBuilder;
import ch.bloodpressure.testdata.PatientPersistedTestDataBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.ApplicationContext;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@Testcontainers
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureTestDatabase(replace = NONE)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public abstract class AbstractBloodpressureIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @LocalServerPort
    protected Integer port;

    @Autowired
    protected PatientPersistedTestDataBuilder patientPersistedTestDataBuilder;

    @Autowired
    protected BloodpressurePersistedTestDataBuilder bloodpressurePersistedTestDataBuilder;

    @Autowired
    protected BloodpressureService bloodpressureService;

    @Autowired
    protected PatientService patientService;

    @Autowired
    protected ApplicationContext context;
}
