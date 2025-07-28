package de.rogallab.mobile.ui.people.composables.input_detail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.rogallab.mobile.R
import de.rogallab.mobile.ui.people.PersonIntent
import de.rogallab.mobile.ui.people.PersonUiState
import de.rogallab.mobile.ui.people.PersonValidator
import de.rogallab.mobile.ui.people.PersonViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun PersonDetailScreen(
   id: String,
   viewModel: PersonViewModel = koinViewModel()
) {
   // Observe the PersonUIStateFlow of the viewmodel
   val personUiState: PersonUiState by viewModel.personUiStateFlow.collectAsStateWithLifecycle()

   // fetch person by id
   LaunchedEffect(id) {
      viewModel.onProcessPersonIntent(PersonIntent.FetchById(id))
   }

   PersonContent(
      title = stringResource(R.string.personDetail),
      personUiState = personUiState,
      validator = koinInject<PersonValidator>(),
      onFirstNameChange = { viewModel.onProcessPersonIntent(PersonIntent.FirstNameChange(it)) },
      onLastNameChange = { viewModel.onProcessPersonIntent(PersonIntent.LastNameChange(it)) },
      onEmailChange = { viewModel.onProcessPersonIntent(PersonIntent.EmailChange(it)) },
      onPhoneChange = { viewModel.onProcessPersonIntent(PersonIntent.PhoneChange(it)) },
   )
}