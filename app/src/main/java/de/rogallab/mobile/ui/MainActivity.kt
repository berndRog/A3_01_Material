package de.rogallab.mobile.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.SideEffect
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.ui.base.BaseActivity
import de.rogallab.mobile.ui.people.PersonViewModel
import de.rogallab.mobile.ui.people.composables.input_detail.PersonDetailScreen
import de.rogallab.mobile.ui.people.composables.input_detail.PersonInputScreen
import de.rogallab.mobile.ui.people.composables.list.PeopleListScreen
import de.rogallab.mobile.ui.theme.AppTheme
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import kotlin.getValue

class MainActivity : BaseActivity(TAG) {

   // lazy initialization of the ViewModel with koin
   // Activity-scoped ViewModels viewModelStoreOwner = MainActivity
   private val _personViewModel: PersonViewModel by viewModel()

   override fun onCreate(savedInstanceState: Bundle?) {
      super.onCreate(savedInstanceState)
      logDebug(TAG, "_personViewModel=${System.identityHashCode(_personViewModel)}")

      enableEdgeToEdge()
      setContent {

         AppTheme {
//            PersonInputScreen(viewModel = _personViewModel)
//            PersonDetailScreen(
//               id = "db6cee2b-5f90-459a-aabe-876ef80fcd5f",
//               , viewModel = _personViewModel
//            )
            PeopleListScreen(viewModel = _personViewModel)
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

