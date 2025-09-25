package de.rogallab.mobile.ui.people

import androidx.lifecycle.ViewModel
import de.rogallab.mobile.domain.IPersonRepository
import de.rogallab.mobile.domain.entities.Person
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logError
import de.rogallab.mobile.domain.utilities.newUuid
import de.rogallab.mobile.ui.base.updateState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class PersonViewModel(
   private val _repository: IPersonRepository,
   private val _validator: PersonValidator
): ViewModel() {

   // region PersonInputScreen or PersonDetailScreen
   // StateFlow for PersonInputScreen and PersonDetailScreen
   private val _personUiStateFlow = MutableStateFlow(PersonUiState())
   val personUiStateFlow = _personUiStateFlow.asStateFlow()

   // transform intent into an action
   fun handlePersonIntent(intent: PersonIntent) {
      when (intent) {
         is PersonIntent.FirstNameChange -> onFirstNameChange(intent.firstName)
         is PersonIntent.LastNameChange -> onLastNameChange(intent.lastName)
         is PersonIntent.EmailChange -> onEmailChange(intent.email)
         is PersonIntent.PhoneChange -> onPhoneChange(intent.phone)

         is PersonIntent.Clear -> clearState()
         is PersonIntent.FetchById -> fetchById(intent.id)
         is PersonIntent.Create -> create()
         is PersonIntent.Update -> update()
         is PersonIntent.Remove -> remove(intent.person)
      }
   }

   private fun onFirstNameChange(firstName: String) {
      updateState(_personUiStateFlow) {
         copy(person = person.copy(firstName = firstName.trim())) }
   }
   private fun onLastNameChange(lastName: String) {
      updateState(_personUiStateFlow) {
         copy(person = person.copy(lastName = lastName.trim())) }
   }
   private fun onEmailChange(email: String?) {
      updateState(_personUiStateFlow) {
         copy(person = person.copy(email = email?.trim())) }
   }
   private fun onPhoneChange(phone: String?) {
      updateState(_personUiStateFlow) {
         copy(person = person.copy(phone = phone?.trim())) }
   }

   private fun clearState() {
      updateState(_personUiStateFlow) {
         copy(person = Person(id = newUuid() )) }
   }

   private fun fetchById(id: String) {
      logDebug(TAG, "fetchById() $id")
      _repository.findById(id)
         .onSuccess { person ->
            updateState(_personUiStateFlow) {
               copy(person = person ?: Person()) } // if null, create an empty
         }
         .onFailure { logError(TAG, it.message ?: "Error in fetchById") }
   }

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
   //endregion

   // region Validation
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
   //endregion

   // region PeopleListScreen
   // StateFlow for UI State People
   private val _peopleUiStateFlow = MutableStateFlow(PeopleUiState())
   val peopleUiStateFlow = _peopleUiStateFlow.asStateFlow()

   // transform intent into an action
   fun handlePeopleIntent(intent: PeopleIntent) {
      when (intent) {
         is PeopleIntent.Fetch -> fetch()
      }
   }

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