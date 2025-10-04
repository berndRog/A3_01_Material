package de.rogallab.mobile.data.repositories

import de.rogallab.mobile.data.IDataStore
import de.rogallab.mobile.data.local.datastore.DataStore
import de.rogallab.mobile.di.defModulesTest
import de.rogallab.mobile.domain.IPersonRepository
import de.rogallab.mobile.domain.entities.Person
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest
import org.koin.test.inject
import java.io.File
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.fail

class IPersonRepositoryUt: KoinTest {

   private val _dataStore: IDataStore by inject()
   private val _repository: IPersonRepository by inject()
   private var _testFilePath: String? = null
   private var _people = mutableListOf<Person>()

   @Before
   fun setup() {
      // Stop any existing Koin instance
      try {
         stopKoin()
      } catch (e: Exception) {
         // Ignore if Koin wasn't started
      }

      // Start Koin with test modules
      startKoin {
         modules(defModulesTest)
      }

      // decode people from given JSON String and write JSON file
      _testFilePath = ( _dataStore as DataStore).filePath
      requireNotNull(_testFilePath) {
         "DataStore.filePath is null"
      }
       _dataStore.initialize()
      // expected people from seed data
      _people = (_dataStore as DataStore).people.toMutableList()
   }

   @After
   fun tearDown() {
      _testFilePath?.let { path ->
         File(path).delete()
      }
      stopKoin()
   }

   @Test
   fun getAllUt_ok() {
      // arrange
      val expected = _people.toList()
      // act / assert
      _repository.getAll()
         .onSuccess { actual -> assertContentEquals(expected, actual) }
         .onFailure { fail(it.message) }
   }

   @Test
   fun getAllSortByUt_ok() {
      // arrange
      val expected = _people.sortedBy { it.firstName }
      // act / assert
      _repository.getAllSortedBy{ it.firstName}
         .onSuccess{ actual -> assertContentEquals(expected, actual) }
         .onFailure { fail(it.message) }
   }

   @Test
   fun getWhereUt_ok() {
      // arrange
      val expected = _people.filter{
         it.lastName.contains("mann",true) ?: false }
      // act / assert  --> Hoffmann
      _repository.getWhere { it.lastName.contains("mann",true) ?: false }
         .onSuccess { actual -> assertContentEquals(expected, actual) }
         .onFailure { fail(it.message) }
   }

   @Test
   fun findByIdUt_ok() {
      // arrange
      val id = "01000000-0000-0000-0000-000000000000"
      val expected = _people.firstOrNull { person -> person.id == id  }
      assertNotNull(expected)
      // act / assert
      _repository.findById(id)
         .onSuccess { actual -> assertEquals(expected, actual)  }
         .onFailure { fail(it.message) }
   }

   @Test
   fun findByUt_ok() {
      // arrange
      val expected = _people.firstOrNull { person ->
         person.lastName.contains("mann",true ) ?: false }
      assertNotNull(expected)
      // act / assert
      _repository.findBy { it.lastName.contains("mann",true ) ?: false }
         .onSuccess { actual -> assertEquals(expected, actual) }
         .onFailure { fail(it.message) }
   }

   @Test
   fun insertUt_ok() {
      // arrange
        val person = Person(
         "Bernd", "Rogalla", "b-u.rogalla@ostfalia.de", null,
         "00000001-0000-0000-0000-000000000000")
      // act
      _repository.create(person)
         .onSuccess { assertEquals(Unit, it) }
         .onFailure { fail(it.message) }
      // assert
      _repository.findById(person.id)
         .onSuccess { assertEquals(person, it) }
         .onFailure { fail(it.message) }
   }

   @Test
   fun updateUt_ok() {
      // arrange
      val id = "01000000-0000-0000-0000-000000000000"
      var person: Person? = null
      _repository.findById(id)
         .onSuccess { person = it }
         .onFailure { t -> fail(t.message) }
      assertNotNull(person)
      // act
      val updated = person.copy(lastName ="Albers")
      _repository.update(updated)
         .onSuccess { assertEquals(Unit, it) }
         .onFailure { fail(it.message) }
      // assert
      _repository.findById(person.id)
         .onSuccess { assertEquals(updated, it) }
         .onFailure { fail(it.message) }
   }

   @Test
   fun deleteUt_ok() {
      // arrange
      val id = "01000000-0000-0000-0000-000000000000"
      val person = _dataStore.findById(id)
      assertNotNull(person)
      // act
      _repository.remove(person)
         .onSuccess { assertEquals(Unit, it) }
         .onFailure { fail(it.message) }
      // assert
      _repository.findById(person.id)
         .onSuccess { actual -> assertNull(actual) }
         .onFailure { fail(it.message) }
   }
}
