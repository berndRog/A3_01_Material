package de.rogallab.mobile.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import de.rogallab.mobile.data.IDataStore
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.ui.base.BaseActivity
import de.rogallab.mobile.ui.people.PersonViewModel
import de.rogallab.mobile.ui.people.composables.PeopleListScreen
import de.rogallab.mobile.ui.people.composables.PersonInputScreen
import de.rogallab.mobile.ui.theme.AppTheme
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : BaseActivity(TAG) {

   // lazy initialization of the ViewModel with koin
   // Activity-scoped ViewModels viewModelStoreOwner = MainActivity
   private val _personViewModel: PersonViewModel by viewModel()
   private val _dataStore: IDataStore by inject()

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      logDebug(TAG, "_personViewModel=${System.identityHashCode(_personViewModel)}")

      _dataStore.initialize()

      enableEdgeToEdge()
      setContent {

         AppTheme {
//            PersonInputScreen(viewModel = _personViewModel)
//            PersonDetailScreen(
//               id = "db6cee2b-5f90-459a-aabe-876ef80fcd5f",
//               , viewModel = _personViewModel
//            )
            PeopleListScreen() //viewModel = _personViewModel)
         }
      }
   }

   companion object {
      private const val TAG = "<-MainActivity"
   }
}


private fun isInTest(): Boolean {
   return try {
      Class.forName("androidx.test.espresso.Espresso")
      true
   } catch (e: ClassNotFoundException) {
      false
   }
}

