package com.example.snapdata.auth.domain

import kotlinx.coroutines.flow.StateFlow

/**
 * Adapter interface for authentication backends.
 * Allows SnapData to decouple its presentation & domain layers from any specific SDK
 * (Firebase, Supabase, Okta, Custom Enterprise Backend, or Local Secure Provider).
 */
interface AuthenticationProvider {

    /**
     * Observable stream of session states.
     */
    val authState: StateFlow<AuthState>

    /**
     * Currently authenticated user, or null if unauthenticated.
     */
    val currentUser: AuthUser?

    /**
     * Initiates sign-in using email and password.
     */
    suspend fun signIn(email: String, password: CharArray): AuthResult<AuthUser>

    /**
     * Registers a new user account.
     */
    suspend fun signUp(fullName: String, email: String, password: CharArray): AuthResult<AuthUser>

    /**
     * Initiates an unauthenticated guest session.
     */
    suspend fun continueAsGuest(): AuthResult<AuthUser>

    /**
     * Signs out the currently authenticated user and clears the session.
     * Guaranteed NOT to purge local SQLite document records.
     */
    suspend fun signOut(): AuthResult<Unit>

    /**
     * Sends a password reset link to the given email address.
     * Guaranteed not to leak account existence.
     */
    suspend fun sendPasswordReset(email: String): AuthResult<Unit>

    /**
     * Sends an email verification message to the current user's email.
     */
    suspend fun sendEmailVerification(): AuthResult<Unit>

    /**
     * Refreshes and checks if the current user's email is verified.
     */
    suspend fun checkEmailVerified(): AuthResult<Boolean>

    /**
     * Attempts to restore a persisted session on startup.
     */
    suspend fun restoreSession(): AuthResult<AuthUser?>

    /**
     * For testing/debug: allows setting a simulated network availability state.
     */
    fun setNetworkAvailable(isAvailable: Boolean)
}
