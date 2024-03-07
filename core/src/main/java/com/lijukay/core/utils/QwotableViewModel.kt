package com.lijukay.core.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lijukay.core.database.Qwotable
import kotlinx.coroutines.launch

class QwotableViewModel(
    private val repository: QwotableRepository
): ViewModel() {
    val observedQwotables: LiveData<List<Qwotable>> = repository.allQwotables.asLiveData()
    val observedFavorites: LiveData<List<Qwotable>> = repository.allFavorites.asLiveData()
    val observedOwn: LiveData<List<Qwotable>> = repository.allOwnQwotables.asLiveData()

    private val allQwotables: LiveData<List<Qwotable>> = repository.allQwotables.asLiveData()

    init {
        refreshData()
        checkForUpdates()
    }

    private fun refreshData() {
        viewModelScope.launch {
            repository.refreshQwotable()
        }
    }

    fun updateQwotable(qwotable: Qwotable) {
        viewModelScope.launch {
            repository.updateQwotable(qwotable)
        }
    }

    private fun checkForUpdates() {
        viewModelScope.launch {
            repository.checkForApiUpdates()
        }
    }

    fun insert(qwotables: List<Qwotable>) = viewModelScope.launch {
        repository.insert(qwotables)
    }

    fun getAllQwotables(): LiveData<List<Qwotable>> {
        return allQwotables
    }
}

class QwotableViewModelFactory(private val repository: QwotableRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QwotableViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return QwotableViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}