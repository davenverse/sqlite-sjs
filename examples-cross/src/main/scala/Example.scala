import io.chrisdavenport.sqlitesjs.cross._

import cats.syntax.all._
import cats.effect._

object Example extends IOApp.Simple {

  def run: IO[Unit] = SqliteCross.impl[IO]("testing/testCrossExample.sqlite").use{ cross => 

    for {
      _ <- cross.exec(createTableStatement)
      init <- cross.all[Hello](select)
      _ <- if (init.isEmpty) IO.unit else IO.println("Initial State:") 
      _ <- init.traverse(h => IO.println(h))
      _ <- IO.println("Please Say Hello To Someone or say CLEAR:")
      who <- fs2.io.stdin[IO](4096).through(fs2.text.utf8.decode).takeThrough(_.contains("\n")).compile.string.map(_.trim())
      // who <- std.Console[IO].readLine // BROKEN
      _ <- if (who === "CLEAR") clear(cross) else sayHello(cross, who)
    } yield ()
  }

  def sayHello(cross: SqliteCross[IO], name: String): IO[Unit] = for {
    now <- Clock[IO].realTime.map(_.toMillis)
    record = Hello(name, now, IntBoolean(name.toUpperCase() === name))
    _ <- cross.run(upsert, record)
    _ <- IO.println("") >> IO.println("Current State:")
    after <- cross.all[Hello](select)
    _ <- after.traverse(h => IO.println(h))
  } yield ()

  def clear(cross: SqliteCross[IO]): IO[Unit] = {
    cross.run(del).flatMap(i => IO.println(s"Cleared $i rows"))
  }

  case class Hello(name: String, lastAccessed: Long, allCaps: IntBoolean)
  // IntBoolean is used to get booleans in a compatible fashion without custom encoding/generic logic.
  // You can use a custom decoder which maps to Boolean from IntBoolean if you do want to prevent
  // intermediates appearing in your code.
  object Hello {
    // Can use anything to get the decoder for this
    // Generic for 2.12
    // derives Codec.AsObject for 3
    // implicit val codec: io.circe.Codec[Hello] = ???
    // In this example I use semi-auto derivation
    implicit val codec: io.circe.Codec[Hello] = io.circe.generic.semiauto.deriveCodec[Hello]

    // An example of how to do this without use of custom type, but explicitly encoding/decoding
    // implicit val decoder: Decoder[Hello] = new Decoder[Hello]{
    //   def apply(h: HCursor) = for {
    //     name <- h.downField("name").as[String]
    //     accessed <- h.downField("lastAccessed").as[Long]
    //     allCaps <- h.downField("allCaps").as[IntBoolean].map(_.boolean)
    //   } yield Hello(name, accessed, allCaps)
    // }

    // implicit val encoder:  Encoder[Hello] = new Encoder[Hello]{
    //   def apply(a: Hello): Json = Json.obj(
    //     "name" -> a.name.asJson,
    //     "lastAccessed" -> a.lastAccessed.asJson,
    //     "allCaps" -> IntBoolean(a.allCaps).asJson
    //   )
    // }
  }

  val createTableStatement = {
    """CREATE TABLE IF NOT EXISTS hello (
      |name TEXT NOT NULL PRIMARY KEY,
      |lastAccessed INTEGER NOT NULL,
      |allCaps INTEGER NOT NULL)""".stripMargin
  }

  val select = {
    "SELECT name,lastAccessed,allCaps FROM hello"
  }

  val upsert = {
    """INSERT OR REPLACE INTO hello (name, lastAccessed, allCaps) VALUES (?, ?, ?)"""
  }

  val del = """DELETE FROM hello"""

}