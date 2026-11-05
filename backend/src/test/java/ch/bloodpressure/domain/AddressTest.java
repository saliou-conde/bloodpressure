package ch.bloodpressure.domain;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class AddressTest {

    @Test
    void equalsAndHashCode_shouldBeCorrect() {
        // Given
        var id = UUID.randomUUID().toString();
        var createdAt = LocalDateTime.now();
        var updatedAt = LocalDateTime.now().plusDays(1);
        var address1 = Address.builder()
                .city("New York City")
                .state("NY")
                .street("Main Street")
                .zipCode("8001")
                .build();

        var address2 = Address.builder()
                .city("New York City")
                .state("NY")
                .street("Main Street")
                .zipCode("8001")
                .build();

        // When
        address1.setId(id);
        address1.setIsDeleted(true);
        address1.setCreatedAt(createdAt);
        address1.setUpdatedAt(updatedAt);

        address2.setId(id);
        address2.setIsDeleted(true);
        address2.setCreatedAt(createdAt);
        address2.setUpdatedAt(updatedAt);

        // Then
        assertThat(address1.hashCode()).isEqualTo(address2.hashCode());
        assertThat(address1).isEqualTo(address2);
    }

    @Test
    void equals_shouldReturnFalseForDifferentIds() {
        // Given
        var address1 = Address.builder()
                .city("New York City")
                .state("NY")
                .street("Main Street")
                .zipCode("8001")
                .build();

        var address2 = Address.builder()
                .city("New York City")
                .state("NY")
                .street("Main Street")
                .zipCode("8001")
                .build();

        // When
        address1.setId(UUID.randomUUID().toString());
        address2.setId(UUID.randomUUID().toString());

        // Then
        assertThat(address1.hashCode()).isNotEqualTo(address2.hashCode());
        assertThat(address1).isNotEqualTo(address2);
    }

}