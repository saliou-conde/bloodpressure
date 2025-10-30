package ch.bloodpressure.dto.mapper;

import ch.bloodpressure.domain.Address;
import ch.bloodpressure.dto.AddressDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AddressMapper {

    AddressDto toAddressDto(Address address);

    Address toAddress(AddressDto addressDto);
}
