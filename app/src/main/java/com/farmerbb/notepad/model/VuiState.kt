
package com.farmerbb.notepad.model

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver

private const val ASM = "Assblng"
private const val DISASM = "Disassblng"


sealed interface VuiState {
    object off: VuiState
    data class Asm(val id: Long): VuiState
    data class Disasm(val id: Long? = null): VuiState
}

val VuiStateSaver = Saver<MutableState<VuiState>, Pair<String, Long?>>(
    save = {
        when(val state = it.value) {
            is VuiState.Asm -> ASM to state.id
            is VuiState.Disasm -> DISASM to state.id
            else -> "" to null
        }
    },
    restore = {
        mutableStateOf(
            when(it.first) {
                ASM -> VuiState.Asm(it.second ?: 0)
                DISASM -> VuiState.Disasm(it.second)
                else -> VuiState.off
            }
        )
    }
)
