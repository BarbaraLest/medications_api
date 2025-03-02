package br.pucpr.medicineserver.medication

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate

interface MedicationRepository : JpaRepository<Medication, Long> {
    @Query("SELECT m FROM Medication m WHERE " +
            "(:name IS NULL OR m.name LIKE %:name%) AND " +
            "(:minDate IS NULL OR m.expirationDate >= :minDate) AND " +
            "(:maxDate IS NULL OR m.expirationDate <= :maxDate)")
    fun searchMedications(
        @Param("name") name: String?,
        @Param("minDate") minDate: LocalDate?,
        @Param("maxDate") maxDate: LocalDate?
    ): List<Medication>


    fun findByUserId(userId: Long): List<Medication>
}