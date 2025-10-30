package ch.bloodpressure.repository;

import ch.bloodpressure.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<Patient, String> {

    @Query("""
            SELECT DISTINCT p
            FROM Patient p
            LEFT JOIN FETCH p.bloodpressure
            WHERE p.id = :id AND (p.isDeleted IS NULL OR p.isDeleted = false)
    """)
    Optional<Patient> findPatientByIdWithMeasurements(@Param("id") String id);

    @Query("""
            SELECT DISTINCT p
            FROM Patient p
            LEFT JOIN FETCH p.bloodpressure bp
            WHERE bp IS NULL OR bp.isDeleted = false
            ORDER BY p.lastName, p.firstName
    """)
    List<Patient> findAllPatientsWithMeasurements();

    @Modifying
    @Query("""
            UPDATE Patient p
            SET p.isDeleted = true
            WHERE p.isDeleted = false
            """)
    void markAllPatientsAsDeleted();

    @Query("""
            SELECT p
            FROM Patient p
            """)
    List<Patient> findAllPatientsOnly();

    boolean existsByEmailIgnoreCase(String email);

}
