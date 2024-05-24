package com.farmerbb.notepad.model

import com.squareup.sqldelight.Query
import com.squareup.sqldelight.Transacter
import kotlin.Any
import kotlin.Long
import kotlin.Unit
import kotlin.collections.Collection

public interface CrossRefQueries : Transacter {
  public fun <T : Any> `get`(id: Long, mapper: (metadataId: Long, contentsId: Long) -> T): Query<T>

  public fun `get`(id: Long): Query<CrossRef>

  public fun <T : Any> getMultiple(metadataId: Collection<Long>, mapper: (metadataId: Long,
      contentsId: Long) -> T): Query<T>

  public fun getMultiple(metadataId: Collection<Long>): Query<CrossRef>

  public fun insert(CrossRef: CrossRef): Unit

  public fun delete(id: Long): Unit

  public fun deleteMultiple(metadataId: Collection<Long>): Unit
}
