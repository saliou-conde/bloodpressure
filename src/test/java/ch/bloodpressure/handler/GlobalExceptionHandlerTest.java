package ch.bloodpressure.handler;

import ch.bloodpressure.controller.*;
import ch.bloodpressure.dto.PatientRequestDto;
import ch.bloodpressure.exception.*;
import ch.bloodpressure.service.*;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.*;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest({
        PatientController.class,
        BloodpressureController.class
})
@Import({
        GlobalExceptionHandling.class,
        GlobalExceptionHandlerTest.MockConfig.class
})
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PatientService patientService;

    @Autowired
    private BloodpressureService bloodpressureService;

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
    void shouldReturn404_whenPatientNotFound() throws Exception {
        var id = UUID.randomUUID().toString();
        when(patientService.getPatient(id))
                .thenThrow(new PatientNotFoundException("Patient not found"));

        mockMvc.perform(get("/api/v1/patients/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Patient not found"))
                .andExpect(jsonPath("$.title").value("Patient does not exist in the database"))
                .andExpect(jsonPath("$.status").value(NOT_FOUND.value()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturn400_whenEmailAlreadyExists() throws Exception {
        when(patientService.createPatient(any(PatientRequestDto.class)))
                .thenThrow(new EmailAlreadyExistException("Patient with email already exists"));

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                             {
                                "firstName": "John",
                                "lastName": "Doe",
                                "email": "test@example.com",
                                "gender": "MALE",
                                "birthDate": "1990-01-01",
                                "address": {
                                  "street": "Main-Street 10",
                                  "city": "Zürich",
                                  "state": "ZH",
                                  "zipCode": "8001"
                                }
                             }
                             """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Patient email already exists in the database"))
                .andExpect(jsonPath("$.detail").value("Patient with email already exists"))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void shouldReturn400_whenValidationFails() throws Exception {
        String invalidPatientJson = """
            {
                "firstName": "",
                "lastName": "",
                "email": "invalid-email",
                "gender": "MALE",
                "birthDate": "1990-01-01"
            }
            """;

        mockMvc.perform(post("/api/v1/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidPatientJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Bad Request"))
                .andExpect(jsonPath("$.detail").value("Validation failed for request body. Please check the input values."))
                .andExpect(jsonPath("$.status").value(BAD_REQUEST.value()))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.firstName").exists())
                .andExpect(jsonPath("$.lastName").exists())
                .andExpect(jsonPath("$.email").exists());
    }

    @Test
    void shouldReturn404_whenBloodpressureNotFound() throws Exception {
        var id = UUID.randomUUID().toString();

        when(bloodpressureService.getBloodpressure(id))
                .thenThrow(new BloodpressureNotFoundException("Bloodpressure not found"));

        mockMvc.perform(get("/api/v1/bloodpressure/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Bloodpressure not found"))
                .andExpect(jsonPath("$.title").value("Bloodpressure does not exist in the database"))
                .andExpect(jsonPath("$.status").value(NOT_FOUND.value()))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Nested
    class NullPointerExceptionHandlerTest {

        @Test
        void shouldReturn500_whenBloodpressureNull() throws Exception {
            when(patientService.getPatient(anyString())).thenThrow(new NullPointerException("Patient without bloodpressure measurements"));

            mockMvc.perform(get("/api/v1/patients/123"))
                    .andExpect(status().isInternalServerError())
                    .andExpect(jsonPath("$.title").value("Patient without bloodpressure measurements"))
                    .andExpect(jsonPath("$.detail").value("Patient without bloodpressure measurements"))
                    .andExpect(jsonPath("$.status").value(INTERNAL_SERVER_ERROR.value()))
                    .andExpect(jsonPath("$.timestamp").exists());
        }
    }
}
