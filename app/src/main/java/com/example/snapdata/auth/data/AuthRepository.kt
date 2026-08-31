package com.example.snapdata.auth.data

import com.example.snapdata.auth.domain.*
import kotlinx.coroutines.flow.StateFlow

/**
 * Repository mediating between the UI presentation layer and the underlying AuthenticationProvider.
 *
 * Guarantees:
 * - Decouples ViewModels from provider specifics.
 * - Single source of truth for authentication & session state.
 * - Production default: [FirebaseAuthProvider] (real accounts, real email verification).
 * - Test/offline fallback: [ProductionAuthProvider] (local session, guest mode always works).
 */
class AuthRepository(
    private val provider: AuthenticationProvider,
    val isFirebaseConfigured: Boolean = provider is FirebaseAuthProvider
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

    companion object {
        /**
         * Checks if Firebase is configured with active initialized apps.
         * Safe against missing google-services.json and uninitialized FirebaseApp.
         */
        fun isFirebaseAvailable(context: android.content.Context? = null): Boolean {
            return try {
                if (context != null) {
                    val apps = com.google.firebase.FirebaseApp.getApps(context)
                    apps.isNotEmpty()
                } else {
                    val apps = com.google.firebase.FirebaseApp.getApps(com.google.firebase.FirebaseApp.getInstance().applicationContext)
                    apps.isNotEmpty()
                }
            } catch (t: Throwable) {
                false
            }
        }

        /**
         * Creates an AuthRepository with the appropriate provider.
         *
         * If Firebase is available (google-services.json present + Firebase initialized),
         * uses [FirebaseAuthProvider] for real production authentication.
         *
         * Falls back to [ProductionAuthProvider] for local/offline environments.
         */
        fun create(sessionStorage: SecureSessionStorage, context: android.content.Context? = null): AuthRepository {
            return try {
                val isAvailable = isFirebaseAvailable(context)
                if (isAvailable) {
                    AuthRepository(FirebaseAuthProvider(sessionStorage), isFirebaseConfigured = true)
                } else {
                    AuthRepository(ProductionAuthProvider(sessionStorage), isFirebaseConfigured = false)
                }
            } catch (t: Throwable) {
                // Firebase not configured (no google-services.json or FirebaseApp not initialized) → use local provider
                AuthRepository(ProductionAuthProvider(sessionStorage), isFirebaseConfigured = false)
            }
        }
    }
}
