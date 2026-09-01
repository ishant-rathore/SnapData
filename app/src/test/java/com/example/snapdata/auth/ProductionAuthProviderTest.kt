package com.example.snapdata.auth

import com.example.snapdata.auth.data.ProductionAuthProvider
import com.example.snapdata.auth.data.SecureSessionStorage
import com.example.snapdata.auth.domain.AppAuthError
import com.example.snapdata.auth.domain.AuthResult
import com.example.snapdata.auth.domain.AuthState
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for ProductionAuthProvider covering:
 * - Password validation (strength policy)
 * - Rate limiting (anti-brute-force)
 * - Input validation (email format, empty fields)
 * - Session management (sign in, sign out, restore)
 * - Guest mode
 * - Network unavailable handling
 * - Email verification cooldown
 */
class ProductionAuthProviderTest {

    private lateinit var provider: ProductionAuthProvider

    @Before
    fun setUp() {
        // Use in-memory storage (no Context) for unit testing
        provider = ProductionAuthProvider(SecureSessionStorage())
    }

    // ---------------------------------------------------------------------------
    // Password strength validation
    // ---------------------------------------------------------------------------

    @Test
    fun `isStrongPassword rejects empty password`() {
        assertFalse(ProductionAuthProvider.isStrongPassword(charArrayOf()))
    }

    @Test
    fun `isStrongPassword rejects passwords shorter than 8 characters`() {
        assertFalse(ProductionAuthProvider.isStrongPassword("Ab1!".toCharArray()))
    }

    @Test
    fun `isStrongPassword rejects passwords without uppercase`() {
        assertFalse(ProductionAuthProvider.isStrongPassword("abc12345!".toCharArray()))
    }

    @Test
    fun `isStrongPassword rejects passwords without lowercase`() {
        assertFalse(ProductionAuthProvider.isStrongPassword("ABC12345!".toCharArray()))
    }

    @Test
    fun `isStrongPassword rejects passwords without digits`() {
        assertFalse(ProductionAuthProvider.isStrongPassword("Abcdefgh!".toCharArray()))
    }

    @Test
    fun `isStrongPassword rejects passwords without special characters`() {
        assertFalse(ProductionAuthProvider.isStrongPassword("Abcdef12".toCharArray()))
    }

    @Test
    fun `isStrongPassword accepts valid strong password`() {
        assertTrue(ProductionAuthProvider.isStrongPassword("Secure@123".toCharArray()))
    }

    @Test
    fun `isStrongPassword accepts complex passwords`() {
        assertTrue(ProductionAuthProvider.isStrongPassword("P@ssw0rd!#\$".toCharArray()))
    }

    @Test
    fun `isStrongPassword accepts exactly 8 char valid password`() {
        assertTrue(ProductionAuthProvider.isStrongPassword("Ab1!xyzW".toCharArray()))
    }

    // ---------------------------------------------------------------------------
    // Sign-up input validation
    // ---------------------------------------------------------------------------

    @Test
    fun `signUp with empty email returns EmptyEmail error`() = runTest {
        val result = provider.signUp("Aarav Sharma", "", "Secure@123".toCharArray())
        assertTrue(result is AuthResult.Error)
        assertEquals("AUTH_EMAIL_EMPTY", (result as AuthResult.Error).error.errorCode)
    }

    @Test
    fun `signUp with invalid email format returns InvalidEmailFormat error`() = runTest {
        val result = provider.signUp("Aarav Sharma", "not-an-email", "Secure@123".toCharArray())
        assertTrue(result is AuthResult.Error)
        assertEquals("AUTH_EMAIL_INVALID", (result as AuthResult.Error).error.errorCode)
    }

    @Test
    fun `signUp with empty password returns EmptyPassword error`() = runTest {
        val result = provider.signUp("Aarav Sharma", "aarav.sharma@example.in", charArrayOf())
        assertTrue(result is AuthResult.Error)
        assertEquals("AUTH_PASSWORD_EMPTY", (result as AuthResult.Error).error.errorCode)
    }

    @Test
    fun `signUp with weak password returns WeakPassword error`() = runTest {
        val result = provider.signUp("Aarav Sharma", "aarav.sharma@example.in", "password".toCharArray())
        assertTrue(result is AuthResult.Error)
        assertEquals("AUTH_PASSWORD_WEAK", (result as AuthResult.Error).error.errorCode)
    }

    // ---------------------------------------------------------------------------
    // Sign-in input validation
    // ---------------------------------------------------------------------------

    @Test
    fun `signIn with empty email returns EmptyEmail error`() = runTest {
        val result = provider.signIn("", "Secure@123".toCharArray())
        assertTrue(result is AuthResult.Error)
        assertEquals("AUTH_EMAIL_EMPTY", (result as AuthResult.Error).error.errorCode)
    }

    @Test
    fun `signIn with invalid email format returns InvalidEmailFormat error`() = runTest {
        val result = provider.signIn("bad-email", "Secure@123".toCharArray())
        assertTrue(result is AuthResult.Error)
        assertEquals("AUTH_EMAIL_INVALID", (result as AuthResult.Error).error.errorCode)
    }

    @Test
    fun `signIn with empty password returns EmptyPassword error`() = runTest {
        val result = provider.signIn("aarav.sharma@example.in", charArrayOf())
        assertTrue(result is AuthResult.Error)
        assertEquals("AUTH_PASSWORD_EMPTY", (result as AuthResult.Error).error.errorCode)
    }

    @Test
    fun `signIn with non-existent account returns InvalidCredentials`() = runTest {
        val result = provider.signIn("nonexistent@example.in", "Secure@123".toCharArray())
        assertTrue(result is AuthResult.Error)
        assertEquals("AUTH_INVALID_CREDENTIALS", (result as AuthResult.Error).error.errorCode)
    }

    // ---------------------------------------------------------------------------
    // Network unavailable
    // ---------------------------------------------------------------------------

    @Test
    fun `signIn returns NetworkUnavailable when offline`() = runTest {
        provider.setNetworkAvailable(false)
        val result = provider.signIn("aarav.sharma@example.in", "Secure@123".toCharArray())
        assertTrue(result is AuthResult.Error)
        assertEquals("AUTH_NETWORK_UNAVAILABLE", (result as AuthResult.Error).error.errorCode)
    }

    @Test
    fun `signUp returns NetworkUnavailable when offline`() = runTest {
        provider.setNetworkAvailable(false)
        val result = provider.signUp("Aarav Sharma", "aarav.sharma@example.in", "Secure@123".toCharArray())
        assertTrue(result is AuthResult.Error)
        assertEquals("AUTH_NETWORK_UNAVAILABLE", (result as AuthResult.Error).error.errorCode)
    }

    @Test
    fun `sendPasswordReset returns NetworkUnavailable when offline`() = runTest {
        provider.setNetworkAvailable(false)
        val result = provider.sendPasswordReset("aarav.sharma@example.in")
        assertTrue(result is AuthResult.Error)
        assertEquals("AUTH_NETWORK_UNAVAILABLE", (result as AuthResult.Error).error.errorCode)
    }

    // ---------------------------------------------------------------------------
    // Guest mode
    // ---------------------------------------------------------------------------

    @Test
    fun `continueAsGuest succeeds and returns guest user`() = runTest {
        val result = provider.continueAsGuest()
        assertTrue("Guest login should succeed", result is AuthResult.Success)
        val user = (result as AuthResult.Success).data
        assertTrue("User should be marked as guest", user.isGuest)
        assertTrue("Guest email should contain guest marker", user.email.contains("guest"))
    }

    @Test
    fun `continueAsGuest sets auth state to Authenticated`() = runTest {
        provider.continueAsGuest()
        val state = provider.authState.value
        assertTrue("Auth state should be Authenticated after guest login", state is AuthState.Authenticated)
    }

    @Test
    fun `guest user id is unique per session`() = runTest {
        val result1 = provider.continueAsGuest()
        provider.signOut()
        val result2 = provider.continueAsGuest()
        val id1 = (result1 as AuthResult.Success).data.id
        val id2 = (result2 as AuthResult.Success).data.id
        assertNotEquals("Each guest session should have a unique ID", id1, id2)
    }

    // ---------------------------------------------------------------------------
    // Sign out
    // ---------------------------------------------------------------------------

    @Test
    fun `signOut clears current user and sets unauthenticated state`() = runTest {
        provider.continueAsGuest()
        provider.signOut()
        assertNull("Current user should be null after sign out", provider.currentUser)
        assertTrue("Auth state should be Unauthenticated", provider.authState.value is AuthState.Unauthenticated)
    }

    // ---------------------------------------------------------------------------
    // Session restore
    // ---------------------------------------------------------------------------

    @Test
    fun `restoreSession returns null when no session saved`() = runTest {
        val result = provider.restoreSession()
        assertTrue("Result should be success", result is AuthResult.Success)
        assertNull("Data should be null when no session", (result as AuthResult.Success).data)
    }

    // ---------------------------------------------------------------------------
    // Rate limiting
    // ---------------------------------------------------------------------------

    @Test
    fun `rate limiting activates after 5 failed sign-in attempts`() = runTest {
        provider.setNetworkAvailable(true)
        repeat(5) {
            provider.signIn("ratelimit@example.in", "WrongPass@1".toCharArray())
        }
        val result = provider.signIn("ratelimit@example.in", "WrongPass@1".toCharArray())
        assertTrue("Should be rate limited", result is AuthResult.Error)
        assertEquals("AUTH_RATE_LIMITED", (result as AuthResult.Error).error.errorCode)
    }

    // ---------------------------------------------------------------------------
    // Email verification cooldown
    // ---------------------------------------------------------------------------

    @Test
    fun `sendEmailVerification returns error when no user is signed in`() = runTest {
        val result = provider.sendEmailVerification()
        assertTrue("Should return error when not signed in", result is AuthResult.Error)
    }

    // ---------------------------------------------------------------------------
    // Password reset
    // ---------------------------------------------------------------------------

    @Test
    fun `sendPasswordReset with empty email returns EmptyEmail error`() = runTest {
        val result = provider.sendPasswordReset("")
        assertTrue(result is AuthResult.Error)
        assertEquals("AUTH_EMAIL_EMPTY", (result as AuthResult.Error).error.errorCode)
    }

    @Test
    fun `sendPasswordReset with invalid email returns InvalidEmailFormat error`() = runTest {
        val result = provider.sendPasswordReset("not-valid-email")
        assertTrue(result is AuthResult.Error)
        assertEquals("AUTH_EMAIL_INVALID", (result as AuthResult.Error).error.errorCode)
    }

    @Test
    fun `sendPasswordReset with valid email returns success even for non-existent accounts`() = runTest {
        // Must return generic success to prevent account enumeration
        val result = provider.sendPasswordReset("nonexistent@example.in")
        assertTrue("Must return success to prevent email enumeration", result is AuthResult.Success)
    }
}
