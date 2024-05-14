package com.rockthejvm.domain.errors

import zio.*
import zio.http.*

object ErrorMapper {
  extension [E <: Throwable, A](task: ZIO[Any, E, A])
    def defaultErrorsMappings: ZIO[Any, Response, A] = task.mapError {
      case e: ServerExceptions.AlreadyInUse =>
        Response(
          status = Status.Conflict,
          body = Body.fromString(e.message)
        )
      case e: ServerExceptions.NotFound =>
        Response(
          status = Status.NotFound,
          body = Body.fromString(e.message)
        )
      case e: ServerExceptions.BadRequest =>
        Response(
          status = Status.BadRequest,
          body = Body.fromString(e.message)
        )
      case e: ServerExceptions.Unauthorized =>
        Response(
          status = Status.Unauthorized,
          body = Body.fromString(e.message)
        )
      case e: ServerExceptions.DatabaseException =>
        Response(
          status = Status.InternalServerError,
          body = Body.fromString(e.message)
        )
      case _ =>
        Response(
          status = Status.InternalServerError
        )
    }
}