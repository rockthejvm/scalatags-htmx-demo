package com.rockthejvm

import com.rockthejvm.controllers.{ContactsController, Redirects}
import com.rockthejvm.db.{Db, DbConfig, DbMigrator}
import com.rockthejvm.domain.config.Configuration
import com.rockthejvm.repositories.ContactsRepository
import com.rockthejvm.services.ContactService
import zio.*
import zio.http.*
import zio.logging.LogFormat
import zio.logging.backend.SLF4J

object Main extends ZIOAppDefault {

  override val bootstrap: ZLayer[ZIOAppArgs, Any, Any] =
    SLF4J.slf4j(LogFormat.colored)

  override def run: ZIO[Any & ZIOAppArgs & Scope, Any, Any] = {
    val basicRoutes = Routes(
      Method.GET / "" -> handler(Response.redirect(Redirects.contacts)),
      Method.GET / "static" / "css" / "main.css" -> Handler.fromResource("css/main.css").orDie
    )

    val htmxApp = ZIO.service[ContactsController].map { contacts =>
      basicRoutes.toHttpApp ++ contacts.routes.toHttpApp
    }
    
    val program = 
      for {
        migrator <- ZIO.service[DbMigrator]
        _ <- Console.printLine(s"Running db migrations")
        _ <- migrator.migrate()
        _ <- ZIO.logInfo("Successfully ran migrations")
        app <- htmxApp
        _ <- ZIO.logInfo("Starting server....")
        _ <- Server.serve(app @@ Middleware.debug @@ Middleware.flashScopeHandling)
        _ <- ZIO.logInfo("Server started - Rock the JVM!")
      } yield ()
      
    program.provide(
        Configuration.live,
        DbConfig.live,
        Db.dataSourceLive,
        Db.quillLive,
        DbMigrator.live,
        ContactsRepository.live,
        ContactsController.live,
        ContactService.live,
        Server.default
      )
      .exitCode
  }
}