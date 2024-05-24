package com.farmerbb.notepad.model

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Long
import kotlin.String
import kotlin.Unit
import kotlin.collections.Collection

public interface NoteContentsQueries : Transacter {
  public fun <T : Any> `get`(id: Long, mapper: (
    contentsId: Long,
    text: String?,
    draftText: String?
  ) -> T): Query<T>

  public fun `get`(id: Long): Query<NoteContents>

  public fun <T : Any> getMultiple(contentsId: Collection<Long>, mapper: (
    contentsId: Long,
    text: String?,
    draftText: String?
  ) -> T): Query<T>

  public fun getMultiple(contentsId: Collection<Long>): Query<NoteContents>

  public fun getIndex(): Query<Long>

  public fun update(NoteContents: NoteContents): Unit

  public fun insert(NoteContents: NoteContents): Unit

  public fun delete(id: Long): Unit

  public fun deleteMultiple(contentsId: Collection<Long>): Unit
}
