package io.chrisdavenport.sqlitesjs.cross

import doobie.{Write => _, Read => _, _}
import doobie.syntax.all._
import cats.effect.kernel._
import cats.syntax.all._

trait SqliteCrossCompanionPlatform {
  def impl[F[_]: Async](path: String): Resource[F, SqliteCross[F]] = Resource.pure[F, SqliteCross[F]]{
    val ts = Transactor.fromDriverManager[F]("org.sqlite.JDBC", s"jdbc:sqlite:${path.toString}", "", "")
    new SqliteCrossJVMImpl[F](ts)
  }

  private class SqliteCrossJVMImpl[F[_]: MonadCancelThrow](ts: Transactor[F]) extends SqliteCross[F]{
    def exec(sql: String): F[Unit] = Update(sql).run(()).transact(ts).void
    def get[B: Read](sql: String): F[Option[B]] = Query0(sql).option.transact(ts)
    def get[A: Write, B: Read](sql: String, write: A): F[Option[B]] =
      Query[A, B](sql).option(write).transact(ts)

    def all[B: Read](sql: String): F[List[B]] = 
      Query0(sql).to[List].transact(ts)
    def all[A: Write, B: Read](sql: String, write: A): F[List[B]] = 
      Query[A, B](sql).to[List](write).transact(ts)

    def run(sql: String): F[Int] = Update(sql).run(()).transact(ts)
    def run[A: Write](sql: String, write: A): F[Int] = Update[A](sql).run(write).transact(ts)
  }
}