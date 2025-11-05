package de.rogallab.mobile.ui.people

import androidx.lifecycle.ViewModel
import de.rogallab.mobile.domain.IPersonRepository
import de.rogallab.mobile.domain.entities.Person
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logError
import de.rogallab.mobile.domain.utilities.newUuid
import de.rogallab.mobile.ui.base.updateState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PersonViewModel(
   private val _repository: IPersonRepository,
   private val _validator: PersonValidator
): ViewModel() {

   // region StateFlows and Intent handlers --------------------------------------------------------
   // StateFlow for PeopleListScreen ---------------------------------------------------------------
   private val _peopleUiStateFlow: MutableStateFlow<PeopleUiState> =
      MutableStateFlow(PeopleUiState())
   val peopleUiStateFlow: StateFlow<PeopleUiState> =
      _peopleUiStateFlow.asStateFlow()

   // Transform PeopleIntent into an action
   fun handlePeopleIntent(intent: PeopleIntent) {
      when (intent) {
         is PeopleIntent.Fetch -> fetch()
      }
   }

   // StateFlow for PersonInput-/ PersonDetailScreen -----------------------------------------------
   private val _personUiStateFlow: MutableStateFlow<PersonUiState> =
      MutableStateFlow(PersonUiState())
   val personUiStateFlow: StateFlow<PersonUiState> =
      _personUiStateFlow.asStateFlow()

   // Transform PersonIntent into an action --------------------------------------------------------
   fun handlePersonIntent(intent: PersonIntent) {
      when (intent) {
         is PersonIntent.FirstNameChange -> onFirstNameChange(intent.firstName)
         is PersonIntent.LastNameChange -> onLastNameChange(intent.lastName)
         is PersonIntent.EmailChange -> onEmailChange(intent.email)
         is PersonIntent.PhoneChange -> onPhoneChange(intent.phone)
         is PersonIntent.ImageChange -> onImageChange(intent.uriString)

         is PersonIntent.Clear -> clearState()
         is PersonIntent.FetchById -> fetchById(intent.id)
         is PersonIntent.Create -> create()
         is PersonIntent.Update -> update()
         is PersonIntent.Remove -> remove(intent.person)
      }
   }
   // endregion

   // region Input updates (immutable copy, trimmed) -----------------------------------------------
   private fun onFirstNameChange(firstName: String) =
      updateState(_personUiStateFlow) {
         copy(person = person.copy(firstName = firstName.trim())) }
   private fun onLastNameChange(lastName: String) =
      updateState(_personUiStateFlow) {
         copy(person = person.copy(lastName = lastName.trim())) }

   private fun onEmailChange(email: String?) =
      updateState(_personUiStateFlow) {
         copy(person = person.copy(email = email?.trim())) }
   private fun onPhoneChange(phone: String?) =
      updateState(_personUiStateFlow) {
         copy(person = person.copy(phone = phone?.trim())) }
   private fun onImageChange(uriString: String?) =
      updateState(_personUiStateFlow) {
         copy(person = person.copy(imagePath = uriString?.trim()))
      }

   private fun clearState() =
      updateState(_personUiStateFlow) {
         copy(person = Person(id = newUuid() )) }


   // region Fetch by id (error → navigate back to list) -------------------------------------------
   private fun fetchById(id: String) {
      logDebug(TAG, "fetchById() $id")
      _repository.findById(id)
         .onSuccess { person ->
            if (person != null) {
               updateState(_personUiStateFlow) { copy(person = person) }
            } else {
               logError(TAG, "Person not found")
            }
         }
         .onFailure { logError(TAG, it.message ?: "Error in fetchById") }
   }
   // endregion

   // region Create/Update/Remove (persist then refresh list) --------------------------
   private fun create() {
      logDebug(TAG, "createPerson")
      _repository.create(_personUiStateFlow.value.person)
         .onSuccess { fetch() } // reread all people
         .onFailure { logError(TAG, it.message ?: "Error in create") }
   }

   private fun update() {
      logDebug(TAG, "updatePerson()")
      _repository.update(_personUiStateFlow.value.person)
         .onSuccess { fetch() } // reread all people
         .onFailure { logError(TAG, it.message ?: "Error in update") }
   }

   private fun remove(person: Person) {
      logDebug(TAG, "removePerson()")
      _repository.remove(person)
         .onSuccess { fetch() } // reread all people
         .onFailure { logError(TAG, it.message ?: "Error in remove") }
   }
   // endregion

   // region Validation ----------------------------------------------------------------------------
   // validate all input fields after user finished input into the form
   fun validate(): Boolean {
      val person = _personUiStateFlow.value.person

      // only one error message can be processed at a time
      if(!validateAndLogError(_validator.validateFirstName(person.firstName)))
         return false
      if(!validateAndLogError(_validator.validateLastName(person.lastName)))
         return false
      if(!validateAndLogError(_validator.validateEmail(person.email)))
         return false
      if(!validateAndLogError(_validator.validatePhone(person.phone)))
         return false
      return true // all fields are valid
   }

   private fun validateAndLogError(validationResult: Pair<Boolean, String>): Boolean {
      val (error, message) = validationResult
      if (error) {
         logError(TAG, message)
         return false
      }
      return true
   }
   // endregion

   // region Fetch all (persisted → UI) ------------------------------------------------------------
   // read all people from repository
   private fun fetch() {
      logDebug(TAG, "fetch")
      _repository.getAll()
         .onSuccess { people ->
            val snapshot = people.toList()
            updateState(_peopleUiStateFlow) {
               copy(people = snapshot ) }
         }
         .onFailure { logError(TAG, it.message ?: "Error in fetch") }
   }
   // endregion

   companion object {
      private const val TAG = "<-PersonViewModel"
   }
}