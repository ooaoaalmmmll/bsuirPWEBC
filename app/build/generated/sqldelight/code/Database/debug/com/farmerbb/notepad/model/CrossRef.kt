package com.farmerbb.notepad.model

import kotlin.Long
import kotlin.String

public data class CrossRef(
  public val metadataId: Long,
  public val contentsId: Long
) {
  public override fun toString(): String = """
  |CrossRef [
  |  metadataId: $metadataId
  |  contentsId: $contentsId
  |]
  """.trimMargin()
}
