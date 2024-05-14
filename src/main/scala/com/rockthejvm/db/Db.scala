package com.rockthejvm.db

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import io.getquill.*
import io.getquill.jdbczio.*
import zio.{ZIO, ZLayer}

import javax.sql.DataSource

object Db {
  private def create(dbConfig: DbConfig): HikariDataSource = {
    val poolConfig = new HikariConfig()
    poolConfig.setJdbcUrl(dbConfig.jdbcUrl)
    poolConfig.setConnectionInitSql(dbConfig.connectionInitSql)
    new HikariDataSource(poolConfig)
  }

  val dataSourceLive: ZLayer[DbConfig, Nothing, DataSource] =
    ZLayer.scoped {
      ZIO.fromAutoCloseable {
        for {
          dbConfig <- ZIO.service[DbConfig]
          dataSource <- ZIO.succeed(create(dbConfig))
        } yield dataSource
      }
    }

  val quillLive: ZLayer[DataSource, Nothing, Quill.Sqlite[SnakeCase]] =
    Quill.Sqlite.fromNamingStrategy(SnakeCase)
}