package com.farmerbb.notepad.app

import com.farmerbb.notepad.Database
import com.farmerbb.notepad.model.CrossRef
import com.farmerbb.notepad.model.CrossRefQueries
import com.farmerbb.notepad.model.NoteContents
import com.farmerbb.notepad.model.NoteContentsQueries
import com.farmerbb.notepad.model.NoteMetadata
import com.farmerbb.notepad.model.NoteMetadataQueries
import com.squareup.sqldelight.Query
import com.squareup.sqldelight.TransacterImpl
import com.squareup.sqldelight.`internal`.copyOnWriteList
import com.squareup.sqldelight.db.SqlCursor
import com.squareup.sqldelight.db.SqlDriver
import java.util.Date
import kotlin.Any
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Unit
import kotlin.collections.Collection
import kotlin.collections.MutableList
import kotlin.reflect.KClass

internal val KClass<Database>.schema: SqlDriver.Schema
  get() = DatabaseImpl.Schema

internal fun KClass<Database>.newInstance(driver: SqlDriver,
    NoteMetadataAdapter: NoteMetadata.Adapter): Database = DatabaseImpl(driver, NoteMetadataAdapter)

private class DatabaseImpl(
  driver: SqlDriver,
  internal val NoteMetadataAdapter: NoteMetadata.Adapter
) : TransacterImpl(driver), Database {
  public override val crossRefQueries: CrossRefQueriesImpl = CrossRefQueriesImpl(this, driver)

  public override val noteContentsQueries: NoteContentsQueriesImpl = NoteContentsQueriesImpl(this,
      driver)

  public override val noteMetadataQueries: NoteMetadataQueriesImpl = NoteMetadataQueriesImpl(this,
      driver)

  public object Schema : SqlDriver.Schema {
    public override val version: Int
      get() = 1

    public override fun create(driver: SqlDriver): Unit {
      driver.execute(null, """
          |CREATE TABLE IF NOT EXISTS CrossRef(
          |    metadataId INTEGER NOT NULL,
          |    contentsId INTEGER NOT NULL,
          |    PRIMARY KEY(metadataId, contentsId)
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE IF NOT EXISTS NoteContents(
          |    contentsId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
          |    text TEXT,
          |    draftText TEXT
          |)
          """.trimMargin(), 0)
      driver.execute(null, """
          |CREATE TABLE IF NOT EXISTS NoteMetadata(
          |    metadataId INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
          |    title TEXT NOT NULL,
          |    date INTEGER NOT NULL,
          |    hasDraft INTEGER NOT NULL
          |)
          """.trimMargin(), 0)
      driver.execute(null,
          "CREATE INDEX IF NOT EXISTS index_CrossRef_contentsId ON CrossRef(contentsId)", 0)
    }

    public override fun migrate(
      driver: SqlDriver,
      oldVersion: Int,
      newVersion: Int
    ): Unit {
    }
  }
}

private class CrossRefQueriesImpl(
  private val database: DatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), CrossRefQueries {
  internal val `get`: MutableList<Query<*>> = copyOnWriteList()

  internal val getMultiple: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> `get`(id: Long, mapper: (metadataId: Long, contentsId: Long) -> T):
      Query<T> = GetQuery(id) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getLong(1)!!
    )
  }

  public override fun `get`(id: Long): Query<CrossRef> = get(id) { metadataId, contentsId ->
    CrossRef(
      metadataId,
      contentsId
    )
  }

  public override fun <T : Any> getMultiple(metadataId: Collection<Long>, mapper: (metadataId: Long,
      contentsId: Long) -> T): Query<T> = GetMultipleQuery(metadataId) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getLong(1)!!
    )
  }

  public override fun getMultiple(metadataId: Collection<Long>): Query<CrossRef> =
      getMultiple(metadataId) { metadataId_, contentsId ->
    CrossRef(
      metadataId_,
      contentsId
    )
  }

  public override fun insert(CrossRef: CrossRef): Unit {
    driver.execute(49125078,
        """INSERT OR REPLACE INTO CrossRef(metadataId, contentsId) VALUES (?, ?)""", 2) {
      bindLong(1, CrossRef.metadataId)
      bindLong(2, CrossRef.contentsId)
    }
    notifyQueries(49125078, {database.crossRefQueries.get + database.crossRefQueries.getMultiple})
  }

  public override fun delete(id: Long): Unit {
    driver.execute(-102540856, """DELETE FROM CrossRef WHERE metadataId = ?""", 1) {
      bindLong(1, id)
    }
    notifyQueries(-102540856, {database.crossRefQueries.get + database.crossRefQueries.getMultiple})
  }

  public override fun deleteMultiple(metadataId: Collection<Long>): Unit {
    val metadataIdIndexes = createArguments(count = metadataId.size)
    driver.execute(null, """DELETE FROM CrossRef WHERE metadataId IN $metadataIdIndexes""",
        metadataId.size) {
      metadataId.forEachIndexed { index, metadataId_ ->
          bindLong(index + 1, metadataId_)
          }
    }
    notifyQueries(834935800, {database.crossRefQueries.get + database.crossRefQueries.getMultiple})
  }

  private inner class GetQuery<out T : Any>(
    public val id: Long,
    mapper: (SqlCursor) -> T
  ) : Query<T>(get, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(-1307477927,
        """SELECT * FROM CrossRef WHERE metadataId = ?""", 1) {
      bindLong(1, id)
    }

    public override fun toString(): String = "CrossRef.sq:get"
  }

  private inner class GetMultipleQuery<out T : Any>(
    public val metadataId: Collection<Long>,
    mapper: (SqlCursor) -> T
  ) : Query<T>(getMultiple, mapper) {
    public override fun execute(): SqlCursor {
      val metadataIdIndexes = createArguments(count = metadataId.size)
      return driver.executeQuery(null,
          """SELECT * FROM CrossRef WHERE metadataId IN $metadataIdIndexes""", metadataId.size) {
        metadataId.forEachIndexed { index, metadataId_ ->
            bindLong(index + 1, metadataId_)
            }
      }
    }

    public override fun toString(): String = "CrossRef.sq:getMultiple"
  }
}

private class NoteContentsQueriesImpl(
  private val database: DatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), NoteContentsQueries {
  internal val `get`: MutableList<Query<*>> = copyOnWriteList()

  internal val getMultiple: MutableList<Query<*>> = copyOnWriteList()

  internal val getIndex: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> `get`(id: Long, mapper: (
    contentsId: Long,
    text: String?,
    draftText: String?
  ) -> T): Query<T> = GetQuery(id) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1),
      cursor.getString(2)
    )
  }

  public override fun `get`(id: Long): Query<NoteContents> = get(id) { contentsId, text,
      draftText ->
    NoteContents(
      contentsId,
      text,
      draftText
    )
  }

  public override fun <T : Any> getMultiple(contentsId: Collection<Long>, mapper: (
    contentsId: Long,
    text: String?,
    draftText: String?
  ) -> T): Query<T> = GetMultipleQuery(contentsId) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1),
      cursor.getString(2)
    )
  }

  public override fun getMultiple(contentsId: Collection<Long>): Query<NoteContents> =
      getMultiple(contentsId) { contentsId_, text, draftText ->
    NoteContents(
      contentsId_,
      text,
      draftText
    )
  }

  public override fun getIndex(): Query<Long> = Query(-1799462542, getIndex, driver,
      "NoteContents.sq", "getIndex", "SELECT last_insert_rowid()") { cursor ->
    cursor.getLong(0)!!
  }

  public override fun update(NoteContents: NoteContents): Unit {
    driver.execute(569534623,
        """INSERT OR REPLACE INTO NoteContents(contentsId, text, draftText) VALUES (?, ?, ?)""", 3)
        {
      bindLong(1, NoteContents.contentsId)
      bindString(2, NoteContents.text)
      bindString(3, NoteContents.draftText)
    }
    notifyQueries(569534623, {database.noteContentsQueries.getMultiple +
        database.noteContentsQueries.get})
  }

  public override fun insert(NoteContents: NoteContents): Unit {
    driver.execute(224588431,
        """INSERT OR REPLACE INTO NoteContents(text, draftText) VALUES (?, ?)""", 2) {
      bindString(1, NoteContents.text)
      bindString(2, NoteContents.draftText)
    }
    notifyQueries(224588431, {database.noteContentsQueries.getMultiple +
        database.noteContentsQueries.get})
  }

  public override fun delete(id: Long): Unit {
    driver.execute(72922497, """DELETE FROM NoteContents WHERE contentsId = ?""", 1) {
      bindLong(1, id)
    }
    notifyQueries(72922497, {database.noteContentsQueries.getMultiple +
        database.noteContentsQueries.get})
  }

  public override fun deleteMultiple(contentsId: Collection<Long>): Unit {
    val contentsIdIndexes = createArguments(count = contentsId.size)
    driver.execute(null, """DELETE FROM NoteContents WHERE contentsId IN $contentsIdIndexes""",
        contentsId.size) {
      contentsId.forEachIndexed { index, contentsId_ ->
          bindLong(index + 1, contentsId_)
          }
    }
    notifyQueries(1881321137, {database.noteContentsQueries.getMultiple +
        database.noteContentsQueries.get})
  }

  private inner class GetQuery<out T : Any>(
    public val id: Long,
    mapper: (SqlCursor) -> T
  ) : Query<T>(get, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(-1728880832,
        """SELECT * FROM NoteContents WHERE contentsId = ?""", 1) {
      bindLong(1, id)
    }

    public override fun toString(): String = "NoteContents.sq:get"
  }

  private inner class GetMultipleQuery<out T : Any>(
    public val contentsId: Collection<Long>,
    mapper: (SqlCursor) -> T
  ) : Query<T>(getMultiple, mapper) {
    public override fun execute(): SqlCursor {
      val contentsIdIndexes = createArguments(count = contentsId.size)
      return driver.executeQuery(null,
          """SELECT * FROM NoteContents WHERE contentsId IN $contentsIdIndexes""", contentsId.size)
          {
        contentsId.forEachIndexed { index, contentsId_ ->
            bindLong(index + 1, contentsId_)
            }
      }
    }

    public override fun toString(): String = "NoteContents.sq:getMultiple"
  }
}

private class NoteMetadataQueriesImpl(
  private val database: DatabaseImpl,
  private val driver: SqlDriver
) : TransacterImpl(driver), NoteMetadataQueries {
  internal val getSortedByTitleDescending: MutableList<Query<*>> = copyOnWriteList()

  internal val getSortedByTitleAscending: MutableList<Query<*>> = copyOnWriteList()

  internal val getSortedByDateDescending: MutableList<Query<*>> = copyOnWriteList()

  internal val getSortedByDateAscending: MutableList<Query<*>> = copyOnWriteList()

  internal val `get`: MutableList<Query<*>> = copyOnWriteList()

  internal val getDraftId: MutableList<Query<*>> = copyOnWriteList()

  internal val getIndex: MutableList<Query<*>> = copyOnWriteList()

  public override fun <T : Any> getSortedByTitleDescending(mapper: (
    metadataId: Long,
    title: String,
    date: Date,
    hasDraft: Boolean
  ) -> T): Query<T> = Query(1341352657, getSortedByTitleDescending, driver, "NoteMetadata.sq",
      "getSortedByTitleDescending", "SELECT * FROM NoteMetadata ORDER BY title DESC") { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      database.NoteMetadataAdapter.dateAdapter.decode(cursor.getLong(2)!!),
      cursor.getLong(3)!! == 1L
    )
  }

  public override fun getSortedByTitleDescending(): Query<NoteMetadata> =
      getSortedByTitleDescending { metadataId, title, date, hasDraft ->
    NoteMetadata(
      metadataId,
      title,
      date,
      hasDraft
    )
  }

  public override fun <T : Any> getSortedByTitleAscending(mapper: (
    metadataId: Long,
    title: String,
    date: Date,
    hasDraft: Boolean
  ) -> T): Query<T> = Query(-618389537, getSortedByTitleAscending, driver, "NoteMetadata.sq",
      "getSortedByTitleAscending", "SELECT * FROM NoteMetadata ORDER BY title ASC") { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      database.NoteMetadataAdapter.dateAdapter.decode(cursor.getLong(2)!!),
      cursor.getLong(3)!! == 1L
    )
  }

  public override fun getSortedByTitleAscending(): Query<NoteMetadata> = getSortedByTitleAscending {
      metadataId, title, date, hasDraft ->
    NoteMetadata(
      metadataId,
      title,
      date,
      hasDraft
    )
  }

  public override fun <T : Any> getSortedByDateDescending(mapper: (
    metadataId: Long,
    title: String,
    date: Date,
    hasDraft: Boolean
  ) -> T): Query<T> = Query(-1827244603, getSortedByDateDescending, driver, "NoteMetadata.sq",
      "getSortedByDateDescending", "SELECT * FROM NoteMetadata ORDER BY date DESC") { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      database.NoteMetadataAdapter.dateAdapter.decode(cursor.getLong(2)!!),
      cursor.getLong(3)!! == 1L
    )
  }

  public override fun getSortedByDateDescending(): Query<NoteMetadata> = getSortedByDateDescending {
      metadataId, title, date, hasDraft ->
    NoteMetadata(
      metadataId,
      title,
      date,
      hasDraft
    )
  }

  public override fun <T : Any> getSortedByDateAscending(mapper: (
    metadataId: Long,
    title: String,
    date: Date,
    hasDraft: Boolean
  ) -> T): Query<T> = Query(-1967528341, getSortedByDateAscending, driver, "NoteMetadata.sq",
      "getSortedByDateAscending", "SELECT * FROM NoteMetadata ORDER BY date ASC") { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      database.NoteMetadataAdapter.dateAdapter.decode(cursor.getLong(2)!!),
      cursor.getLong(3)!! == 1L
    )
  }

  public override fun getSortedByDateAscending(): Query<NoteMetadata> = getSortedByDateAscending {
      metadataId, title, date, hasDraft ->
    NoteMetadata(
      metadataId,
      title,
      date,
      hasDraft
    )
  }

  public override fun <T : Any> `get`(id: Long, mapper: (
    metadataId: Long,
    title: String,
    date: Date,
    hasDraft: Boolean
  ) -> T): Query<T> = GetQuery(id) { cursor ->
    mapper(
      cursor.getLong(0)!!,
      cursor.getString(1)!!,
      database.NoteMetadataAdapter.dateAdapter.decode(cursor.getLong(2)!!),
      cursor.getLong(3)!! == 1L
    )
  }

  public override fun `get`(id: Long): Query<NoteMetadata> = get(id) { metadataId, title, date,
      hasDraft ->
    NoteMetadata(
      metadataId,
      title,
      date,
      hasDraft
    )
  }

  public override fun getDraftId(): Query<Long> = Query(463210129, getDraftId, driver,
      "NoteMetadata.sq", "getDraftId", "SELECT metadataId FROM NoteMetadata WHERE hasDraft = 1") {
      cursor ->
    cursor.getLong(0)!!
  }

  public override fun getIndex(): Query<Long> = Query(4983303, getIndex, driver, "NoteMetadata.sq",
      "getIndex", "SELECT last_insert_rowid()") { cursor ->
    cursor.getLong(0)!!
  }

  public override fun update(NoteMetadata: NoteMetadata): Unit {
    driver.execute(-796183948,
        """INSERT OR REPLACE INTO NoteMetadata(metadataId, title, date, hasDraft) VALUES (?, ?, ?, ?)""",
        4) {
      bindLong(1, NoteMetadata.metadataId)
      bindString(2, NoteMetadata.title)
      bindLong(3, database.NoteMetadataAdapter.dateAdapter.encode(NoteMetadata.date))
      bindLong(4, if (NoteMetadata.hasDraft) 1L else 0L)
    }
    notifyQueries(-796183948, {database.noteMetadataQueries.getSortedByDateAscending +
        database.noteMetadataQueries.getSortedByDateDescending +
        database.noteMetadataQueries.getSortedByTitleAscending + database.noteMetadataQueries.get +
        database.noteMetadataQueries.getDraftId +
        database.noteMetadataQueries.getSortedByTitleDescending})
  }

  public override fun insert(NoteMetadata: NoteMetadata): Unit {
    driver.execute(-1141130140,
        """INSERT OR REPLACE INTO NoteMetadata(title, date, hasDraft) VALUES (?, ?, ?)""", 3) {
      bindString(1, NoteMetadata.title)
      bindLong(2, database.NoteMetadataAdapter.dateAdapter.encode(NoteMetadata.date))
      bindLong(3, if (NoteMetadata.hasDraft) 1L else 0L)
    }
    notifyQueries(-1141130140, {database.noteMetadataQueries.getSortedByDateAscending +
        database.noteMetadataQueries.getSortedByDateDescending +
        database.noteMetadataQueries.getSortedByTitleAscending + database.noteMetadataQueries.get +
        database.noteMetadataQueries.getDraftId +
        database.noteMetadataQueries.getSortedByTitleDescending})
  }

  public override fun delete(id: Long): Unit {
    driver.execute(-1292796074, """DELETE FROM NoteMetadata WHERE metadataId = ?""", 1) {
      bindLong(1, id)
    }
    notifyQueries(-1292796074, {database.noteMetadataQueries.getSortedByDateAscending +
        database.noteMetadataQueries.getSortedByDateDescending +
        database.noteMetadataQueries.getSortedByTitleAscending + database.noteMetadataQueries.get +
        database.noteMetadataQueries.getDraftId +
        database.noteMetadataQueries.getSortedByTitleDescending})
  }

  public override fun deleteMultiple(metadataId: Collection<Long>): Unit {
    val metadataIdIndexes = createArguments(count = metadataId.size)
    driver.execute(null, """DELETE FROM NoteMetadata WHERE metadataId IN $metadataIdIndexes""",
        metadataId.size) {
      metadataId.forEachIndexed { index, metadataId_ ->
          bindLong(index + 1, metadataId_)
          }
    }
    notifyQueries(517133190, {database.noteMetadataQueries.getSortedByDateAscending +
        database.noteMetadataQueries.getSortedByDateDescending +
        database.noteMetadataQueries.getSortedByTitleAscending + database.noteMetadataQueries.get +
        database.noteMetadataQueries.getDraftId +
        database.noteMetadataQueries.getSortedByTitleDescending})
  }

  private inner class GetQuery<out T : Any>(
    public val id: Long,
    mapper: (SqlCursor) -> T
  ) : Query<T>(get, mapper) {
    public override fun execute(): SqlCursor = driver.executeQuery(55176587,
        """SELECT * FROM NoteMetadata WHERE metadataId = ?""", 1) {
      bindLong(1, id)
    }

    public override fun toString(): String = "NoteMetadata.sq:get"
  }
}
