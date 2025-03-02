package br.pucpr.medicineserver.users

import br.pucpr.medicineserver.medication.Medication
import br.pucpr.medicineserver.security.Role
import jakarta.persistence.*
import java.time.LocalDate


@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false, unique = true)
    val username: String,

    @Column(nullable = false)
    val password: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val role: Role
    

){
    constructor() : this(null, "", "", Role.USER)
}