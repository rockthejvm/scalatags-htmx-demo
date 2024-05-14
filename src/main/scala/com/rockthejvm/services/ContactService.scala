package com.rockthejvm.services

import com.rockthejvm.domain.data.Contact
import com.rockthejvm.domain.errors.ServerExceptions
import com.rockthejvm.repositories.ContactsRepository
import zio.http.FormField
import zio.prelude.{Validation, ZValidation}
import zio.*

class ContactService(contactsRepository: ContactsRepository) {
  type FormMap = Map[String, FormField]

  def searchContacts(searchTerm: String, page: Int) = 
    if (searchTerm.isBlank) contactsRepository.listContacts(page)
    else contactsRepository.filter(searchTerm, page)

  def countContacts = 
    contactsRepository.count()

  def findById(id: Long) = 
    contactsRepository.findById(id)
    
  def delete(id: Long) = 
    contactsRepository.delete(id)

  def update(id: Long, formMap: FormMap) = 
    extractContactFromQueryParams(formMap)
      .flatMap(contactsRepository.update(id, _))

  def create(formMap: FormMap) = 
    extractContactFromQueryParams(formMap)
      .flatMap(createContact)

  def listContacts(page: Int) = 
    contactsRepository.listContacts(page)

  def count() = 
    contactsRepository.count()
    
  def validateEmail(email: String) = 
    contactsRepository.findByEmail(email)
      .map(maybeEmail => maybeEmail.map(_ => "Email already in use").getOrElse(""))
    
  private def createContact(contact: Contact) = 
    contactsRepository.insert(contact)

  private def extractContactFromQueryParams(formData: FormMap): IO[RuntimeException, Contact] = {
    val validations: ZValidation[Nothing, Map[String, String], Contact] = Validation.validateWith(
      Validation.succeed(-1L),
      extractNonEmptyString("name", formData.get("name")),
      extractNonEmptyString("phone", formData.get("phone")),
      extractNonEmptyString("email", formData.get("email"))
    )(Contact.apply)

    validations.toZIOParallelErrors
      .mapError(err => err.foldLeft(Map.empty[String, String])(_ ++ _))
      .mapError(err => ServerExceptions.ValidationError(err))
  }

  private def extractNonEmptyString(name: String, maybe: Option[FormField]): ZValidation[Nothing, Map[String, String], String] =
    for {
      value <- Validation
        .fromOptionWith(s"${name.capitalize} can't be empty")(maybe.flatMap(_.stringValue))
        .mapError(err => Map(name -> err))
      _ <- Validation.fromPredicateWith(s"${name.capitalize} can't be blank")(value)(!_.isBlank).mapError(err => Map(name -> err))
    } yield value
}

object ContactService {
  val live: ZLayer[ContactsRepository, Nothing, ContactService] =
    ZLayer.derive[ContactService]
}
