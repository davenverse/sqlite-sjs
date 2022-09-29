package io.chrisdavenport.sqlitenative

import cats.effect._
import cats.syntax.all._
import com.github.sqlite4s._

object Test extends IOApp.Simple {
  def run: IO[Unit] = Resource.make{
    IO.blocking{
      val conn = new SQLiteConnection(
        new java.io.File("test.sqlite")
      )
      conn.open(true)
      conn
    }
  }(conn => IO.blocking(conn.dispose())).use{ conn =>
    val s = new shim.SqliteShim[IO](conn)
    s.all("SELECT * FROM cookies")
      .flatMap(l => IO.println(l))

  }
}