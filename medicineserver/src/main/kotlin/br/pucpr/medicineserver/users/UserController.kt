package br.pucpr.medicineserver.users

import br.pucpr.medicineserver.security.JwtHelper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@Tag(name = "Users", description = "Gerenciamento de usuários")
@RestController
@RequestMapping("/users")
class UserController(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtHelper
) {


    @Operation(summary = "Criar um novo usuário", description = "Registra um novo usuário no sistema.")
    @PostMapping
    fun createUser(@RequestBody user: User): ResponseEntity<User> {
        if (userRepository.findByUsername(user.username) != null) {
            return ResponseEntity.badRequest().build()
        }
        return ResponseEntity.ok(userRepository.save(user))
    }

    @Operation(summary = "Obter detalhes do usuário", description = "Retorna informações do usuário pelo ID.")
    @GetMapping("/{id}")
    fun getUser(@PathVariable id: Long): ResponseEntity<User> {
        return userRepository.findById(id)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @Operation(summary = "Ver perfil do usuário", description = "Retorna informações baseadas no papel do usuário.")
    @GetMapping("/profile")
    fun getUserProfile(@RequestHeader("Authorization") token: String): ResponseEntity<String> {
        val username = jwtUtil.extractUsername(token.substring(7))
        val role = jwtUtil.extractRole(token.substring(7))

        val message = when (role) {
            "ADMIN" -> "Você pode gerenciar todos os usuários e medicações."
            "USER" -> "Você pode gerenciar suas próprias medicações."
            else -> "Acesso negado."
        }

        return ResponseEntity.ok(message)
    }
}