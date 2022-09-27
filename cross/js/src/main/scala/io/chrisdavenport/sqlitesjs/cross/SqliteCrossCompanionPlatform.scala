package io.chrisdavenport.sqlitesjs.cross

import cats.effect.kernel._
import cats.syntax.all._
import io.chrisdavenport.sqlitesjs.Sqlite
import io.circe.{Json, JsonNumber, JsonObject}

trait SqliteCrossCompanionPlatform {
  def impl[F[_]: Async](path: String): Resource[F, SqliteCross[F]] = {
    Sqlite.fromFile(path).map(new SqliteCrossJSImpl[F](_))
  }

  private class SqliteCrossJSImpl[F[_]: Async](sqlite: Sqlite[F]) extends SqliteCross[F]{

    private def encode[A: Write](a: A): List[Json] = {
      val json = io.circe.Encoder[A].apply(a)
      def toList(json: Json): List[Json] = {
        json.fold(
          jsonNull = List(Json.Null),
          jsonBoolean = {(bool: Boolean) => List({if (bool) Json.fromInt(1) else Json.fromInt(0)})}, // Throw maybe
          jsonNumber = {(number: JsonNumber) => List(Json.fromJsonNumber(number))},
          jsonString = {(s: String) => List(Json.fromString(s))},
          jsonArray =  {(v: Vector[Json]) => v.toList}, // Throw Maybe
          jsonObject = {(obj: JsonObject) => obj.toList.flatMap(a => toList(a._2))} // Throw maybe?
        )
      }
      toList(json)
    }

    def exec(sql: String): F[Unit] = sqlite.exec(sql)
    def get[B: Read](sql: String): F[Option[B]] = sqlite.get(sql).flatMap(_.traverse(_.as[B]).liftTo[F])
    def get[A: Write, B: Read](sql: String, write: A): F[Option[B]] = {
      sqlite.get(sql, encode(write)).flatMap(_.traverse(_.as[B]).liftTo[F])
    }

    def all[B: Read](sql: String): F[List[B]] = 
      sqlite.all(sql).flatMap(_.traverse(_.as[B]).liftTo[F])
    def all[A: Write, B: Read](sql: String, write: A): F[List[B]] = 
      sqlite.all(sql, encode(write)).flatMap(_.traverse(_.as[B]).liftTo[F])

    def run(sql: String): F[Int] = sqlite.run(sql)
    def run[A: Write](sql: String, write: A): F[Int] = sqlite.run(sql, encode(write))
  }
}