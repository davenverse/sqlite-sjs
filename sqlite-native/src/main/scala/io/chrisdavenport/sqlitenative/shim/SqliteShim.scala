package io.chrisdavenport.sqlitenative.shim

import com.github.sqlite4s._
import cats.syntax.all._
import cats.effect._
import io.chrisdavenport.sqlitenative.shim.SqliteType._

private[sqlitenative] class SqliteShim[F[_]: Sync](db: SQLiteConnection){
  def exec(s: String): F[Unit] = Sync[F].blocking{
    val _ = db.exec(s)
    ()
  }

  def get(sql: String, params: List[SqliteType] = Nil): F[Option[SqliteType]] = Sync[F].blocking{
    val p = db.prepare(sql)
    params.zipWithIndex.foreach{
      case (t, index) => t match {
        case SqliteDouble(d) => p.bind(index, d)
        case SqliteInt(i) => p.bind(index, i)
        case SqliteArray(b) => p.bind(index, b)
        case SqliteLong(l) => p.bind(index, l)
        case SqliteString(s) => p.bind(index, s)
        case SqliteNull() => p.bindNull(index)
      }
    }
    var i = 0
    var out = Option.empty[SqliteType]
    while (p.step() && i < 1){
      val intermediate : Option[SqliteType] = p.columnValue(i) match {
        case null => SqliteNull().some
        case i: Int => SqliteInt(i).some
        case b: Array[Byte] => SqliteArray(b).some
        case d: Double => SqliteDouble(d).some
        case l: Long => SqliteLong(l).some
        case s: String => SqliteString(s).some
        case _ => None
      }
      out = intermediate
      i = i + 1
    }
    p.dispose() // Make Resource
    out
  }
  def all(sql: String, params: List[SqliteType] = Nil): F[List[SqliteType]] = Sync[F].blocking{
    val p = db.prepare(sql)
    params.zipWithIndex.foreach{
      case (t, index) => t match {
        case SqliteDouble(d) => p.bind(index, d)
        case SqliteInt(i) => p.bind(index, i)
        case SqliteArray(b) => p.bind(index, b)
        case SqliteLong(l) => p.bind(index, l)
        case SqliteString(s) => p.bind(index, s)
        case SqliteNull() => p.bindNull(index)
      }
    }
    var i = 0
    val buffer = new scala.collection.mutable.ListBuffer[SqliteType]
    while (p.step()){
      val intermediate : Option[SqliteType] = p.columnValue(i) match {
        case null => SqliteNull().some
        case i: Int => SqliteInt(i).some
        case b: Array[Byte] => SqliteArray(b).some
        case d: Double => SqliteDouble(d).some
        case l: Long => SqliteLong(l).some
        case s: String => SqliteString(s).some
        case _ => None
      }
      intermediate.foreach(s => buffer += s)
      i = i + 1
    }
    p.dispose() // Make Resource
    buffer.toList
  }
  def run(sql: String, params: List[SqliteType] = Nil): F[Int] = Sync[F].blocking{
    val p = db.prepare(sql)
    params.zipWithIndex.foreach{
      case (t, index) => t match {
        case SqliteDouble(d) => p.bind(index, d)
        case SqliteInt(i) => p.bind(index, i)
        case SqliteArray(b) => p.bind(index, b)
        case SqliteLong(l) => p.bind(index, l)
        case SqliteString(s) => p.bind(index, s)
        case SqliteNull() => p.bindNull(index)
      }
    }
    val out = if (p.step()) {
      p.columnInt(0)
    } else 0
    p.dispose()
    out
  }
}

object SqliteShim {

}