package br.pucpr.medicineserver.medication


import br.pucpr.medicineserver.security.JwtHelper
import br.pucpr.medicineserver.users.UserRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

@Tag(name = "Medications", description = "Gerenciamento de remédios do usuário")
@RestController
@RequestMapping("/medications")
class MedicationController(
    private val medicationRepository: MedicationRepository,
    private val userRepository: UserRepository,
    private val jwtUtil: JwtHelper
) {

    @Operation(summary = "Cadastrar um novo remédio", description = "Cria um novo registro de remédio para o usuário logado.")
    @PostMapping
    fun createMedication(
        @RequestBody medication: Medication,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Medication> {
        val username = jwtUtil.extractUsername(token.substring(7))
        val user = userRepository.findByUsername(username)
            ?: return ResponseEntity.badRequest().build()

        val newMedication = medication.copy(user = user)
        return ResponseEntity.ok(medicationRepository.save(newMedication))
    }

    @Operation(summary = "Listar todas os remédios", description = "Retorna os remédios do usuário logado, com opções de filtragem.")
    @GetMapping
    fun listMedications(
        @RequestHeader("Authorization") token: String,
        @RequestParam(required = false) name: String?,
        @RequestParam(required = false) minDate: LocalDate?,
        @RequestParam(required = false) maxDate: LocalDate?
    ): ResponseEntity<List<Medication>> {
        val username = jwtUtil.extractUsername(token.substring(7))
        val user = userRepository.findByUsername(username)
            ?: return ResponseEntity.badRequest().build()

        val medications = medicationRepository.searchMedications(name, minDate, maxDate)
            .filter { it.user.id == user.id }

        return ResponseEntity.ok(medications)
    }

    @Operation(summary = "Excluir um remédio" , description = "Remove um remédio cadastrado pelo usuário ou, se for ADMIN, qualquer remédio.")
    @DeleteMapping("/{id}")
    fun deleteMedication(
        @PathVariable id: Long,
        @RequestHeader("Authorization") token: String
    ): ResponseEntity<Void> {
        val username = jwtUtil.extractUsername(token.substring(7))
        val role = jwtUtil.extractRole(token.substring(7))
        val user = userRepository.findByUsername(username)
            ?: return ResponseEntity.badRequest().build()

        val medication = medicationRepository.findById(id)
            .orElse(null) ?: return ResponseEntity.notFound().build()


        if (role != "ADMIN" && medication.user.id != user.id) {
            return ResponseEntity.status(403).build()
        }

        medicationRepository.delete(medication)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "Editar um remédio" , description = "Edita um remédio cadastrado pelo usuário.")
    @PutMapping("/{id}")
    fun updateMedication(
        @PathVariable id: Long,
        @RequestBody updatedMedication: Medication
    ): ResponseEntity<Medication> {
        val medication = medicationRepository.findById(id)

        if (medication.isEmpty) {
            return ResponseEntity.notFound().build()
        }

        val existingMedication = medication.get()
        val updatedEntity = existingMedication.copy(
            name = updatedMedication.name,
            quantity = updatedMedication.quantity,
            expirationDate = updatedMedication.expirationDate,
            user = existingMedication.user
        )

        val savedMedication = medicationRepository.save(updatedEntity)
        return ResponseEntity.ok(savedMedication)
    }

}