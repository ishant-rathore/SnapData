package com.example.snapdata.auth

import com.example.snapdata.auth.domain.*
import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for auth domain models: AuthState, AuthResult, AppAuthError, AuthUser.
 */
class AuthModelsTest {

    // ---------------------------------------------------------------------------
    // AuthResult
    // ---------------------------------------------------------------------------

    @Test
    fun `AuthResult Success isSuccess returns true`() {
        val result: AuthResult<String> = AuthResult.Success("data")
        assertTrue(result.isSuccess())
    }

    @Test
    fun `AuthResult Error isSuccess returns false`() {
        val result: AuthResult<String> = AuthResult.Error(AppAuthError.NetworkUnavailable)
        assertFalse(result.isSuccess())
    }

    @Test
    fun `AuthResult Success getOrNull returns data`() {
        val result: AuthResult<String> = AuthResult.Success("hello")
        assertEquals("hello", result.getOrNull())
    }

    @Test
    fun `AuthResult Error getOrNull returns null`() {
        val result: AuthResult<String> = AuthResult.Error(AppAuthError.EmptyEmail)
        assertNull(result.getOrNull())
    }

    // ---------------------------------------------------------------------------
    // AppAuthError
    // ---------------------------------------------------------------------------

    @Test
    fun `all AppAuthError instances have non-blank userMessage`() {
        val errors = listOf(
            AppAuthError.InvalidCredentials,
            AppAuthError.NetworkUnavailable,
            AppAuthError.TooManyAttempts,
            AppAuthError.EmptyEmail,
            AppAuthError.InvalidEmailFormat,
            AppAuthError.EmptyPassword,
            AppAuthError.WeakPassword,
            AppAuthError.PasswordMismatch,
            AppAuthError.AccountCreationDisabledOrUnavailable,
            AppAuthError.EmailNotVerified,
            AppAuthError.VerificationCooldownActive,
            AppAuthError.SessionExpired,
            AppAuthError.ProviderFailure(),
            AppAuthError.Unknown()
        )
        for (error in errors) {
            assertTrue("userMessage must not be blank for ${error.errorCode}", error.userMessage.isNotBlank())
        }
    }

    @Test
    fun `all AppAuthError instances have non-blank errorCode`() {
        val errors = listOf(
            AppAuthError.InvalidCredentials,
            AppAuthError.NetworkUnavailable,
            AppAuthError.TooManyAttempts,
            AppAuthError.EmptyEmail,
            AppAuthError.InvalidEmailFormat,
            AppAuthError.EmptyPassword,
            AppAuthError.WeakPassword,
            AppAuthError.PasswordMismatch,
            AppAuthError.AccountCreationDisabledOrUnavailable,
            AppAuthError.EmailNotVerified,
            AppAuthError.VerificationCooldownActive,
            AppAuthError.SessionExpired,
            AppAuthError.ProviderFailure(),
            AppAuthError.Unknown()
        )
        for (error in errors) {
            assertTrue("errorCode must not be blank", error.errorCode.isNotBlank())
        }
    }

    @Test
    fun `TooManyAttempts is not retryable`() {
        assertFalse(AppAuthError.TooManyAttempts.isRetryable)
    }

    @Test
    fun `InvalidCredentials is retryable`() {
        assertTrue(AppAuthError.InvalidCredentials.isRetryable)
    }

    @Test
    fun `NetworkUnavailable is retryable`() {
        assertTrue(AppAuthError.NetworkUnavailable.isRetryable)
    }

    // ---------------------------------------------------------------------------
    // AuthUser
    // ---------------------------------------------------------------------------

    @Test
    fun `AuthUser copy preserves all fields`() {
        val user = AuthUser(
            id = "usr_001",
            email = "aarav.sharma@example.in",
            displayName = "Aarav Sharma",
            isEmailVerified = false,
            isGuest = false,
            createdAt = 1000L,
            lastLoginAt = 2000L
        )
        val updated = user.copy(isEmailVerified = true)
        assertEquals("usr_001", updated.id)
        assertEquals("aarav.sharma@example.in", updated.email)
        assertEquals("Aarav Sharma", updated.displayName)
        assertTrue(updated.isEmailVerified)
        assertFalse(updated.isGuest)
    }

    @Test
    fun `AuthUser equality is value-based`() {
        val user1 = AuthUser(id = "usr_001", email = "a@b.com", createdAt = 1000L, lastLoginAt = 2000L)
        val user2 = AuthUser(id = "usr_001", email = "a@b.com", createdAt = 1000L, lastLoginAt = 2000L)
        assertEquals(user1, user2)
    }

    // ---------------------------------------------------------------------------
    // AuthState
    // ---------------------------------------------------------------------------

    @Test
    fun `AuthState Authenticated contains user`() {
        val user = AuthUser(id = "usr_001", email = "aarav.sharma@example.in")
        val state = AuthState.Authenticated(user, "token_abc")
        assertEquals(user, state.user)
        assertEquals("token_abc", state.sessionToken)
    }

    @Test
    fun `AuthState Unauthenticated has no user`() {
        val state = AuthState.Unauthenticated()
        assertNull(state.message)
    }

    @Test
    fun `AuthState Error contains the error`() {
        val error = AppAuthError.NetworkUnavailable
        val state = AuthState.Error(error)
        assertEquals(error, state.error)
    }
}
