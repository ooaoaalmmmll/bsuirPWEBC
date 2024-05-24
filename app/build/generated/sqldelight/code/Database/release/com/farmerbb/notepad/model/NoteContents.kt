package com.farmerbb.notepad.model

import kotlin.Long
import kotlin.String

public data class NoteContents(
  public val contentsId: Long,
  public val text: String?,
  public val draftText: String?
) {
  public override fun toString(): String = """
  |NoteContents [
  |  contentsId: $contentsId
  |  text: $text
  |  draftText: $draftText
  |]
  """.trimMargin()
}
