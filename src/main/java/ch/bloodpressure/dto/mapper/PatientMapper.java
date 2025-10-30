package ch.bloodpressure.dto.mapper;

import ch.bloodpressure.domain.Patient;
import ch.bloodpressure.dto.AddressDto;
import ch.bloodpressure.dto.PatientRequestDto;
import ch.bloodpressure.dto.PatientResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {BloodpressureMapper.class})
public interface PatientMapper {

    @Mapping(target = "bloodpressureRequestDtoList", source = "bloodpressure")
    PatientResponseDto fromPatientWithMeasurements(Patient patient);

    default PatientResponseDto fromPatientWithoutMeasurements(Patient patient) {
        return new PatientResponseDto(
                patient.getId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getEmail(),
                patient.getGender(),
                patient.getBirthDate().toString(),
                patient.getIsDeleted(),
                null,
                new AddressDto(
                        patient.getAddress().getStreet(),
                        patient.getAddress().getCity(),
                        patient.getAddress().getState(),
                        patient.getAddress().getZipCode())
        );
    }

    // RequestDto -> Entity
    @Mapping(target = "bloodpressure", ignore = true)
    Patient toPatientFromPatientRequestDto(PatientRequestDto request);

    // ResponseDto -> Entity
    @Mapping(target = "bloodpressure", ignore = true)
    Patient toPatientFromPatientResponseDto(PatientResponseDto response);
}
