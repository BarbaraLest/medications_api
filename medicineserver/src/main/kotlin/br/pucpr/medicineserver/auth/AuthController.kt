package br.pucpr.medicineserver.auth


import br.pucpr.medicineserver.security.JwtHelper
import br.pucpr.medicineserver.users.UserRepository
import org.springframework.web.bind.annotation.*

data class AuthRequest(val username: String, val password: String)
data class AuthResponse(val token: String)

@RestController
@RequestMapping("/auth")
class AuthController(
    private val userRepository: UserRepository,
    private val jwtUtil: JwtHelper
) {
    @PostMapping("/login")
    fun login(@RequestBody request: AuthRequest): AuthResponse {
        val user = userRepository.findByUsername(request.username)
            ?: throw RuntimeException("Usuário não encontrado! Confira as informações e tente novamente.")

        if (user.password != request.password) {
            throw RuntimeException("Senha inválida!")
        }

        val token = jwtUtil.generateToken(user.username, user.role.name)
        return AuthResponse(token)
    }
}
