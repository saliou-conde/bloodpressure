package ch.bloodpressure.dto.mapper;

import ch.bloodpressure.domain.BPUser;
import ch.bloodpressure.dto.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BPUserMapper {

    BPUser toBPUser(RegisterRequest request);
}
