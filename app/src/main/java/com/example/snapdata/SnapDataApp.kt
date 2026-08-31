package com.example.snapdata

import android.app.Application
import com.example.snapdata.logging.AppLogger

class SnapDataApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppLogger.i(AppLogger.LogDomain.APP, "SnapData initialized successfully. Offline-ready.")

        // Safety catch for any unhandled thread exceptions to prevent hard OS-level crash dialogs
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            AppLogger.e(AppLogger.LogDomain.APP, "Uncaught exception on thread ${thread.name}: ${throwable.localizedMessage}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
    }
}
