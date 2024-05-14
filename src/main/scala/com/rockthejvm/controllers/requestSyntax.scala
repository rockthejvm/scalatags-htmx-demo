package com.rockthejvm.controllers

import com.rockthejvm.domain.errors.ServerExceptions
import zio.ZIO
import zio.http.Request
import zio.json.*

object requestSyntax {
  extension (request: Request) {
    def to[T: JsonDecoder]: ZIO[Any, ServerExceptions.BadRequest, T] =
      for
        body <- request.body.asString
          .mapError(err => ServerExceptions.BadRequest(s"Failure getting request body: ${err.getMessage}"))
        result <- ZIO
          .fromEither(body.fromJson[T])
          .mapError(err => ServerExceptions.BadRequest(s"Failure parsing json: ${err}"))
      yield result

  }
}
