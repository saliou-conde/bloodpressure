package ch.bloodpressure.dto.mapper;

import ch.bloodpressure.domain.Bloodpressure;
import ch.bloodpressure.dto.BloodpressureRequestDto;
import ch.bloodpressure.dto.BloodpressureResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BloodpressureMapper {

    @Mapping(target = "patientId", source = "patient.id")
    BloodpressureRequestDto toBloodpressureRequestDto(Bloodpressure bloodpressure);

    // RequestDto -> Entity
    Bloodpressure toBloodpressure(BloodpressureRequestDto request);

    // Entity -> ResponseDto
    BloodpressureResponseDto fromBloodpressure(Bloodpressure bloodpressure);
}
