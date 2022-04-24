package com.spacenine.dora.view

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.spacenine.dora.repository.OpenCellRepository
import com.spacenine.dora.service.buildUnwiredLabsService
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class CellLocationViewModelFactory : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CellLocationViewModel::class.java)) {
            return CellLocationViewModel(
                OpenCellRepository(buildUnwiredLabsService())
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
