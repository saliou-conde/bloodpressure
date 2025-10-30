package ch.bloodpressure.controller;

import ch.bloodpressure.dto.BloodpressureRequestDto;
import ch.bloodpressure.dto.BloodpressureResponseDto;
import ch.bloodpressure.service.BloodpressureService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/bloodpressure")
public class BloodpressureController {

    private final BloodpressureService bloodpressureService;

    @Operation(
            description = "Add Bloodpressure",
            summary = "A new Bloodpressure will be added into the database",
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
    public ResponseEntity<BloodpressureResponseDto> addBloodpressure(@RequestBody @Valid BloodpressureRequestDto requestDto) {
        return ResponseEntity.ok(bloodpressureService.addBloodpressure(requestDto));
    }

    @Operation(
            description = "Get all Bloodpressure measurements",
            summary = "Display all Bloodpressure measurements.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping
    public ResponseEntity<List<BloodpressureResponseDto>> getBloodpressureMeasurements() {
        return ResponseEntity.ok(bloodpressureService.getBloodpressure());
    }

    @Operation(
            description = "Get all Bloodpressure measurements by provided Patient ID",
            summary = "Display all Bloodpressure measurements of a patient.",
            responses = {
                    @ApiResponse(
                            description = "Success",
                            responseCode = "200"
                    )
            }
    )
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<BloodpressureResponseDto>> getBloodpressureMeasurementsByPatientId(@PathVariable("patientId") String patientId) {
        return ResponseEntity.ok(bloodpressureService.getBloodpressureMeasurementsByPatientId(patientId));
    }

    @Operation(
            description = "Get Bloodpressure with provided ID",
            summary = "The found Bloodpressure with the provided id will be displayed.",
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
    public ResponseEntity<BloodpressureResponseDto> getBloodpressureById(@PathVariable("id") String id) {
        return ResponseEntity.ok(bloodpressureService.getBloodpressure( id));
    }

    @Operation(
            description = "Update Bloodpressure with provided ID",
            summary = "Bloodpressure will be modified.",
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
    public ResponseEntity<Void> updateBloodpressure(@PathVariable("id") String id, @RequestBody @Valid BloodpressureRequestDto requestDto) {
        bloodpressureService.updateBloodpressure(requestDto, id);
        return ResponseEntity.accepted().build();
    }

    @Operation(
            description = "Delete Bloodpressure with provided ID",
            summary = "The found Bloodpressure will be marked as deleted.",
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
    public ResponseEntity<Void> deleteBloodpressure(@PathVariable("id") String id) {
        bloodpressureService.deleteBloodpressure(id);
        return ResponseEntity.accepted().build();
    }
}
