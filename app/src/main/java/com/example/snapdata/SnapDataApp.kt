package com.example.snapdata

import android.app.Application
import com.example.snapdata.logging.AppLogger
import com.example.snapdata.util.TempFileCleanupManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class SnapDataApp : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        AppLogger.isDebugEnabled = BuildConfig.DEBUG
        AppLogger.i(AppLogger.LogDomain.APP, "SnapData initialized successfully. Offline-ready.")

        // Async lifecycle cleanup of stale cache & temporary files on launch
        appScope.launch {
            TempFileCleanupManager.cleanupStaleTempFiles(this@SnapDataApp)
        }

        // Safety catch for any unhandled thread exceptions to prevent hard OS-level crash dialogs
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            AppLogger.e(AppLogger.LogDomain.APP, "Uncaught exception on thread ${thread.name}: ${throwable.localizedMessage}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
