package de.rogallab.mobile

import android.app.Application
import de.rogallab.mobile.data.IDataStore
import de.rogallab.mobile.di.appModules
import de.rogallab.mobile.domain.utilities.logInfo
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import kotlin.getValue

class MainApplication : Application() {

   override fun onCreate() {
      super.onCreate()

      // Initialize Koin dependency injection
      logInfo(TAG, "onCreate(): startKoin{...}")
      startKoin {
         // Use Koin Android Logger
         androidLogger(Level.DEBUG)
         // Reference to Android context
         androidContext(androidContext = this@MainApplication)
         // Load modules
         modules(appModules)
      }

      val _dataStore: IDataStore by inject()
      _dataStore.initialize()

   }

   companion object {
      private const val TAG = "<-MainApplication"
   }
}