// package io.chrisdavenport.sqlitejs

import cats.effect._
// import shim.SqliteShim._
import io.chrisdavenport.sqlitesjs.Sqlite
import io.circe.syntax._

object Main extends IOApp {

  def run(args: List[String]): IO[ExitCode] = Sqlite.fromFile[IO]("test.sqlite").use{ sqlite => 
    sqlite.exec(createDB) >> 
    IO.println("Database Created") >>
    sqlite.run("INSERT OR REPLACE INTO contacts values (?, ?)", List("chris".asJson, "davenport".asJson))
      .flatTap(IO.println(_)) >>
    sqlite.all("SELECT first_name, last_name FROM contacts", List())
      .flatMap(IO.println(_))

  }.as(ExitCode.Success)

  val createDB = """CREATE TABLE IF NOT EXISTS contacts (
	first_name TEXT NOT NULL,
	last_name TEXT NOT NULL,
  CONSTRAINT names UNIQUE (first_name, last_name)
)"""


}