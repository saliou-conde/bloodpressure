package ch.bloodpressure.controller;

import ch.bloodpressure.dto.PatientRequestDto;
import ch.bloodpressure.dto.PatientResponseDto;
import ch.bloodpressure.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping("/api/v1/patients")
@RequiredArgsConstructor
@Slf4j
public class PatientController {

    private final PatientService patientService;

    @Operation(
            description = "Add Patient ",
            summary = "A new Patient  will be added into the database",
            responses = {
                    @ApiResponse(
                            description = "Created",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    )
            }
    )
    @PostMapping
    public ResponseEntity<PatientResponseDto> createPatient(@Valid @RequestBody PatientRequestDto requestDto) {
        return ResponseEntity.status(CREATED).body(patientService.createPatient(requestDto));
    }

    @Operation(
            description = "Get all Patients",
            summary = "Display all Patients.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<PatientResponseDto>> getPatients() {
        return ResponseEntity.ok(patientService.getPatients());
    }

    @Operation(
            description = "Get Patient with the provided ID",
            summary = "The found Patient with the provided id will be displayed.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Not Found",
                            responseCode = "404"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<PatientResponseDto> getPatient(@PathVariable("id") String id) {
        return ResponseEntity.ok(patientService.getPatient(id));
    }

    @Operation(
            description = "Update Patient with the provided ID and the request body",
            summary = "Patient will be updated.",
            responses = {
                    @ApiResponse(
                            description = "OK",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Not Found",
                            responseCode = "404"
                    ),
                    @ApiResponse(
                            description = "Bad Request",
                            responseCode = "400"
                    )
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<Void> updatePatient(@PathVariable("id") String id, @Valid @RequestBody PatientRequestDto requestDto) {
        patientService.updatePatient(requestDto, id);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            description = "Delete Patient with the provided ID",
            summary = "The found Patient will be deleted from the database.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Not Found",
                            responseCode = "404"
                    )
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable("id") String id) {
        patientService.deletePatient(id);
        return ResponseEntity.accepted().build();
    }

}
