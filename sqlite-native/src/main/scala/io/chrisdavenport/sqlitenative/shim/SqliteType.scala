package io.chrisdavenport.sqlitenative.shim

sealed trait SqliteType extends Product with Serializable
object SqliteType {
  case class SqliteInt(i: Int) extends SqliteType
  case class SqliteDouble(d: Double) extends SqliteType
  case class SqliteString(s: String) extends SqliteType
  case class SqliteArray(b: Array[Byte]) extends SqliteType
  case class SqliteLong(l: Long) extends SqliteType
  case class SqliteNull() extends SqliteType
}