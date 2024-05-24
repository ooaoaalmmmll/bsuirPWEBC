package com.farmerbb.notepad.model

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import java.util.Date
import kotlin.Any
import kotlin.Boolean
import kotlin.Long
import kotlin.String
import kotlin.Unit
import kotlin.collections.Collection

public interface NoteMetadataQueries : Transacter {
  public fun <T : Any> getSortedByTitleDescending(mapper: (
    metadataId: Long,
    title: String,
    date: Date,
    hasDraft: Boolean
  ) -> T): Query<T>

  public fun getSortedByTitleDescending(): Query<NoteMetadata>

  public fun <T : Any> getSortedByTitleAscending(mapper: (
    metadataId: Long,
    title: String,
    date: Date,
    hasDraft: Boolean
  ) -> T): Query<T>

  public fun getSortedByTitleAscending(): Query<NoteMetadata>

  public fun <T : Any> getSortedByDateDescending(mapper: (
    metadataId: Long,
    title: String,
    date: Date,
    hasDraft: Boolean
  ) -> T): Query<T>

  public fun getSortedByDateDescending(): Query<NoteMetadata>

  public fun <T : Any> getSortedByDateAscending(mapper: (
    metadataId: Long,
    title: String,
    date: Date,
    hasDraft: Boolean
  ) -> T): Query<T>

  public fun getSortedByDateAscending(): Query<NoteMetadata>

  public fun <T : Any> `get`(id: Long, mapper: (
    metadataId: Long,
    title: String,
    date: Date,
    hasDraft: Boolean
  ) -> T): Query<T>

  public fun `get`(id: Long): Query<NoteMetadata>

  public fun getDraftId(): Query<Long>

  public fun getIndex(): Query<Long>

  public fun update(NoteMetadata: NoteMetadata): Unit

  public fun insert(NoteMetadata: NoteMetadata): Unit

  public fun delete(id: Long): Unit

  public fun deleteMultiple(metadataId: Collection<Long>): Unit
}
