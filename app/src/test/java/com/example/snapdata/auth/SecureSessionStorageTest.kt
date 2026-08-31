package com.example.snapdata.auth

import com.example.snapdata.auth.data.SecureSessionStorage
import com.example.snapdata.auth.domain.AuthUser
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for SecureSessionStorage.
 * Uses the in-memory fallback path (no Android Context needed for unit tests).
 */
class SecureSessionStorageTest {

    private lateinit var storage: SecureSessionStorage

    @Before
    fun setUp() {
        storage = SecureSessionStorage() // no Context → uses in-memory fallback
    }

    private fun makeUser(
        id: String = "usr_test123",
        email: String = "test@example.com",
        displayName: String? = "Test User",
        isEmailVerified: Boolean = true,
        isGuest: Boolean = false
    ) = AuthUser(
        id = id,
        email = email,
        displayName = displayName,
        isEmailVerified = isEmailVerified,
        isGuest = isGuest,
        createdAt = 1000L,
        lastLoginAt = 2000L
    )

    // ---------------------------------------------------------------------------
    // Save and restore
    // ---------------------------------------------------------------------------

    @Test
    fun `saveSession and restoreSession returns the same user and token`() {
        val user = makeUser()
        val token = "snd_sess_abc123"
        storage.saveSession(user, token)
        val restored = storage.restoreSession()
        assertNotNull("Restored session should not be null", restored)
        assertEquals("User ID should match", user.id, restored!!.first.id)
        assertEquals("Email should match", user.email, restored.first.email)
        assertEquals("Token should match", token, restored.second)
    }

    @Test
    fun `restoreSession returns null when no session was saved`() {
        val result = storage.restoreSession()
        assertNull("Fresh storage should have no session", result)
    }

    @Test
    fun `restoreSession preserves email verification state true`() {
        val user = makeUser(isEmailVerified = true)
        storage.saveSession(user, "token")
        val restored = storage.restoreSession()
        assertTrue("Email verified state should be preserved", restored!!.first.isEmailVerified)
    }

    @Test
    fun `restoreSession preserves email verification state false`() {
        val user = makeUser(isEmailVerified = false)
        storage.saveSession(user, "token")
        val restored = storage.restoreSession()
        assertFalse("Unverified state should be preserved", restored!!.first.isEmailVerified)
    }

    @Test
    fun `restoreSession preserves guest flag`() {
        val user = makeUser(isGuest = true)
        storage.saveSession(user, "token")
        val restored = storage.restoreSession()
        assertTrue("Guest flag should be preserved", restored!!.first.isGuest)
    }

    @Test
    fun `restoreSession preserves non-guest flag`() {
        val user = makeUser(isGuest = false)
        storage.saveSession(user, "token")
        val restored = storage.restoreSession()
        assertFalse("Non-guest flag should be preserved", restored!!.first.isGuest)
    }

    // ---------------------------------------------------------------------------
    // Clear session
    // ---------------------------------------------------------------------------

    @Test
    fun `clearSession makes restoreSession return null`() {
        val user = makeUser()
        storage.saveSession(user, "token")
        storage.clearSession()
        val result = storage.restoreSession()
        assertNull("Session should be null after clearing", result)
    }

    @Test
    fun `clearSession is idempotent when called multiple times`() {
        storage.clearSession()
        storage.clearSession() // should not throw
        assertNull("Session should still be null", storage.restoreSession())
    }

    // ---------------------------------------------------------------------------
    // Update email verification
    // ---------------------------------------------------------------------------

    @Test
    fun `updateEmailVerification to true is reflected on next restore`() {
        val user = makeUser(isEmailVerified = false)
        storage.saveSession(user, "token")
        storage.updateEmailVerification(true)
        val restored = storage.restoreSession()
        assertTrue("Email should now be verified", restored!!.first.isEmailVerified)
    }

    @Test
    fun `updateEmailVerification to false is reflected on next restore`() {
        val user = makeUser(isEmailVerified = true)
        storage.saveSession(user, "token")
        storage.updateEmailVerification(false)
        val restored = storage.restoreSession()
        assertFalse("Email should now be unverified", restored!!.first.isEmailVerified)
    }

    // ---------------------------------------------------------------------------
    // Overwrite session
    // ---------------------------------------------------------------------------

    @Test
    fun `saving a second session overwrites the first`() {
        val user1 = makeUser(id = "usr_001", email = "first@example.com")
        val user2 = makeUser(id = "usr_002", email = "second@example.com")
        storage.saveSession(user1, "token1")
        storage.saveSession(user2, "token2")
        val restored = storage.restoreSession()
        assertEquals("Second user should overwrite first", user2.id, restored!!.first.id)
        assertEquals("Second token should overwrite first", "token2", restored.second)
    }

    // ---------------------------------------------------------------------------
    // Passwords are never stored
    // ---------------------------------------------------------------------------

    @Test
    fun `storage does not expose any password-related data`() {
        val user = makeUser()
        storage.saveSession(user, "session_token_xyz")
        val restored = storage.restoreSession()
        // The AuthUser model has no password field — verify the interface is clean
        val userFields = restored!!.first.javaClass.declaredFields.map { it.name }
        assertFalse("User model must not have password field", userFields.any { it.contains("password", ignoreCase = true) })
        assertFalse("User model must not have hash field", userFields.any { it.contains("hash", ignoreCase = true) })
    }
}
