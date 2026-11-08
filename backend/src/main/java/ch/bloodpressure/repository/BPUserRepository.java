package ch.bloodpressure.repository;

import ch.bloodpressure.domain.BPUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BPUserRepository extends JpaRepository<BPUser, String> {
    Optional<BPUser> findByUsername(String username);
}
