package ch.bloodpressure.repository;

import ch.bloodpressure.domain.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, String> {
}
