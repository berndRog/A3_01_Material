package de.rogallab.mobile.ui.people.composables

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.rogallab.mobile.R
import de.rogallab.mobile.domain.entities.Person
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logInfo
import de.rogallab.mobile.domain.utilities.logVerbose
import de.rogallab.mobile.ui.people.PeopleIntent
import de.rogallab.mobile.ui.people.PersonIntent
import de.rogallab.mobile.ui.people.PersonViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeopleListScreen(
   viewModel: PersonViewModel = koinViewModel()
) {
   val tag = "<-PeopleListScreen"
   SideEffect { logVerbose(tag, "Composition") }

   // observe the peopleUiStateFlow in the ViewModel
   val lifecycle = (LocalActivity.current as? ComponentActivity)?.lifecycle
      ?: LocalLifecycleOwner.current.lifecycle
   val peopleUiState by viewModel.peopleUiStateFlow.collectAsStateWithLifecycle(
      lifecycle = lifecycle,
      minActiveState = Lifecycle.State.STARTED
   )
   LaunchedEffect(peopleUiState.isLoading, peopleUiState.people.size) {
      logDebug(tag, "PeopleUiState: isLoading=${peopleUiState.isLoading} size=${peopleUiState.people.size}")
   }

   LaunchedEffect(Unit) {
      logDebug(tag, "fetch data")
      // Trigger initial data load
      viewModel.handlePeopleIntent(PeopleIntent.Fetch)
   }

   val back = stringResource(R.string.back)

   Scaffold(
      contentColor = MaterialTheme.colorScheme.onBackground,
      contentWindowInsets = WindowInsets.safeDrawing,
      topBar = {
         TopAppBar(
            title = { Text(stringResource(R.string.peopleList)) },
//            colors = TopAppBarDefaults.topAppBarColors(
//               containerColor = MaterialTheme.colorScheme.primaryContainer,
//               titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
//            ),
            navigationIcon = {
               val activity: Activity? = LocalActivity.current
               IconButton(
                  onClick = { logDebug(tag, "Menu navigation clicked")
                               activity?.finish()
                            }) {
                  Icon(
                     imageVector = Icons.Default.Menu,
                     contentDescription = back
                  )
               }
            },
         )
      },
      floatingActionButton = {
         FloatingActionButton(
            containerColor = MaterialTheme.colorScheme.tertiary,
            onClick = {
               logDebug(tag, "FAB clicked")
               viewModel.handlePersonIntent(PersonIntent.Clear) // Reset the person state
            }
         ) {
            Icon(Icons.Default.Add, "Add a contact")
         }
      },
      modifier = Modifier.fillMaxSize(),
   ) { innerPadding ->

      LazyColumn(
         modifier = Modifier
            .padding(innerPadding)
            .padding(horizontal = 20.dp)
            .fillMaxSize()) {
         items(
            items = peopleUiState.people,
            key = { it: Person -> it.id }
         ) { person ->
            logDebug(tag, "Person: ${person.firstName} ${person.lastName}")
            PersonListItem(
               id = person.id,
               firstName = person.firstName,
               lastName = person.lastName,
               email = person.email ?: "",
               phone = person.phone ?: "",
               imagePath = person.imagePath,
               onClicked = {
                  logInfo(tag, "Person clicked: ${person.lastName}")
               },
               onDeleted = {
                  logInfo(tag, "Person deleted: ${person.lastName}")
                  viewModel.handlePersonIntent(PersonIntent.Remove(person))
               }
            )
         }
      }
   }
}