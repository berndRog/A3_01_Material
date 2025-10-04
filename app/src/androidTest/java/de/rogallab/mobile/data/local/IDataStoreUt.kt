package de.rogallab.mobile.data.local

import de.rogallab.mobile.Globals
import de.rogallab.mobile.data.IDataStore
import de.rogallab.mobile.data.local.datastore.DataStore
import de.rogallab.mobile.di.defModulesTest
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

class IDataStoreUt: KoinTest {

   private val _dataStore: IDataStore by inject()
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
      // disable log output for tests
      Globals.isInfo = false
      Globals.isDebug = false
      Globals.isVerbose = false

      // decode people from given JSON String and write JSON file
      _testFilePath = ( _dataStore as DataStore).filePath
      requireNotNull(_testFilePath) { "DataStore.filePath is null" }
      _dataStore.initialize() //
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
   fun selectAll_ok() {
      // arrange
      val expected = _people
      // act
      val actual = _dataStore.selectAll()
      // assert
      assertContentEquals(expected, actual)
   }

   @Test
   fun selectAllSortBy_ok() {
      // arrange
      val expected = _people.sortedBy { it.firstName }
      // act
      val actual = _dataStore.selectAllSortedBy { it.firstName }
      // assert
      assertContentEquals(expected, actual)
   }

   @Test
   fun selectWhere_ok() {
      // arrange
      val expected = _people.filter{
         it.lastName.contains("mann",true) ?: false }
      // act
      val actual = _dataStore.selectWhere {
         it.lastName.contains("mann",true) ?: false }
      // assert
      assertContentEquals(expected, actual)
   }

   @Test
   fun findById_ok() {
      // arrange
      val id = "01000000-0000-0000-0000-000000000000"
      val expected = _people.firstOrNull { person -> person.id == id  }
      assertNotNull(expected)
      // act
      val actual = _dataStore.findById(id)
      // assert
      assertEquals(expected, actual)
   }

   @Test
   fun findBy_ok() {
      // arrange
      val name = "arne"
      val expected = _people.firstOrNull { person ->
         person.firstName.contains(name,true ) ?: false }
      assertNotNull(expected)
      // act
      val actual = _dataStore.findBy { person ->
         person.firstName.contains(name,true ) ?: false }
      // assert
      assertEquals(expected, actual)
   }

   @Test
   fun insert_ok() {
      // arrange
      val person = Person(
         "Bernd", "Rogalla",
         "00000001-0000-0000-0000-000000000000")
      // act
      _dataStore.insert(person)
      // assert
      val actual = _dataStore.findById(person.id)
      assertEquals(person, actual)
   }

   @Test
   fun update_ok() {
      // arrange
      val id = "01000000-0000-0000-0000-000000000000"
      val person = _dataStore.findById(id)
      assertNotNull(person)
      // act
      val updated = person.copy(lastName ="Albers") //, email = "a.albers@gmx.de")
      _dataStore.update(updated)
      // assert
      val actual = _dataStore.findById(person.id)
      assertEquals(updated, actual)
   }

   @Test
   fun delete_ok() {
      // arrange
      val id = "01000000-0000-0000-0000-000000000000"
      val person = _dataStore.findById(id)
      assertNotNull(person)
      // act
      _dataStore.delete(person)
      // assert
      val actual = _dataStore.findById(person.id)
      assertNull(actual)
   }
}

