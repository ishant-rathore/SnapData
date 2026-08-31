package com.example.snapdata.auth.data

import com.example.snapdata.auth.domain.*
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository mediating between the UI presentation layer and the underlying AuthenticationProvider.
 *
 * Guarantees:
 * - Decouples ViewModels from provider specifics.
 * - Single source of truth for authentication & session state.
 */
class AuthRepository(
    private val provider: AuthenticationProvider = ProductionAuthProvider()
) {
    val authState: StateFlow<AuthState> = provider.authState
    val currentUser: AuthUser? get() = provider.currentUser

    suspend fun signIn(email: String, password: CharArray): AuthResult<AuthUser> {
        return provider.signIn(email, password)
    }

    suspend fun signUp(fullName: String, email: String, password: CharArray): AuthResult<AuthUser> {
        return provider.signUp(fullName, email, password)
    }

    suspend fun continueAsGuest(): AuthResult<AuthUser> {
        return provider.continueAsGuest()
    }

    suspend fun signOut(): AuthResult<Unit> {
        return provider.signOut()
    }

    suspend fun sendPasswordReset(email: String): AuthResult<Unit> {
        return provider.sendPasswordReset(email)
    }

    suspend fun sendEmailVerification(): AuthResult<Unit> {
        return provider.sendEmailVerification()
    }

    suspend fun checkEmailVerified(): AuthResult<Boolean> {
        return provider.checkEmailVerified()
    }

    suspend fun restoreSession(): AuthResult<AuthUser?> {
        return provider.restoreSession()
    }

    fun setNetworkAvailable(isAvailable: Boolean) {
        provider.setNetworkAvailable(isAvailable)
    }
}
