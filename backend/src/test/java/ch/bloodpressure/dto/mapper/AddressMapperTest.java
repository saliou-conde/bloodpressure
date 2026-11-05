package ch.bloodpressure.dto.mapper;

import ch.bloodpressure.domain.Address;
import ch.bloodpressure.dto.AddressDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AddressMapperTest {

    private AddressMapper addressMapper;

    @BeforeEach
    void setUp() {
        addressMapper = new AddressMapperImpl();
    }

    @Test
    void toAddressDto_shouldMapEntityToDto() {
        // given
        var address = Address.builder()
                .street("Bahnhofstrasse 10")
                .city("Zürich")
                .state("ZH")
                .zipCode("8001")
                .build();

        // when
        var addressDto = addressMapper.toAddressDto(address);

        // then
        assertThat(addressDto).isNotNull();
        assertThat(addressDto.street()).isEqualTo(address.getStreet());
        assertThat(addressDto.city()).isEqualTo(address.getCity());
        assertThat(addressDto.state()).isEqualTo(address.getState());
        assertThat(addressDto.zipCode()).isEqualTo(address.getZipCode());
    }

    @Test
    void toAddress_shouldMapDtoToEntity() {
        // given
        var addressDto = new AddressDto("Bahnhofstrasse 10", "Zürich", "ZH", "8001");

        // when
        var address = addressMapper.toAddress(addressDto);

        // then
        assertThat(address).isNotNull();
        assertThat(address.getStreet()).isEqualTo(addressDto.street());
        assertThat(address.getCity()).isEqualTo(addressDto.city());
        assertThat(address.getState()).isEqualTo(addressDto.state());
        assertThat(address.getZipCode()).isEqualTo(addressDto.zipCode());
    }

    @Test
    void toAddressDto_shouldReturnNull_whenInputIsNull() {
        assertThat(addressMapper.toAddressDto(null)).isNull();
    }

    @Test
    void toAddress_shouldReturnNull_whenInputIsNull() {
        assertThat(addressMapper.toAddress(null)).isNull();
    }
}
