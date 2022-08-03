package io.chrisdavenport.sqlitesjs.cross

// import io.chrisdavenport.sqlitesjs.Sqlite

trait PackagePlatform {
  // Warning: SJS encodes Booleans as Ints. If using boolean it will fail, use IntBoolean directly
  // for automatic or contramap from Decoder[IntBoolean] to build your decoder for use with booleans.
  type Write[A] = io.circe.Encoder[A]
  type Read[A] = io.circe.Decoder[A]

}