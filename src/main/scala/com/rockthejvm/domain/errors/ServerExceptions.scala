package com.rockthejvm.domain.errors

object ServerExceptions {
  private val InvalidCredentialsMsg = "Invalid email or password!"

  case class BadRequest(message: String) extends RuntimeException(message)
  case class Unauthorized(message: String = InvalidCredentialsMsg) extends RuntimeException(message)
  case class NotFound(message: String) extends RuntimeException(message)
  case class AlreadyInUse(message: String) extends RuntimeException(message)
  case class DatabaseException(message: String) extends RuntimeException(message)
  case class ValidationError(errors: Map[String, String]) extends RuntimeException(s"Validation errors: ${errors.mkString(",")}")
}