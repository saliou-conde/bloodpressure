package ch.bloodpressure.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class BaseEntity {

    @Id
    @Column(updatable = false, nullable = false)
    @Builder.Default
    @EqualsAndHashCode.Include
    protected String id = UUID.randomUUID().toString();

    @Builder.Default
    @EqualsAndHashCode.Include
    protected Boolean isDeleted = false;

    @CreationTimestamp
    @Column(updatable = false, nullable = false)
    @EqualsAndHashCode.Include
    protected LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(insertable = false)
    @EqualsAndHashCode.Include
    protected LocalDateTime updatedAt;
}
