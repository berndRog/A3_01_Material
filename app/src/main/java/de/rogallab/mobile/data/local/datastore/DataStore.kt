package de.rogallab.mobile.data.local.datastore

import android.content.Context
import de.rogallab.mobile.MainApplication.Companion.DIRECTORY_NAME
import de.rogallab.mobile.MainApplication.Companion.FILE_NAME
import de.rogallab.mobile.data.IDataStore
import de.rogallab.mobile.data.local.Seed
import de.rogallab.mobile.domain.IAppStorage
import de.rogallab.mobile.domain.entities.Person
import de.rogallab.mobile.domain.utilities.logDebug
import de.rogallab.mobile.domain.utilities.logError
import de.rogallab.mobile.domain.utilities.logVerbose
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import java.io.File
import kotlin.collections.any
import kotlin.collections.filter
import kotlin.collections.firstOrNull
import kotlin.collections.indexOfFirst
import kotlin.collections.none
import kotlin.collections.sortedBy
import kotlin.collections.toList
import kotlin.io.readText
import kotlin.io.writeText
import kotlin.text.isBlank
import kotlin.text.lowercase

class DataStore(
   private val _context: Context,
   private val _appStorage: IAppStorage,
): IDataStore {

   // directory and file name for the dataStore from MainApplication
   // get the Apps home directory
   private val _appHome = _context.filesDir.toString()
   private val _directoryName = DIRECTORY_NAME
   private val _fileName =  FILE_NAME

   // list of people
   private var _people: MutableList<Person> = mutableListOf()

   // Json serializer
   private val _json = Json {
      prettyPrint = true
      ignoreUnknownKeys = true
   }

   init {
      logDebug(TAG, "init: read datastore")
      _people.clear()
      read()
   }

   override fun selectAll(): List<Person> =
      _people.toList()

   // sort case insensitive by selector
   override fun selectAllSortedBy(
      selector: (Person) -> String?
   ): List<Person> =
      _people.sortedBy { person -> selector(person)?.lowercase() }
         .toList()

   override fun selectWhere(predicate: (Person) -> Boolean): List<Person> =
      _people.filter(predicate)
         .toList()

   override fun findById(id: String): Person? =
      _people.firstOrNull { it:Person -> it.id == id }

   override fun findBy(predicate: (Person) -> Boolean): Person? =
      _people.firstOrNull(predicate)

   override fun insert(person: Person) {
      logVerbose(TAG, "insert: $person")
      if (_people.any { it.id == person.id })
         throw kotlin.IllegalArgumentException("Person with id ${person.id} already exists")
      _people.add(person)
      write()
   }

   override fun update(person: Person) {
      logVerbose(TAG, "update: $person")
      val index = _people.indexOfFirst { it.id == person.id }
      if (index == -1)
         throw kotlin.IllegalArgumentException("Person with id ${person.id} does not exist")
      _people[index] = person
      write()
   }

   override fun delete(person: Person) {
      logVerbose(TAG, "delete: $person")
      if (_people.none { it.id == person.id })
         throw kotlin.IllegalArgumentException("Person with id ${person.id} does not exist")
      _people.remove(person)
      write()
   }

   // list of people is saved as JSON file to the user's home directory
   // UserHome/Documents/android/people.json
   private fun read() {
      try {
         val filePath = getFilePath(_appHome,_fileName,_directoryName)
         // if file does not exist or is empty, return an empty list
         val file = File(filePath)
         if (!file.exists() || file.readText().isBlank()) {
            // seed _people with some data
            val seed = Seed(_context, _appStorage)
            _people.addAll(seed.people)
            logVerbose(TAG, "create(): seedData ${_people.size} people")
            write()
            return
         }
         // read json from a file and convert to a list of people
         val jsonString = File(filePath).readText()
         logVerbose(TAG, jsonString)
         _people = _json.decodeFromString(jsonString)
         logDebug(TAG, "read(): decode JSON ${_people.size} Ppeople")
      } catch (e: Exception) {
         logError(TAG, "Failed to read: ${e.message}")
         throw e
      }
   }

   // write the list of people to the dataStore as JSON file
   private fun write() {
      try {
         val filePath = getFilePath(_appHome,_fileName, _directoryName)
         val jsonString = _json.encodeToString(_people)
         logDebug(TAG, "write(): encode JSON ${_people.size} people")
         // save to a file
         val file = File(filePath)
         file.writeText(jsonString)
         logVerbose(TAG, jsonString)
      } catch (e: Exception) {
         logError(TAG, "Failed to write: ${e.message}")
         throw e
      }
   }

   companion object {

      private const val TAG = "<-DataStore"

      // get the file path for the dataStore
      // UserHome/Documents/android/people.json
      private fun getFilePath(appHome: String, fileName: String, directoryName: String): String {
         try {
            // the directory must exist, if not create it
            val directoryPath = "$appHome/documents/$directoryName"
            if ( !directoryExists(directoryPath) ) {
               val result = createDirectory(directoryPath)
               if (!result) {
                  throw kotlin.Exception("Failed to create directory: $directoryPath")
               }
            }
            // return the file path
            return "$directoryPath/$fileName"
         } catch (e: Exception) {
            logError(TAG, "Failed to getFilePath or create directory; ${e.localizedMessage}")
            throw e
         }
      }

      private fun directoryExists(directoryPath: String): Boolean {
         val directory = File(directoryPath)
         return directory.exists() && directory.isDirectory
      }

      private fun createDirectory(directoryPath: String): Boolean {
         val directory = File(directoryPath)
         return directory.mkdirs()
      }
   }
}