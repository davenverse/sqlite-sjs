package io.chrisdavenport.sqlitesjs

import munit.CatsEffectSuite
import cats.effect._
import io.circe._
import io.circe.syntax._

class SqliteSpec extends CatsEffectSuite {

  test("Sqlite should interact with a database") {
      val createDB = """CREATE TABLE IF NOT EXISTS contacts (
	first_name TEXT NOT NULL,
	last_name TEXT NOT NULL,
  CONSTRAINT names UNIQUE (first_name, last_name)
)"""
    Sqlite.fromFile[IO]("testing/sqlitespec.sqlite").use{sqlite => 
      sqlite.exec(createDB) >> {
      for {
        count <- sqlite.run("INSERT OR REPLACE INTO contacts values (?, ?)", List("chris".asJson, "davenport".asJson))
        out <- sqlite.get("SELECT first_name, last_name FROM contacts")
      } yield {
        assertEquals(count, 1)
        assertEquals(out, Some(Json.obj("first_name" -> "chris".asJson, "last_name" -> "davenport".asJson)))
      }}
    }
  }

}
