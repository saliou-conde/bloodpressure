package ch.bloodpressure.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(name = "idx_patient_email", columnList = "email", unique = true),
        @Index(name = "idx_patient_lastname", columnList = "lastName"),
        @Index(name = "idx_patient_address_id", columnList = "address_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Patient extends BaseEntity {

    @Column(nullable = false, length = 100)
    @EqualsAndHashCode.Include
    private String firstName;

    @Column(nullable = false, length = 100)
    @EqualsAndHashCode.Include
    private String lastName;

    @Column(unique = true, nullable = false)
    @EqualsAndHashCode.Include
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @EqualsAndHashCode.Include
    private Gender gender;

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private LocalDate birthDate;

//    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    @JoinColumn(name = "address_id", referencedColumnName = "id", nullable = false)
    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "address_id", nullable = false)
    @EqualsAndHashCode.Include
    private Address address;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    @EqualsAndHashCode.Include
    private List<Bloodpressure> bloodpressure = new ArrayList<>();

    @Transient  // Not persisted in the database
    public int getAge() {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    public void addBloodpressure(Bloodpressure bp) {
        bloodpressure.add(bp);
        bp.setPatient(this);
    }

    public void removeBloodpressure(Bloodpressure bp) {
        if (bp != null && bloodpressure.remove(bp)) {
            bp.setPatient(null);
        }
    }
}
