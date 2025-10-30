import ch.bloodpressure.BloodpressureApplication;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

public class BloodpressureApplicationTest {

    @Test
    void main_shouldLogStartupMessages() {

        var logCaptor = LogCaptor.forClass(BloodpressureApplication.class);

        try (MockedStatic<SpringApplication> springApp = mockStatic(SpringApplication.class)) {
            springApp.when(() -> SpringApplication.run(any(Class.class), any(String[].class)))
                    .thenReturn(null);

            BloodpressureApplication.main(new String[]{});
        }

        assertThat(logCaptor.getInfoLogs())
                .anyMatch(msg -> msg.contains("Starting Bloodpressure Application"))
                .anyMatch(msg -> msg.contains("Bloodpressure Application started successfully"));
    }
}
