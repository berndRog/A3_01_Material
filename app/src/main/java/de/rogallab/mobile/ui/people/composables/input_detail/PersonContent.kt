package de.rogallab.mobile.ui.people.composables.input_detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContent
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeGestures
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.unit.dp
import de.rogallab.mobile.ui.people.PersonUiState
import de.rogallab.mobile.ui.people.PersonValidator
import de.rogallab.mobile.R
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.ui.people.composables.SelectAndShowImage
import kotlinx.coroutines.channels.Channel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonContent(
   title: String,                                  // State ↓
   personUiState: PersonUiState,                   // State ↓
   validator: PersonValidator,                     // Value ↓
   onFirstNameChange: (String) -> Unit,            // Event ↑
   onLastNameChange: (String) -> Unit,             // Event ↑
   onEmailChange: (String) -> Unit,                // Event ↑
   onPhoneChange: (String) -> Unit,                // Event ↑
) {
   val tag = "<-PersonContent"

   Scaffold(
      contentColor = MaterialTheme.colorScheme.onBackground,
      contentWindowInsets = WindowInsets.safeDrawing, // .safeContent .safeGestures,
      modifier = Modifier.fillMaxSize(),
      topBar = {
         TopAppBar(
            title = { Text(text = title) },
            navigationIcon = {
               IconButton(
                  onClick = {
                     logDebug(tag, "Up navigation clicked")
                  }
               ) {
                  Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                     contentDescription = stringResource(R.string.back))
               }
            }
         )
      }
   ) { innerPadding ->
      Column(
         modifier = Modifier
            .padding(paddingValues = innerPadding)
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .imePadding()
      ) {
         InputName(
            name = personUiState.person.firstName,
            onNameChange = onFirstNameChange,
            label = stringResource(R.string.firstName),
            validateName = validator::validateFirstName,
         )
         InputName(
            name = personUiState.person.lastName,
            onNameChange = onLastNameChange,
            label = stringResource(R.string.lastName),
            validateName = validator::validateLastName,
         )
         InputEmail(
            email = personUiState.person.email ?: "",
            onEmailChange = onEmailChange,
            validateEmail = validator::validateEmail
         )
         InputPhone(
            phone = personUiState.person.phone ?: "",
            onPhoneChange = onPhoneChange,
            validatePhone = validator::validatePhone
         )
         SelectAndShowImage(
            imageUrl = personUiState.person.imagePath,      
            onImageUrlChange = { Unit }
         )
      }
   }
}