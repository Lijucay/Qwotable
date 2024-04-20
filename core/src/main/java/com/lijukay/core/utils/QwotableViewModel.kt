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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.lijukay.core.database.Qwotable
import kotlinx.coroutines.launch

class QwotableViewModel(
    private val repository: QwotableRepository
) : ViewModel() {
    val observedFavorites: LiveData<List<Qwotable>> = repository.allFavorites.asLiveData()
    val observedOwn: LiveData<List<Qwotable>> = repository.allOwnQwotables.asLiveData()

    init {
        refreshData()
        checkForUpdates()
    }

    private fun refreshData() {
        viewModelScope.launch {
            repository.refreshQwotable()
        }
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

    private fun checkForUpdates() {
        viewModelScope.launch {
            repository.checkForApiUpdates()
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