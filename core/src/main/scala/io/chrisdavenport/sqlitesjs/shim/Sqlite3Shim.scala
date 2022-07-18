package io.chrisdavenport.sqlitesjs.shim

import scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.annotation.nowarn

private[sqlitesjs] object Sqlite3Shim {
  @JSImport("sqlite3", "Database")
  @js.native
  @nowarn
  val Database: js.Any = js.native

}