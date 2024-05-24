package com.farmerbb.notepad

import com.farmerbb.notepad.app.newInstance
import com.farmerbb.notepad.app.schema
import com.farmerbb.notepad.model.CrossRefQueries
import com.farmerbb.notepad.model.NoteContentsQueries
import com.farmerbb.notepad.model.NoteMetadata
import com.farmerbb.notepad.model.NoteMetadataQueries
import com.squareup.sqldelight.Transacter
import com.squareup.sqldelight.db.SqlDriver

public interface Database : Transacter {
  public val crossRefQueries: CrossRefQueries

  public val noteContentsQueries: NoteContentsQueries

  public val noteMetadataQueries: NoteMetadataQueries

  public companion object {
    public val Schema: SqlDriver.Schema
      get() = Database::class.schema

    public operator fun invoke(driver: SqlDriver, NoteMetadataAdapter: NoteMetadata.Adapter):
        Database = Database::class.newInstance(driver, NoteMetadataAdapter)
  }
}
