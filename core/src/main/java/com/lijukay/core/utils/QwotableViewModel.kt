/*
* Copyright (C) 2024 Lijucay (Luca)
*
*   This program is free software: you can redistribute it and/or modify
*   it under the terms of the GNU General Public License as published by
*   the Free Software Foundation, either version 3 of the License, or
*   (at your option) any later version.
*
*   This program is distributed in the hope that it will be useful,
*   but WITHOUT ANY WARRANTY; without even the implied warranty of
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*   GNU General Public License for more details.
*
*   You should have received a copy of the GNU General Public License
*   along with this program.  If not, see <https://www.gnu.org/licenses/>
* */

package com.lijukay.core.utils

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lijukay.core.database.Qwotable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QwotableViewModel(
    private val repository: QwotableRepository
) : ViewModel() {
    private val _qwotableFileUpdateAvailable = MutableLiveData(false)
    private val _tempNewVersion: MutableLiveData<Int> = MutableLiveData(0)

    val qwotableFileUpdateAvailable: LiveData<Boolean> = _qwotableFileUpdateAvailable

    val observedFavorites: LiveData<List<Qwotable>> = repository.allFavorites.asLiveData()
    val observedOwn: LiveData<List<Qwotable>> = repository.allOwnQwotables.asLiveData()

    init {
        refreshData()
        checkForUpdates(onUpdateCheckCompleted = null)
    }

    private fun refreshData() {
        viewModelScope.launch {
            repository.refreshQwotable()
        }
    }

    fun updateFileUpdateAvailability(isUpdateAvailable: Boolean, newVersion: Int) {
        _qwotableFileUpdateAvailable.value = isUpdateAvailable
        _tempNewVersion.value = newVersion
    }

    fun updateFileUpdateAvailability(isUpdateAvailable: Boolean) {
        _qwotableFileUpdateAvailable.value = isUpdateAvailable
    }

    fun insert(qwotable: Qwotable) {
        viewModelScope.launch {
            repository.insert(qwotable)
        }
    }

    fun updateQwotable(qwotable: Qwotable) {
        viewModelScope.launch {
            repository.updateQwotable(qwotable)
        }
    }

    fun checkForUpdates(onUpdateCheckCompleted: ((Boolean) -> Unit)?) {
        viewModelScope.launch {
            repository.checkForApiUpdates(this@QwotableViewModel) { updateAvailable, newVersion ->
                CoroutineScope(Dispatchers.Main).launch {
                    if (onUpdateCheckCompleted != null) {
                        onUpdateCheckCompleted(updateAvailable)
                    }
                    updateFileUpdateAvailability(updateAvailable, newVersion)
                }
            }
        }
    }

    fun deleteSingleQwotable(qwotable: Qwotable) {
        viewModelScope.launch {
            repository.deleteSingleQwotable(qwotable)
        }
    }

    fun getFilteredQwotable(language: String): LiveData<List<Qwotable>> {
        return repository.getFilteredQwotable(language).asLiveData()
    }

    suspend fun updateQwotableDatabase() {
        repository.updateQwotableDatabase(_tempNewVersion.value!!, this)
    }
}

class QwotableViewModelFactory(private val repository: QwotableRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(QwotableViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return QwotableViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}