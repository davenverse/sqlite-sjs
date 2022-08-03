package io.chrisdavenport.sqlitesjs.cross



trait PackagePlatform {
  // type SqliteConnection[F[_]] = doobie.Transactor[F]
  type Write[A] = doobie.Write[A]
  type Read[A] = doobie.Read[A]
}