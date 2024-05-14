package com.rockthejvm.domain.errors

import zio.json.JsonCodec

sealed trait ErrorInfo
case class BadRequest(error: String = "Bad request.") extends ErrorInfo derives JsonCodec
case class Unauthorized(error: String = "Unauthorized.") extends ErrorInfo derives JsonCodec
case class Forbidden(error: String = "Forbidden.") extends ErrorInfo derives JsonCodec
case class NotFound(error: String = "Not found.") extends ErrorInfo derives JsonCodec
case class Conflict(error: String = "Conflict.") extends ErrorInfo derives JsonCodec
case class ValidationFailed(errors: Map[String, List[String]]) extends ErrorInfo derives JsonCodec
case class InternalServerError(error: String = "Internal server error.") extends ErrorInfo derives JsonCodec
