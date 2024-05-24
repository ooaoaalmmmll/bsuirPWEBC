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

@file:Suppress("DEPRECATION", "OVERRIDE_DEPRECATION")

package com.farmerbb.notepad.android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import com.farmerbb.notepad.model.Note
import com.farmerbb.notepad.model.JsonNote
import com.farmerbb.notepad.ui.routes.NotepadComposeAppRoute
import com.farmerbb.notepad.viewmodel.NotepadViewModel
import com.github.k1rakishou.fsaf.FileChooser
import com.github.k1rakishou.fsaf.callback.FSAFActivityCallbacks
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.util.Identity.decode
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.decodeToSequence
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.koin.android.ext.android.get
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.Date
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.future.*



class NotepadActivity: ComponentActivity(), FSAFActivityCallbacks {
    private val vm: NotepadViewModel by viewModel()
    private val fileChooser: FileChooser = get()
    private val supaBaseURL: String = "https://sofsuvthvjgumbdidhac.supabase.co"
    private val supaBaseKEY: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InNvZnN1dnRodmpndW1iZGlkaGFjIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MTUzNDg0NzgsImV4cCI6MjAzMDkyNDQ3OH0.8lKtFA0W6xVHn0plJ9DpHyfMB_DDzFB0MI8ZSnDStvg"
    private val TABLENAME: String = "notesSupabaseTable"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        fileChooser.setCallbacks(this)

        vm.migrateData {
            setContent {
                NotepadComposeAppRoute()
            }
        }
        getData()
    }

    private fun getData() {
        vm.fetchLastNoteInLocalDB()
        lifecycleScope.launch {
            val client = getClient()
                Log.v("supabase","clientOK")
            val supaBaseResponse = client.postgrest[TABLENAME].select()
                Log.v("supabase","response = "+ supaBaseResponse.toString())

            val deserialized = supaBaseResponse.decodeList<JsonNote>()
                Log.v("supabase",deserialized.toString())
            val notes = deserialized.map { jsonNote ->
                Note(jsonNote)
            }

            vm.updateNotes(notes)
            Log.v("supabase",vm.notes.value.toString())
            vm.chooseAndFetchDB()

        }

    }

   private fun getClient(): SupabaseClient {
        return createSupabaseClient(supaBaseURL, supaBaseKEY){
            install(Postgrest)
        }
    }

    override fun onStart() {
        super.onStart()
        vm.deleteDraft()
    }

    override fun onStop() {
        super.onStop()
        if (!isChangingConfigurations) {
            vm.saveDraft()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        fileChooser.removeCallbacks()
    }

    override fun fsafStartActivityForResult(intent: Intent, requestCode: Int) {
        when(intent.action) {
            Intent.ACTION_OPEN_DOCUMENT -> intent.type = "text/plain"
            Intent.ACTION_OPEN_DOCUMENT_TREE -> intent.removeExtra(Intent.EXTRA_LOCAL_ONLY)
        }

        startActivityForResult(intent, requestCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        fileChooser.onActivityResult(requestCode, resultCode, data)
    }

    override fun dispatchKeyShortcutEvent(event: KeyEvent): Boolean {
        return if (event.action == KeyEvent.ACTION_DOWN && event.isCtrlPressed) {
            vm.keyboardShortcutPressed(event.keyCode)
        } else {
            super.dispatchKeyShortcutEvent(event)
        }
    }
}