package io.chrisdavenport.sqlitesjs.cross

trait PackagePlatform {
  // type SqliteConnection[F[_]] = doobie.Transactor[F]
  type Write[A] = A
  type Read[A] = A
}