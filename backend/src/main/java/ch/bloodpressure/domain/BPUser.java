package ch.bloodpressure.domain;

import jakarta.persistence.*;
import lombok.*;

@Table(
       indexes = {
               @Index(name = "idx_bp_user_username", columnList = "username"),
               @Index(name = "idx_bp_user_email", columnList = "email")
       }
)
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class BPUser extends BaseEntity {

    @Column(nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String username;

    @Column(nullable = false, unique = true)
    @EqualsAndHashCode.Include
    private String email;

    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @EqualsAndHashCode.Include
    private Role role;
}
