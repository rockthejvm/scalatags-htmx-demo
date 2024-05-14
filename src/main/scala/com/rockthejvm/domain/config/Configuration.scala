package com.rockthejvm.domain.config

import zio.*
import zio.config.magnolia.*
import zio.config.typesafe.*

final case class AppConfig(db: DbConfig)
final case class DbConfig(url: String)

object Configuration {
  val live: ZLayer[Any, Config.Error, AppConfig] =
    ZLayer.fromZIO(TypesafeConfigProvider.fromResourcePath().load(deriveConfig[AppConfig]))
}
  

