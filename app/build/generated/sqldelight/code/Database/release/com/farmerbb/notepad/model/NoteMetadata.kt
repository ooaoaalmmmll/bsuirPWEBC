package com.farmerbb.notepad.model

import com.squareup.sqldelight.ColumnAdapter
import java.util.Date
import kotlin.Boolean
import kotlin.Long
import kotlin.String

public data class NoteMetadata(
  public val metadataId: Long,
  public val title: String,
  public val date: Date,
  public val hasDraft: Boolean
) {
  public override fun toString(): String = """
  |NoteMetadata [
  |  metadataId: $metadataId
  |  title: $title
  |  date: $date
  |  hasDraft: $hasDraft
  |]
  """.trimMargin()

  public class Adapter(
    public val dateAdapter: ColumnAdapter<Date, Long>
  )
}
