/* Copyright 2021 Braden Farmer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.farmerbb.notepad.model

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.util.Date


data class Note(
    val metadata: NoteMetadata = Defaults.metadata,
    private val contents: NoteContents = Defaults.contents
) {
    constructor(jNote: JsonNote) : this(
    metadata = NoteMetadata(jNote.id, jNote.title, Date(jNote.date), false
    ),
    contents = NoteContents(jNote.id, jNote.text, null)
)
    val id: Long get() = metadata.metadataId
    val text: String get() = contents.text ?: ""
    val draftText: String get() = contents.draftText ?: ""
    val title: String get() = metadata.title
    val date: Date get() = metadata.date

}

@Serializable
public data class JsonNote(
    public val id: Long = 0,
    public val text: String = "",
    public val draftText: String = "",
    public val title: String= "",
    public val date: Long =0,
){
}


