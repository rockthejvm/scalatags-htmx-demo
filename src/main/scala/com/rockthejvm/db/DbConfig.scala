package com.rockthejvm.db

import com.rockthejvm.domain.config.AppConfig
import zio.{ZIO, ZLayer}

case class DbConfig(jdbcUrl: String) {
  val connectionInitSql = "PRAGMA foreign_keys = ON"
}

object DbConfig {
  val live: ZLayer[AppConfig, Nothing, DbConfig] =
    ZLayer.fromFunction { (appConfig: AppConfig) =>
      DbConfig(appConfig.db.url)
    }
}
