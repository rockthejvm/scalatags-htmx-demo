package com.rockthejvm.repositories

import com.rockthejvm.domain.data.Contact
import com.rockthejvm.domain.errors.ServerExceptions
import io.getquill.*
import io.getquill.jdbczio.*
import org.sqlite.SQLiteErrorCode.SQLITE_CONSTRAINT_UNIQUE
import org.sqlite.{SQLiteErrorCode, SQLiteException}
import zio.{IO, RIO, Task, ZIO, ZLayer}

import java.sql.SQLException
import java.time.Instant
import scala.collection.immutable
import scala.util.chaining.*

class ContactsRepository(quill: Quill.Sqlite[SnakeCase]):
  import quill.*

  private inline def queryContact = quote(querySchema[Contact](entity = "contacts"))

  def count() = 
    run(queryContact.size)
    
  def insert(contact: Contact): ZIO[Any, RuntimeException, Long] =
    run(queryContact.insert(_.phone -> lift(contact.phone), _.name -> lift(contact.name), _.email -> lift(contact.email)))
      .mapDatabaseException("Contact already exists")

  def update(id: Long, contact: Contact): ZIO[Any, RuntimeException, Long] =
    run(
      queryContact
        .filter(_.id == lift(id))
        .update(_.phone -> lift(contact.phone), _.name -> lift(contact.name), _.email -> lift(contact.email))
    ).mapDatabaseException()

  def delete(ids: List[Long]): ZIO[Any, RuntimeException, Long] =
    run(
      queryContact
        .filter(c => liftQuery(ids).contains(c.id))
        .delete
    ).mapDatabaseException()

  def delete(id: Long): ZIO[Any, RuntimeException, Long] =
    run(
      queryContact
        .filter(_.id == lift(id))
        .delete
    ).mapDatabaseException()
    
  def listContacts(page: Int): ZIO[Any, SQLException, List[Contact]] = 
    run(queryContact.drop(10 * lift(page)).take(10))

  def findById(id: Long): ZIO[Any, RuntimeException, Contact] = 
    run(queryContact.filter(c => c.id == lift(id)))
      .map(_.headOption)
      .mapDatabaseException()
      .flatMap(maybeContact => ZIO.getOrFailWith(ServerExceptions.NotFound(s"No contact with id ${id} found"))(maybeContact))

  def findByEmail(email: String): ZIO[Any, RuntimeException, Option[Contact]] = 
    run(queryContact.filter(c => c.email == lift(email)))
      .map(_.headOption)
      .mapDatabaseException()

  def filter(term: String, page: Int) = run(
    queryContact
      .sortBy(_.id)
      .filter(c => c.email.like(lift(s"%$term%")) || c.name.like(lift(s"%$term%")) || c.phone.like(lift(s"%$term%")))
      .drop(10 * lift(page))
      .take(10)
  )

  extension [R, A](task: RIO[R, A])
    // [SQLITE_CONSTRAINT_UNIQUE] A UNIQUE constraint failed (UNIQUE constraint failed: contacts.email)
    def mapDatabaseException(message: String = "Unknown error when connecting to database"): ZIO[R, RuntimeException, A] = task.mapError {
      case e: SQLiteException if e.getResultCode == SQLITE_CONSTRAINT_UNIQUE =>
        ServerExceptions.AlreadyInUse(e.getMessage)
      case e => ServerExceptions.DatabaseException(s"${message}: e.getMessage")
    }

object ContactsRepository {
  val live: ZLayer[Quill.Sqlite[SnakeCase], Nothing, ContactsRepository] =
    ZLayer.fromFunction(new ContactsRepository(_))
}
