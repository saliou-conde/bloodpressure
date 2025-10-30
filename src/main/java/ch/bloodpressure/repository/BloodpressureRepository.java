package ch.bloodpressure.repository;

import ch.bloodpressure.domain.Bloodpressure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BloodpressureRepository extends JpaRepository<Bloodpressure, String> {

    List<Bloodpressure> findAllByIsDeletedFalse();

    @Query("""
            SELECT b
            FROM Bloodpressure b
            JOIN FETCH b.patient
            WHERE b.id = :id AND b.isDeleted = false
            """)
    Optional<Bloodpressure> findByIdWithPatient(@Param("id") String id);

    Optional<Bloodpressure> findByIdAndIsDeletedFalse(String id);

    @Modifying
    @Query(""" 
            UPDATE Bloodpressure bp
            SET bp.isDeleted = true
            WHERE bp.isDeleted = false
            """)
    void markAllBloodpressureAsDeleted();


    @Query("""
        SELECT bp
        FROM Bloodpressure bp
        WHERE bp.patient.id = :id AND (bp.isDeleted IS NULL OR bp.isDeleted = false)
    """)
    List<Bloodpressure> findBloodpressureByPatientId(@Param("id") String id);
}
