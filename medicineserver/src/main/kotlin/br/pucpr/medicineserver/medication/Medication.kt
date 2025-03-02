package br.pucpr.medicineserver.medication
import br.pucpr.medicineserver.users.User
import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "medications")
data class Medication(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    val name: String = "",
    val expirationDate: String = "",
    val quantity: Int = 1,


    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    val user: User
) {
    constructor() : this(null, "", "", 1, user = User())
}