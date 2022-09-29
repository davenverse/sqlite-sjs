package io.chrisdavenport.sqlitesjs.shim

import scalajs.js
import scala.scalajs.js.annotation.JSImport
import scala.annotation.nowarn

private[sqlitesjs] object SqliteShim {
  @JSImport("sqlite", JSImport.Namespace)
  @js.native
  @nowarn
  val ^ : js.Any = js.native


  @scala.inline
  def open(config: Config): js.Promise[Database] = 
    ^.asInstanceOf[js.Dynamic].applyDynamic("open")(config.asInstanceOf[js.Any]).asInstanceOf[js.Promise[Database]]

  @js.native
  trait Config extends js.Object {
    
    /**
      * The database driver. Most will install `sqlite3` and use the `Database` class from it.
      * As long as the library you are using conforms to the `sqlite3` API, you can use it as
      * the driver.
      *
      * @example
      *
      * ```
      * import sqlite from 'sqlite3'
      *
      * const driver = sqlite.Database
      * ```
      */
    @nowarn
    var driver: Any = js.native
    
    /**
      * Valid values are filenames, ":memory:" for an anonymous in-memory
      * database and an empty string for an anonymous disk-based database.
      * Anonymous databases are not persisted and when closing the database
      * handle, their contents are lost.
      */
    @nowarn
    var filename: String = js.native
    
    /**
      * One or more of sqlite3.OPEN_READONLY, sqlite3.OPEN_READWRITE and
      * sqlite3.OPEN_CREATE. The default value is OPEN_READWRITE | OPEN_CREATE.
      */
    @nowarn
    var mode: js.UndefOr[Double] = js.native
  }

  object Config {
    
    @scala.inline
    def apply(driver: Any, filename: String): Config = {
      val __obj = js.Dynamic.literal(driver = driver.asInstanceOf[js.Any], filename = filename.asInstanceOf[js.Any])
      __obj.asInstanceOf[Config]
    }

    @scala.inline
    def apply(filename: String): Config = {
      val __obj = js.Dynamic.literal(driver = Sqlite3Shim.Database.asInstanceOf[js.Any], filename = filename.asInstanceOf[js.Any])
      __obj.asInstanceOf[Config]
    }
  }

  @JSImport("sqlite/build/Database", "Database")
  @js.native
  class Database protected () extends js.Object {
    /**
      * Runs the SQL query with the specified parameters. The parameters are the same as the
      * Database#run function, with the following differences:
      *
      * If the result set is empty, it will be an empty array, otherwise it will
      * have an object for each result row which
      * in turn contains the values of that row, like the Database#get function.
      *
      * Note that it first retrieves all result rows and stores them in memory.
      * For queries that have potentially large result sets, use the Database#each
      * function to retrieve all rows or Database#prepare followed by multiple
      * Statement#get calls to retrieve a previously unknown amount of rows.
      *
      * @param {string} sql The SQL query to run.
      *
      * @param {any} [params, ...] When the SQL statement contains placeholders, you
      * can pass them in here. They will be bound to the statement before it is
      * executed. There are three ways of passing bind parameters: directly in
      * the function's arguments, as an array, and as an object for named
      * parameters. This automatically sanitizes inputs.
      *
      * @see https://github.com/mapbox/node-sqlite3/wiki/API#databaseallsql-param--callback
      */
    @nowarn
    def all(sql: String, params: Any*): js.Promise[js.Any] = js.native

    /**
    * Closes the database.
    */
    def close(): js.Promise[Unit] = js.native

    /**
      * Runs the SQL query with the specified parameters and resolves with
      * with the first result row afterwards. If the result set is empty, returns undefined.
      *
      * The property names correspond to the column names of the result set.
      * It is impossible to access them by column index; the only supported way is by column name.
      *
      * @param {string} sql The SQL query to run.
      *
      * @param {any} [params, ...] When the SQL statement contains placeholders, you
      * can pass them in here. They will be bound to the statement before it is
      * executed. There are three ways of passing bind parameters: directly in
      * the function's arguments, as an array, and as an object for named
      * parameters. This automatically sanitizes inputs.
      *
      * @see https://github.com/mapbox/node-sqlite3/wiki/API#databasegetsql-param--callback
      */
    @nowarn
    def get[T](sql: String, params: Any*): js.Promise[js.UndefOr[js.Any]] = js.native

    /**
      * Runs all SQL queries in the supplied string. No result rows are retrieved. If a query fails,
      * no subsequent statements will be executed (wrap it in a transaction if you want all
      * or none to be executed).
      *
      * Note: This function will only execute statements up to the first NULL byte.
      * Comments are not allowed and will lead to runtime errors.
      *
      * @param {string} sql The SQL query to run.
      * @param {any} [params, ...] Same as the `params` parameter of `all`
      * @see https://github.com/mapbox/node-sqlite3/wiki/API#databaseexecsql-callback
      */
    @nowarn
    def exec(sql: String, params: Any*): js.Promise[Unit] = js.native


    /**
      * Runs the SQL query with the specified parameters. It does not retrieve any result data.
      * The function returns the Database object for which it was called to allow for function chaining.
      *
      * @param {string} sql The SQL query to run.
      *
      * @param {any} [params, ...] When the SQL statement contains placeholders, you
      * can pass them in here. They will be bound to the statement before it is
      * executed. There are three ways of passing bind parameters: directly in
      * the function's arguments, as an array, and as an object for named
      * parameters. This automatically sanitizes inputs.
      *
      * @see https://github.com/mapbox/node-sqlite3/wiki/API#databaserunsql-param--callback
      */
    @nowarn
    def run(sql: String, params: Any*): js.Promise[RunResult] = js.native

  }

    @js.native
    trait RunResult extends js.Object {
      
      /**
        * Number of rows changed.
        *
        * Only contains valid information when the query was a
        * successfully completed UPDATE or DELETE statement.
        */
      @nowarn
      var changes: js.UndefOr[Double] = js.native
    }

}