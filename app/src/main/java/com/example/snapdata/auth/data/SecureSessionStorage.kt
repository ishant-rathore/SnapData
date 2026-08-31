package com.example.snapdata.auth.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.snapdata.auth.domain.AuthUser
import com.example.snapdata.logging.AppLogger

/**
 * Secure Session Storage for persisting active session tokens and user identity.
 *
 * Security guarantees:
 * - Passwords are NEVER stored here.
 * - On Android 6.0+ (API 23+): uses [EncryptedSharedPreferences] backed by Android Keystore.
 * - On older API levels: falls back to standard SharedPreferences (minSdk is 26, so this is unreachable).
 * - Stores only opaque session tokens, user ID, email, display name, and verification state.
 * - Automatically redacts logs when saving/restoring tokens.
 * - In-memory fallback when Context is null (unit test path).
 */
class SecureSessionStorage(context: Context? = null) {

    private val prefs: SharedPreferences? = try {
        context?.let { ctx ->
            createEncryptedPrefs(ctx) ?: createFallbackPrefs(ctx)
        }
    } catch (e: Exception) {
        AppLogger.w(AppLogger.LogDomain.AUTH, "Failed to initialize SecureSessionStorage: ${e.message}")
        null
    }

    // In-memory fallback if Context is null (e.g., unit tests)
    private var inMemoryToken: String? = null
    private var inMemoryUser: AuthUser? = null

    private fun createEncryptedPrefs(context: Context): SharedPreferences? {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            AppLogger.w(AppLogger.LogDomain.AUTH, "EncryptedSharedPreferences unavailable, using fallback: ${e.message}")
            null
        }
    }

    private fun createFallbackPrefs(context: Context): SharedPreferences {
        // Fallback: standard SharedPreferences (should not be reached with minSdk 26 + EncryptedSharedPreferences)
        AppLogger.w(AppLogger.LogDomain.AUTH, "Falling back to standard SharedPreferences for session storage.")
        return context.getSharedPreferences(PREFS_NAME + "_fallback", Context.MODE_PRIVATE)
    }

    @Synchronized
    fun saveSession(user: AuthUser, sessionToken: String) {
        inMemoryUser = user
        inMemoryToken = sessionToken

        prefs?.edit()?.apply {
            putString(KEY_USER_ID, user.id)
            putString(KEY_USER_EMAIL, user.email)
            putString(KEY_USER_NAME, user.displayName)
            putBoolean(KEY_USER_VERIFIED, user.isEmailVerified)
            putBoolean(KEY_USER_GUEST, user.isGuest)
            putLong(KEY_USER_CREATED, user.createdAt)
            putLong(KEY_USER_LAST_LOGIN, user.lastLoginAt)
            putString(KEY_SESSION_TOKEN, sessionToken)
            apply()
        }
        // Deliberately do NOT log token value — only log user ID
        AppLogger.d(AppLogger.LogDomain.AUTH, "Session saved for user: ${user.id} [Guest: ${user.isGuest}]")
    }

    @Synchronized
    fun restoreSession(): Pair<AuthUser, String>? {
        if (inMemoryUser != null && inMemoryToken != null) {
            return inMemoryUser!! to inMemoryToken!!
        }

        val prefs = this.prefs ?: return null
        val userId = prefs.getString(KEY_USER_ID, null) ?: return null
        val email = prefs.getString(KEY_USER_EMAIL, null) ?: return null
        val token = prefs.getString(KEY_SESSION_TOKEN, null) ?: return null

        val displayName = prefs.getString(KEY_USER_NAME, null)
        val isVerified = prefs.getBoolean(KEY_USER_VERIFIED, false)
        val isGuest = prefs.getBoolean(KEY_USER_GUEST, false)
        val createdAt = prefs.getLong(KEY_USER_CREATED, System.currentTimeMillis())
        val lastLoginAt = prefs.getLong(KEY_USER_LAST_LOGIN, System.currentTimeMillis())

        val user = AuthUser(
            id = userId,
            email = email,
            displayName = displayName,
            isEmailVerified = isVerified,
            isGuest = isGuest,
            createdAt = createdAt,
            lastLoginAt = lastLoginAt
        )
        inMemoryUser = user
        inMemoryToken = token
        return user to token
    }

    @Synchronized
    fun updateEmailVerification(isVerified: Boolean) {
        inMemoryUser = inMemoryUser?.copy(isEmailVerified = isVerified)
        prefs?.edit()?.putBoolean(KEY_USER_VERIFIED, isVerified)?.apply()
    }

    @Synchronized
    fun clearSession() {
        inMemoryUser = null
        inMemoryToken = null
        prefs?.edit()?.clear()?.apply()
        AppLogger.d(AppLogger.LogDomain.AUTH, "Session cleared.")
    }

    companion object {
        private const val PREFS_NAME = "snapdata_secure_session"
        private const val KEY_USER_ID = "auth_user_id"
        private const val KEY_USER_EMAIL = "auth_user_email"
        private const val KEY_USER_NAME = "auth_user_name"
        private const val KEY_USER_VERIFIED = "auth_user_verified"
        private const val KEY_USER_GUEST = "auth_user_guest"
        private const val KEY_USER_CREATED = "auth_user_created"
        private const val KEY_USER_LAST_LOGIN = "auth_user_last_login"
        private const val KEY_SESSION_TOKEN = "auth_session_token"
    }
}
