package ch.bloodpressure.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(indexes = {
        @Index(name = "idx_bp_patient_id", columnList = "patient_id"),
        @Index(name = "idx_bp_created_at", columnList = "createdAt")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Bloodpressure extends BaseEntity {

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private Integer systole;

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private Integer diastole;

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private Integer map;

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private Integer heartRate;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    @EqualsAndHashCode.Include
    private Patient patient;
}
