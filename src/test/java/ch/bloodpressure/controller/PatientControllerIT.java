package ch.bloodpressure.controller;

import ch.bloodpressure.AbstractBloodpressureIT;
import ch.bloodpressure.dto.PatientRequestDto;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class PatientControllerIT extends AbstractBloodpressureIT {

    @BeforeEach
    void setup() {
        RestAssured.port = port;
    }

    @Test
    void createPatient_shouldInsertPatient_whenValidDataGiven() {
        // Create
        PatientRequestDto request = patientPersistedTestDataBuilder.createPatientRequestDto("Saliou", "Conde", "saliou@example.com");

        String id = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/patient")
                .then()
                .statusCode(200)
                .body("firstName", equalTo("Saliou"))
                .body("lastName", equalTo("Conde"))
                .body("email", equalTo("saliou@example.com"))
                .extract()
                .path("id");

        // Get by ID
        given()
                .when()
                .get("/api/v1/patient/{id}", id)
                .then()
                .statusCode(200)
                .body("id", equalTo(id))
                .body("firstName", equalTo("Saliou"))
                .body("lastName", equalTo("Conde"));
    }

    @Test
    void getPatients_shouldGetPatientList_whenPatientsExist() {
        // Create a patient
        PatientRequestDto request = patientPersistedTestDataBuilder.createPatientRequestDto("Alice", "Smith", "alice@example.com");

        given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/patient")
                .then()
                .statusCode(200);

        // Get all patients
        given()
                .when()
                .get("/api/v1/patient")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(1))
                .body("[0].firstName", notNullValue());
    }

    @Test
    void updatePatient_shouldModifyPatient_whenValidDataGiven() {
        // Create
        PatientRequestDto request = patientPersistedTestDataBuilder.createPatientRequestDto(
                "John",
                "Doe",
                "john@example.com");

        String id = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/patient")
                .then()
                .extract()
                .path("id");

        // Update
        PatientRequestDto update = patientPersistedTestDataBuilder.createPatientRequestDto(
                "JohnUpdated",
                "DoeUpdated",
                "john.updated@example.com");

        given()
                .contentType(ContentType.JSON)
                .body(update)
                .when()
                .put("/api/v1/patient/{id}", id)
                .then()
                .statusCode(202);

        // Verify update
        given()
                .when()
                .get("/api/v1/patient/{id}", id)
                .then()
                .statusCode(200)
                .body("firstName", equalTo("JohnUpdated"))
                .body("lastName", equalTo("DoeUpdated"))
                .body("email", equalTo("john.updated@example.com"));
    }

    @Test
    void deletePatient_shouldSoftDelete_whenPatientPresent() {
        // Create
        PatientRequestDto request = patientPersistedTestDataBuilder.randomPatientRequestDto();

        String id = given()
                .contentType(ContentType.JSON)
                .body(request)
                .when()
                .post("/api/v1/patient")
                .then()
                .extract()
                .path("id");

        // Delete
        given()
                .when()
                .delete("/api/v1/patient/{id}", id)
                .then()
                .statusCode(202);

        // Verify not found anymore
        given()
                .when()
                .get("/api/v1/patient/{id}", id)
                .then()
                .statusCode(404);
    }
}
