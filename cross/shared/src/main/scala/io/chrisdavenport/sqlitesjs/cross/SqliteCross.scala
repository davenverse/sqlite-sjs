package io.chrisdavenport.sqlitesjs.cross


trait SqliteCross[F[_]]{
  def exec(sql: String): F[Unit]
  // Uses Positional Encoding
  def get[B: Read](sql: String): F[Option[B]]
  def get[A: Write, B: Read](sql: String, write: A): F[Option[B]]


  def all[B: Read](sql: String): F[List[B]]
  def all[A: Write, B: Read](sql: String, write: A): F[List[B]]

  def run(sql: String): F[Int]
  def run[A: Write](sql: String, write: A): F[Int]
}

object SqliteCross extends SqliteCrossCompanionPlatform {}


