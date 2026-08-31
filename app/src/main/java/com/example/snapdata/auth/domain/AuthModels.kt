package com.example.snapdata.auth.domain

/**
 * Immutable authenticated user entity.
 * Contains only identity and session metadata.
 * ZERO document data or OCR/AI extractions are attached to this model.
 */
data class AuthUser(
    val id: String,
    val email: String,
    val displayName: String? = null,
    val isEmailVerified: Boolean = false,
    val isGuest: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLoginAt: Long = System.currentTimeMillis()
)

/**
 * Formal session state machine for SnapData.
 */
sealed interface AuthState {
    /**
     * Initial startup state while checking for persisted session tokens.
     */
    data object Unknown : AuthState

    /**
     * In-flight authentication operation (Sign in, Sign up, Reset, etc.).
     */
    data class Authenticating(val operation: String = "Authenticating...") : AuthState

    /**
     * Valid active session established.
     */
    data class Authenticated(
        val user: AuthUser,
        val sessionToken: String? = null
    ) : AuthState

    /**
     * No active session (logged out, guest, or initial launch).
     */
    data class Unauthenticated(val message: String? = null) : AuthState

    /**
     * Previously valid session has expired (TTL elapsed or revoked).
     */
    data class Expired(val lastUser: AuthUser? = null) : AuthState

    /**
     * Recoverable authentication error state.
     */
    data class Error(
        val error: AppAuthError,
        val timestamp: Long = System.currentTimeMillis()
    ) : AuthState
}

/**
 * Monadic Result wrapper for authentication operations.
 */
sealed interface AuthResult<out T> {
    data class Success<out T>(val data: T) : AuthResult<T>
    data class Error(val error: AppAuthError) : AuthResult<Nothing>

    fun isSuccess(): Boolean = this is Success
    fun getOrNull(): T? = when (this) {
        is Success -> data
        is Error -> null
    }
}

/**
 * Strict, sanitized error hierarchy for authentication.
 * Guarantees zero sensitive data leakage and prevents account enumeration.
 */
sealed class AppAuthError(
    val userMessage: String,
    val errorCode: String,
    val isRetryable: Boolean = true
) {
    data object InvalidCredentials : AppAuthError(
        userMessage = "Unable to sign in. Check your email and password.",
        errorCode = "AUTH_INVALID_CREDENTIALS"
    )

    data object NetworkUnavailable : AppAuthError(
        userMessage = "Connection is unavailable. Check your internet connection and try again.",
        errorCode = "AUTH_NETWORK_UNAVAILABLE"
    )

    data object TooManyAttempts : AppAuthError(
        userMessage = "Too many attempts. Please try again later.",
        errorCode = "AUTH_RATE_LIMITED",
        isRetryable = false
    )

    data object EmptyEmail : AppAuthError(
        userMessage = "Email address is required.",
        errorCode = "AUTH_EMAIL_EMPTY"
    )

    data object InvalidEmailFormat : AppAuthError(
        userMessage = "Please enter a valid email address.",
        errorCode = "AUTH_EMAIL_INVALID"
    )

    data object EmptyPassword : AppAuthError(
        userMessage = "Password is required.",
        errorCode = "AUTH_PASSWORD_EMPTY"
    )

    data object WeakPassword : AppAuthError(
        userMessage = "Password must be at least 8 characters with uppercase, lowercase, number, and special character.",
        errorCode = "AUTH_PASSWORD_WEAK"
    )

    data object PasswordMismatch : AppAuthError(
        userMessage = "Passwords do not match.",
        errorCode = "AUTH_PASSWORD_MISMATCH"
    )

    data object AccountCreationDisabledOrUnavailable : AppAuthError(
        userMessage = "Unable to create the account with these details.",
        errorCode = "AUTH_REGISTRATION_FAILED"
    )

    data object EmailNotVerified : AppAuthError(
        userMessage = "Your email has not been verified yet.",
        errorCode = "AUTH_EMAIL_UNVERIFIED"
    )

    data object VerificationCooldownActive : AppAuthError(
        userMessage = "Please wait before requesting another verification email.",
        errorCode = "AUTH_COOLDOWN_ACTIVE"
    )

    data object SessionExpired : AppAuthError(
        userMessage = "Your session has expired. Please sign in again.",
        errorCode = "AUTH_SESSION_EXPIRED"
    )

    data class ProviderFailure(val technicalCode: String = "UNKNOWN") : AppAuthError(
        userMessage = "Authentication service is temporarily unavailable. Please try again.",
        errorCode = "AUTH_PROVIDER_FAILURE"
    )

    data class Unknown(val detail: String = "Something went wrong.") : AppAuthError(
        userMessage = "Something went wrong. Please try again.",
        errorCode = "AUTH_UNKNOWN"
    )
}
