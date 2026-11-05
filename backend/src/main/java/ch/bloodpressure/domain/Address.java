package ch.bloodpressure.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(indexes = {
        @Index(name = "idx_address_city", columnList = "city"),
        @Index(name = "idx_address_zipcode", columnList = "zipCode")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Address extends BaseEntity {

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private String street;

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private String city;

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private String state;

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private String zipCode;
}
