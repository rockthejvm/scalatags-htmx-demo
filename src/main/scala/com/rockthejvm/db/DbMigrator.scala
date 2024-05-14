package com.rockthejvm.db

import org.flywaydb.core.Flyway
import org.flywaydb.core.api.output.MigrateErrorResult
import zio.{Task, ZIO, ZLayer}

import javax.sql.DataSource

class DbMigrator(ds: DataSource) {
  import DbMigrator.*
  
  def migrate(): Task[Unit] = {
    ZIO
      .attempt(
        Flyway
          .configure()
          .dataSource(ds)
          .load()
          .migrate()
      )
      .flatMap {
        case r: MigrateErrorResult => ZIO.fail(DbMigrationFailed(r.error.message, r.error.stackTrace))
        case e => ZIO.unit
      }
      .onError(cause => ZIO.logErrorCause("Database migration has failed", cause))
  }
}

object DbMigrator {
  case class DbMigrationFailed(msg: String, stackTrace: String) extends RuntimeException(s"$msg\n$stackTrace")
  
  def live: ZLayer[DataSource, Nothing, DbMigrator] = 
    ZLayer.fromFunction(DbMigrator(_))
}
