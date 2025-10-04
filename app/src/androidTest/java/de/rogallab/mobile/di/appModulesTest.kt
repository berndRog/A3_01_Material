package de.rogallab.mobile.di

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import de.rogallab.mobile.data.IDataStore
import de.rogallab.mobile.data.local.appstorage.AppStorage
import de.rogallab.mobile.data.local.datastore.DataStore
import de.rogallab.mobile.data.repositories.PersonRepository
import de.rogallab.mobile.domain.IAppStorage
import de.rogallab.mobile.domain.IPersonRepository
import de.rogallab.mobile.domain.utilities.logInfo
import de.rogallab.mobile.ui.people.PersonValidator
import de.rogallab.mobile.ui.people.PersonViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import kotlin.random.Random

val defModulesTest: Module = module {
   val tag = "<-defModulesTest"

   logInfo(tag, "single    -> InstrumentationRegistry.getInstrumentation().targetContext")
   single<Context> {
      InstrumentationRegistry.getInstrumentation().targetContext
   }

   logInfo(tag, "single    -> DataStore: IDataStore")
   single<IAppStorage> {
      AppStorage( _context = androidContext() )
   }

   // use factory to get a new instance each time (to avoid data conflicts in tests)
   logInfo(tag, "single    -> DataStore")
   val randomText = Random.nextInt(1,1_000_000).toString()
   val timestamp = System.currentTimeMillis()
   single<IDataStore> {
      DataStore(
         _context = get<Context>(),
         _appStorage = get<IAppStorage>(),
         directoryName = "androidTest",
         fileName = "people_${timestamp}_$randomText",
         _isTest = true
      )
   }

   logInfo(tag, "single    -> PersonRepository: IPersonRepository")
   single<IPersonRepository> {
      PersonRepository(
         _dataStore = get<IDataStore>()  // dependency injection of DataStore
      )
   }

   logInfo(tag, "single    -> PersonValidator")
   single<PersonValidator> {
      PersonValidator(
         _context = androidContext()
      )
   }

   logInfo(tag, "viewModel -> PersonViewModel")
   viewModel {
      PersonViewModel(
         _repository = get<IPersonRepository>(),
         _validator = get<PersonValidator>()
      )
   }
}