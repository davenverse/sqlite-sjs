package io.chrisdavenport.sqlitesjs.cross

import io.circe._
import cats.syntax.all._

// Use this rather than boolean in models, otherwise javascript platform will
// die. 
case class IntBoolean(boolean: Boolean)
object IntBoolean {
  implicit val decoder: Decoder[IntBoolean] = new Decoder[IntBoolean]{
    def apply(c: HCursor): Decoder.Result[IntBoolean] =
      Decoder[Int].emap{
        case 0 => IntBoolean(false).asRight
        case 1 => IntBoolean(true).asRight
        case other => s"Invalid IntBoolean: $other".asLeft
      }.or(Decoder.decodeBoolean.map(IntBoolean(_)))(c)
  }

  // implicit val decoder: Decoder[IntBoolean] = Decoder[Int].emap{
  //   case 0 => IntBoolean(false).asRight
  //   case 1 => IntBoolean(true).asRight
  //   case other => s"Invalid IntBoolean: $other".asLeft
  // }

  implicit val encoder: Encoder[IntBoolean] = Encoder[Int].contramap[IntBoolean]{
    case IntBoolean(true) => 1
    case IntBoolean(false) => 0
  }
}