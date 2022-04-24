package com.spacenine.dora.view

import com.spacenine.dora.model.response.CellLocation

sealed class State {
    object Loading : State()
    data class Success(val response: CellLocation) : State()
    data class Failed(val message: String) : State()
}
