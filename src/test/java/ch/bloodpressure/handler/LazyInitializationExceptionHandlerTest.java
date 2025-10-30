package ch.bloodpressure.handler;

import ch.bloodpressure.controller.PatientController;
import ch.bloodpressure.service.BloodpressureService;
import ch.bloodpressure.service.PatientService;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({PatientController.class})
@Import({GlobalExceptionHandling.class, LazyInitializationExceptionHandlerTest.MockConfig.class})
class LazyInitializationExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientService patientService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        PatientService patientService() {
            return Mockito.mock(PatientService.class);
        }

        @Bean
        BloodpressureService bloodpressureService() {
            return Mockito.mock(BloodpressureService.class);
        }
    }

    @Test
    void shouldReturn500_whenLazyInitializationExceptionOccurs() throws Exception {
        when(patientService.getPatient(anyString()))
                .thenThrow(new LazyInitializationException("Failed to load lazy collection"));

        mockMvc.perform(get("/api/v1/patients/123"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.title").value("Failed to load lazy data"))
                .andExpect(jsonPath("$.detail").value("Failed to load lazy collection"))
                .andExpect(jsonPath("$.status").value(INTERNAL_SERVER_ERROR.value()))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}
