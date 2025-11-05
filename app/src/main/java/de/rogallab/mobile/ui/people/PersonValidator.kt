package de.rogallab.mobile.ui.people

import android.content.Context
import android.util.Patterns
import de.rogallab.mobile.R
import de.rogallab.mobile.ui.base.validateString

class PersonValidator(
   private val _context: Context
) {
   private val _min: Int by lazy {
      _context.getString(R.string.errorCharMin).toInt() }
   private val _max: Int by lazy {
      _context.getString(R.string.errorCharMax).toInt() }

   private val _firstNameTooShort: String by lazy {
      _context.getString(R.string.errorFirstNameTooShort) }
   private val _firstNameTooLong: String by lazy {
      _context.getString(R.string.errorFirstNameTooLong) }
   private val _lastNameTooShort: String by lazy {
      _context.getString(R.string.errorLastNameTooShort) }
   private val _lastNameTooLong: String by lazy {
      _context.getString(R.string.errorLastNameTooLong) }
   private val _emailInValid: String by lazy {
      _context.getString(R.string.errorEmail) }
   private val _phoneInValid: String by lazy {
      _context.getString(R.string.errorPhone) }

   fun validateFirstName(firstName: String): Pair<Boolean, String> =
      validateString(firstName, _min, _max,_firstNameTooShort, _firstNameTooLong)

   fun validateLastName(lastName: String): Pair<Boolean, String> =
      validateString(lastName, _min, _max,_lastNameTooShort, _lastNameTooLong)

   // isNullOrEmpty() checks if a String? is null or has a length of 0.
   // isNullOrBlank() checks if a String? is null, empty, or consists only of whitespace characters (spaces, tabs, etc.).
   fun validateEmail(email: String?): Pair<Boolean, String> {
      if(email.isNullOrBlank()) return Pair(false, "")
      return when (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
         true -> Pair(false, "") // email ok
         false -> Pair(true, _emailInValid)
      }
   }

   fun validatePhone(phone: String?): Pair<Boolean, String> {
      if(phone.isNullOrBlank()) return Pair(false,"")
      return when (Patterns.PHONE.matcher(phone).matches()) {
         true -> Pair(false,"")   // phone ok
         false -> Pair(true, _phoneInValid)
      }
   }
}