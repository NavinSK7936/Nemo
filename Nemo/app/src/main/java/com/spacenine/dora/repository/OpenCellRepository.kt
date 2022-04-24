package com.spacenine.dora.repository

import com.spacenine.dora.model.request.CellInfo
import com.spacenine.dora.service.UnwiredLabsService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class OpenCellRepository(
    private val service: UnwiredLabsService
) {
    suspend fun getLocationByCellInfo(cellInfo: CellInfo) = flow {
        val response = service.getLocationByCellInfo(cellInfo)
        emit(response.body())
    }.flowOn(Dispatchers.IO)
}
