package com.example.snapdata.auth.data

import com.example.snapdata.auth.domain.*
import com.example.snapdata.logging.AppLogger
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Firebase Authentication Provider for SnapData.
 *
 * Implements [AuthenticationProvider] using Firebase Auth SDK, providing:
 * - Real account creation and sign-in via Firebase
 * - Real email verification via Firebase
 * - Real password reset via Firebase
 * - Session persistence via Firebase ID tokens + [SecureSessionStorage]
 * - Automatic token refresh on session restore
 * - Guest mode: remains local (no Firebase account required for offline use)
 * - Strict offline-first: guest/offline processing NEVER blocked by Firebase
 *
 * Architecture:
 * This class is a drop-in replacement for [ProductionAuthProvider] via the
 * [AuthenticationProvider] interface. No UI or ViewModel changes required.
 */
class FirebaseAuthProvider(
    private val sessionStorage: SecureSessionStorage
) : AuthenticationProvider {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unknown)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var _currentUser: AuthUser? = null
    override val currentUser: AuthUser? get() = _currentUser

    // Network availability (updated by ConnectivityManager observer in ViewModel)
    private var isNetworkAvailable = true

    override fun setNetworkAvailable(isAvailable: Boolean) {
        isNetworkAvailable = isAvailable
    }

    // ---------------------------------------------------------------------------
    // Session restore
    // ---------------------------------------------------------------------------

    override suspend fun restoreSession(): AuthResult<AuthUser?> = withContext(Dispatchers.IO) {
        _authState.value = AuthState.Authenticating("Checking session...")
        return@withContext try {
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                // Refresh the token to ensure it's still valid
                firebaseUser.reload().await()
                val authUser = firebaseUser.toAuthUser()
                _currentUser = authUser
                _authState.value = AuthState.Authenticated(authUser)
                sessionStorage.saveSession(authUser, firebaseUser.uid)
                AppLogger.i(AppLogger.LogDomain.AUTH, "Firebase session restored for: ${authUser.id}")
                AuthResult.Success(authUser)
            } else {
                // Check local session for guest users (guest sessions have no Firebase account)
                val localSession = sessionStorage.restoreSession()
                if (localSession != null && localSession.first.isGuest) {
                    val guestUser = localSession.first
                    _currentUser = guestUser
                    _authState.value = AuthState.Authenticated(guestUser)
                    AppLogger.i(AppLogger.LogDomain.AUTH, "Local guest session restored: ${guestUser.id}")
                    AuthResult.Success(guestUser)
                } else {
                    _currentUser = null
                    _authState.value = AuthState.Unauthenticated()
                    sessionStorage.clearSession()
                    AppLogger.i(AppLogger.LogDomain.AUTH, "No active session on startup.")
                    AuthResult.Success(null)
                }
            }
        } catch (e: Exception) {
            _currentUser = null
            _authState.value = AuthState.Unauthenticated()
            AppLogger.w(AppLogger.LogDomain.AUTH, "Session restore failed: ${e.message}")
            AuthResult.Success(null) // Treat restore failure as unauthenticated, not error
        }
    }

    // ---------------------------------------------------------------------------
    // Sign In
    // ---------------------------------------------------------------------------

    override suspend fun signIn(email: String, password: CharArray): AuthResult<AuthUser> {
        val sanitizedEmail = email.trim().lowercase()
        if (sanitizedEmail.isBlank()) return AuthResult.Error(AppAuthError.EmptyEmail)
        if (password.isEmpty()) return AuthResult.Error(AppAuthError.EmptyPassword)
        if (!isNetworkAvailable) return AuthResult.Error(AppAuthError.NetworkUnavailable)

        _authState.value = AuthState.Authenticating("Signing in...")
        return withContext(Dispatchers.IO) {
            try {
                val passwordStr = String(password)
                val result = firebaseAuth.signInWithEmailAndPassword(sanitizedEmail, passwordStr).await()
                val firebaseUser = result.user
                    ?: return@withContext AuthResult.Error(AppAuthError.InvalidCredentials)

                val authUser = firebaseUser.toAuthUser()
                _currentUser = authUser
                sessionStorage.saveSession(authUser, firebaseUser.uid)
                _authState.value = AuthState.Authenticated(authUser)
                AppLogger.i(AppLogger.LogDomain.AUTH, "Firebase sign-in successful: ${authUser.id}")
                AuthResult.Success(authUser)
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                _authState.value = AuthState.Error(AppAuthError.InvalidCredentials)
                AuthResult.Error(AppAuthError.InvalidCredentials)
            } catch (e: FirebaseAuthInvalidUserException) {
                _authState.value = AuthState.Error(AppAuthError.InvalidCredentials)
                AuthResult.Error(AppAuthError.InvalidCredentials)
            } catch (e: FirebaseAuthException) {
                val error = mapFirebaseError(e)
                _authState.value = AuthState.Error(error)
                AuthResult.Error(error)
            } catch (e: Exception) {
                val error = if (isNetworkError(e)) AppAuthError.NetworkUnavailable
                            else AppAuthError.ProviderFailure(e.message ?: "UNKNOWN")
                _authState.value = AuthState.Error(error)
                AuthResult.Error(error)
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Sign Up
    // ---------------------------------------------------------------------------

    override suspend fun signUp(
        fullName: String,
        email: String,
        password: CharArray
    ): AuthResult<AuthUser> {
        val sanitizedEmail = email.trim().lowercase()
        val sanitizedName = fullName.trim()
        if (sanitizedEmail.isBlank()) return AuthResult.Error(AppAuthError.EmptyEmail)
        if (password.isEmpty()) return AuthResult.Error(AppAuthError.EmptyPassword)
        if (!ProductionAuthProvider.isStrongPassword(password)) return AuthResult.Error(AppAuthError.WeakPassword)
        if (!isNetworkAvailable) return AuthResult.Error(AppAuthError.NetworkUnavailable)

        _authState.value = AuthState.Authenticating("Creating account...")
        return withContext(Dispatchers.IO) {
            try {
                val passwordStr = String(password)
                val result = firebaseAuth.createUserWithEmailAndPassword(sanitizedEmail, passwordStr).await()
                val firebaseUser = result.user
                    ?: return@withContext AuthResult.Error(AppAuthError.AccountCreationDisabledOrUnavailable)

                // Update display name if provided
                if (sanitizedName.isNotBlank()) {
                    val profileUpdate = UserProfileChangeRequest.Builder()
                        .setDisplayName(sanitizedName)
                        .build()
                    firebaseUser.updateProfile(profileUpdate).await()
                }

                val authUser = firebaseUser.toAuthUser().let {
                    if (sanitizedName.isNotBlank()) it.copy(displayName = sanitizedName) else it
                }
                _currentUser = authUser
                sessionStorage.saveSession(authUser, firebaseUser.uid)
                _authState.value = AuthState.Authenticated(authUser)

                // Send verification email
                sendEmailVerification()

                AppLogger.i(AppLogger.LogDomain.AUTH, "Firebase account created: ${authUser.id}")
                AuthResult.Success(authUser)
            } catch (e: FirebaseAuthWeakPasswordException) {
                _authState.value = AuthState.Error(AppAuthError.WeakPassword)
                AuthResult.Error(AppAuthError.WeakPassword)
            } catch (e: FirebaseAuthUserCollisionException) {
                // Generic error to prevent email enumeration
                _authState.value = AuthState.Error(AppAuthError.AccountCreationDisabledOrUnavailable)
                AuthResult.Error(AppAuthError.AccountCreationDisabledOrUnavailable)
            } catch (e: FirebaseAuthException) {
                val error = mapFirebaseError(e)
                _authState.value = AuthState.Error(error)
                AuthResult.Error(error)
            } catch (e: Exception) {
                val error = if (isNetworkError(e)) AppAuthError.NetworkUnavailable
                            else AppAuthError.ProviderFailure(e.message ?: "UNKNOWN")
                _authState.value = AuthState.Error(error)
                AuthResult.Error(error)
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Guest Mode (local only — no Firebase account)
    // ---------------------------------------------------------------------------

    override suspend fun continueAsGuest(): AuthResult<AuthUser> {
        _authState.value = AuthState.Authenticating("Initializing guest session...")

        val guestId = "guest_" + java.util.UUID.randomUUID().toString().take(8)
        val guestUser = AuthUser(
            id = guestId,
            email = "$guestId@local.snapdata",
            displayName = "Guest User",
            isEmailVerified = true,
            isGuest = true,
            createdAt = System.currentTimeMillis(),
            lastLoginAt = System.currentTimeMillis()
        )

        _currentUser = guestUser
        sessionStorage.saveSession(guestUser, guestId)
        _authState.value = AuthState.Authenticated(guestUser)

        AppLogger.i(AppLogger.LogDomain.AUTH, "Guest session active: ${guestUser.id}")
        return AuthResult.Success(guestUser)
    }

    // ---------------------------------------------------------------------------
    // Sign Out
    // ---------------------------------------------------------------------------

    override suspend fun signOut(): AuthResult<Unit> {
        _authState.value = AuthState.Authenticating("Signing out...")
        return withContext(Dispatchers.IO) {
            try {
                if (firebaseAuth.currentUser != null) {
                    firebaseAuth.signOut()
                }
                _currentUser = null
                sessionStorage.clearSession()
                _authState.value = AuthState.Unauthenticated()
                AppLogger.i(AppLogger.LogDomain.AUTH, "Signed out. Local documents preserved.")
                AuthResult.Success(Unit)
            } catch (e: Exception) {
                // Sign out should always succeed locally even if Firebase call fails
                _currentUser = null
                sessionStorage.clearSession()
                _authState.value = AuthState.Unauthenticated()
                AuthResult.Success(Unit)
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Password Reset
    // ---------------------------------------------------------------------------

    override suspend fun sendPasswordReset(email: String): AuthResult<Unit> {
        val sanitizedEmail = email.trim().lowercase()
        if (sanitizedEmail.isBlank()) return AuthResult.Error(AppAuthError.EmptyEmail)
        if (!isValidEmail(sanitizedEmail)) return AuthResult.Error(AppAuthError.InvalidEmailFormat)
        if (!isNetworkAvailable) return AuthResult.Error(AppAuthError.NetworkUnavailable)

        return withContext(Dispatchers.IO) {
            try {
                firebaseAuth.sendPasswordResetEmail(sanitizedEmail).await()
                AppLogger.i(AppLogger.LogDomain.AUTH, "Firebase password reset email dispatched.")
                AuthResult.Success(Unit)
            } catch (e: Exception) {
                // Return generic success to prevent email enumeration — Firebase handles delivery
                AppLogger.w(AppLogger.LogDomain.AUTH, "Password reset request processed (generic response).")
                AuthResult.Success(Unit)
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Email Verification
    // ---------------------------------------------------------------------------

    override suspend fun sendEmailVerification(): AuthResult<Unit> {
        val firebaseUser = firebaseAuth.currentUser
            ?: return AuthResult.Error(AppAuthError.InvalidCredentials)
        if (!isNetworkAvailable) return AuthResult.Error(AppAuthError.NetworkUnavailable)

        return withContext(Dispatchers.IO) {
            try {
                firebaseUser.sendEmailVerification().await()
                AppLogger.i(AppLogger.LogDomain.AUTH, "Firebase verification email sent.")
                AuthResult.Success(Unit)
            } catch (e: FirebaseAuthException) {
                val error = mapFirebaseError(e)
                AppLogger.w(AppLogger.LogDomain.AUTH, "Send verification failed: ${e.errorCode}")
                AuthResult.Error(error)
            } catch (e: Exception) {
                val error = if (isNetworkError(e)) AppAuthError.NetworkUnavailable
                            else AppAuthError.ProviderFailure()
                AuthResult.Error(error)
            }
        }
    }

    override suspend fun checkEmailVerified(): AuthResult<Boolean> {
        val firebaseUser = firebaseAuth.currentUser
            ?: return AuthResult.Error(AppAuthError.InvalidCredentials)

        return withContext(Dispatchers.IO) {
            try {
                // Reload forces Firebase to fetch latest verification state from server
                firebaseUser.reload().await()
                val isVerified = firebaseAuth.currentUser?.isEmailVerified ?: false
                if (isVerified) {
                    val updatedUser = _currentUser?.copy(isEmailVerified = true)
                    _currentUser = updatedUser
                    updatedUser?.let {
                        sessionStorage.updateEmailVerification(true)
                        _authState.value = AuthState.Authenticated(it)
                    }
                }
                AppLogger.i(AppLogger.LogDomain.AUTH, "Email verification state: $isVerified")
                AuthResult.Success(isVerified)
            } catch (e: Exception) {
                AppLogger.w(AppLogger.LogDomain.AUTH, "Failed to check email verification: ${e.message}")
                AuthResult.Error(AppAuthError.ProviderFailure())
            }
        }
    }

    // ---------------------------------------------------------------------------
    // Private helpers
    // ---------------------------------------------------------------------------

    private fun com.google.firebase.auth.FirebaseUser.toAuthUser() = AuthUser(
        id = uid,
        email = email ?: "",
        displayName = displayName,
        isEmailVerified = isEmailVerified,
        isGuest = false,
        createdAt = metadata?.creationTimestamp ?: System.currentTimeMillis(),
        lastLoginAt = metadata?.lastSignInTimestamp ?: System.currentTimeMillis()
    )

    private fun mapFirebaseError(e: FirebaseAuthException): AppAuthError {
        return when (e.errorCode) {
            "ERROR_TOO_MANY_REQUESTS" -> AppAuthError.TooManyAttempts
            "ERROR_NETWORK_REQUEST_FAILED" -> AppAuthError.NetworkUnavailable
            "ERROR_USER_DISABLED" -> AppAuthError.AccountCreationDisabledOrUnavailable
            "ERROR_EMAIL_ALREADY_IN_USE" -> AppAuthError.AccountCreationDisabledOrUnavailable
            else -> AppAuthError.ProviderFailure(e.errorCode)
        }
    }

    private fun isNetworkError(e: Exception): Boolean {
        val msg = e.message?.lowercase() ?: ""
        return msg.contains("network") || msg.contains("connection") ||
               msg.contains("timeout") || msg.contains("unable to resolve host")
    }

    private fun isValidEmail(email: String): Boolean {
        return Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$").matches(email)
    }
}
