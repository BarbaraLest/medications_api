package br.pucpr.medicineserver.security

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtHelper {
    private val secretHex = "976d12157dbf73366b74f45c1b0f170e870f09cef22193a8a09eac2dae710a63"

    private val key: SecretKey = Keys.hmacShaKeyFor(secretHex.chunked(2)
        .map { it.toInt(16).toByte() }
        .toByteArray())

    fun generateToken(username: String, role: String): String {
        val claims = mapOf("role" to role)

        return Jwts.builder()
            .setClaims(claims)
            .setSubject(username)
            .setIssuedAt(Date())
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60))
            .signWith(key, SignatureAlgorithm.HS256)
            .compact()
    }

    fun extractUsername(token: String): String {
        return getClaims(token).subject
    }

    fun extractRole(token: String): String {
        return getClaims(token)["role"] as String
    }

    fun isTokenValid(token: String, username: String): Boolean {
        return extractUsername(token) == username && !isTokenExpired(token)
    }

    private fun getClaims(token: String): Claims {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    }

    private fun isTokenExpired(token: String): Boolean {
        return getClaims(token).expiration.before(Date())
    }
}
