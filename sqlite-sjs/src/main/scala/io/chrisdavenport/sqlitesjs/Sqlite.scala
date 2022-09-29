package io.chrisdavenport.sqlitesjs

import cats.syntax.all._
import io.chrisdavenport.sqlitesjs.shim.SqliteShim
import cats.effect._
import io.circe.Json
import io.circe.syntax._
// import io.circe.scalajs._
import io.circe.scalajs.{convertJsToJson, convertJsonToJs}

trait Sqlite[F[_]]{
  def exec(sql: String): F[Unit]
  // Uses Positional Encoding
  def get(sql: String, params: List[Json] = Nil): F[Option[Json]]
  def all(sql: String, params: List[Json] = Nil): F[List[Json]]
  def run(sql: String, params: List[Json] = Nil): F[Int]
}

object Sqlite {
  def fromFile[F[_]: Async](path: String): Resource[F, Sqlite[F]] = 
    Resource.make(Async[F].fromPromise(Async[F].delay(SqliteShim.open(SqliteShim.Config(path)))))(db => 
      Async[F].fromPromise(Async[F].delay(db.close()))
    ).map(new SqliteJsImpl[F](_))

  private class SqliteJsImpl[F[_]: Async](db: SqliteShim.Database) extends Sqlite[F]{
    def exec(sql: String): F[Unit] = Async[F].fromPromise(Async[F].delay(db.exec(sql)))

    def get(sql: String, params: List[Json]): F[Option[Json]] = 
      Async[F].fromPromise(Async[F].delay{
        db.get(sql, convertJsonToJs(params.asJson))
      })
      .flatMap(undef => undef.toOption.traverse(a => convertJsToJson(a).liftTo[F]))

    
    def all(sql: String, params: List[Json]): F[List[Json]] = 
      Async[F].fromPromise(Async[F].delay{
        db.all(sql, convertJsonToJs(params.asJson))
      })
      .flatMap(a => convertJsToJson(a).liftTo[F])
      .flatMap(_.as[List[Json]].liftTo[F])
    
    def run(sql: String, params: List[Json]): F[Int] = 
      Async[F].fromPromise(Async[F].delay{
        db.run(sql, convertJsonToJs(params.asJson))
      }).map(r => r.changes.toOption.map(_.toInt).getOrElse(0))
    
  }
}