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

package com.lijukay.quotesAltDesign.presentation.viewmodels

import androidx.lifecycle.ViewModel
import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.data.local.model.LocalQwotable
import com.lijukay.quotesAltDesign.data.local.model.OwnQwotable
import com.lijukay.quotesAltDesign.data.repository.QwotableRepository
import com.lijukay.quotesAltDesign.data.shared.DefaultQwotable
import com.lijukay.quotesAltDesign.data.shared.Qwotable
import com.lijukay.quotesAltDesign.data.utils.StringValue
import com.lijukay.quotesAltDesign.domain.util.states.QwotableResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QwotableViewModel @Inject constructor(
    private val repository: QwotableRepository
) : ViewModel() {

    private val _languageFilteredQwotables = MutableStateFlow(listOf<LocalQwotable>())
    val languageFilteredQwotables: StateFlow<List<LocalQwotable>> = _languageFilteredQwotables

    private val _favoriteQwotables = MutableStateFlow(listOf<LocalQwotable>())
    val favoriteQwotables: StateFlow<List<LocalQwotable>> = _favoriteQwotables

    private val _ownQwotables = MutableStateFlow(listOf<OwnQwotable>())
    val ownQwotables: StateFlow<List<OwnQwotable>> = _ownQwotables

    private val _randomQuote = MutableStateFlow<Qwotable>(DefaultQwotable())
        val randomQuote: StateFlow<Qwotable> = _randomQuote

    private val _error = MutableStateFlow(false)
    val error: StateFlow<Boolean> = _error

    private val _errorMessage = MutableStateFlow<StringValue?>(null)
    val errorMessage: StateFlow<StringValue?> = _errorMessage

    private val _updateAvailable = MutableStateFlow(false)
    val updateAvailable: StateFlow<Boolean> = _updateAvailable

    fun loadLanguageFilteredQwotables(language: String) {
        CoroutineScope(Dispatchers.IO).launch {
            repository.loadLanguageFilteredQwotables(language)
                .collect { _languageFilteredQwotables.value = it }
        }
    }

    fun loadFavoriteQwotables() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.loadFavoriteQwotables().collect { _favoriteQwotables.value = it }
        }
    }

    fun loadOwnQwotables() {
        CoroutineScope(Dispatchers.IO).launch {
            repository.loadOwnQwotables().collect { _ownQwotables.value = it }
        }
    }

    fun insertQwotable(localQwotable: Qwotable) = CoroutineScope(Dispatchers.IO).launch {
        val result = when (localQwotable) {
            is LocalQwotable -> repository.insertQwotable(localQwotable)
            else -> false
        }

        if (!result) setError(StringValue.StringResource(R.string.insert_failure))
    }

    fun updateQwotable(localQwotable: Qwotable) = CoroutineScope(Dispatchers.IO).launch {
        val result = when (localQwotable) {
            is LocalQwotable -> repository.updateQwotable(localQwotable)
            else -> false
        }
        if (!result) setError(StringValue.StringResource(R.string.update_failure))
    }

    fun deleteQwotable(localQwotable: Qwotable) = CoroutineScope(Dispatchers.IO).launch {
        val result = when (localQwotable) {
            is LocalQwotable -> repository.deleteQwotable(localQwotable)
            else -> false
        }

        if (!result) setError(StringValue.StringResource(R.string.deletion_failure))
    }

    fun refreshQwotables() = CoroutineScope(Dispatchers.IO).launch {
        when (val result = repository.refreshQwotables()) {
            is QwotableResult.Failure -> setError(result.message)
            is QwotableResult.Success -> {
                result.data?.let { _updateAvailable.value = it }
            }
        }
    }

    fun checkForUpdates() = CoroutineScope(Dispatchers.IO).launch {
        _updateAvailable.value = repository.checkForAPIUpdate()
    }

    fun updateQwotableDatabase() = CoroutineScope(Dispatchers.IO).launch {
        _updateAvailable.value = false
        val result = repository.updateQwotableDatabase()

        if (result is QwotableResult.Failure) {
            setError(result.message)
        }
    }

    fun hideUpdateAvailable() {
        _updateAvailable.value = false // Will reset on app restart or on another manual check
    }

    fun resetError() {
        _error.value = false
        _errorMessage.value = null
    }

    fun getRandomQuote() = CoroutineScope(Dispatchers.IO).launch {
        when (val result = repository.getRandomQuote()) {
            is QwotableResult.Success -> _randomQuote.value = result.data
            is QwotableResult.Failure -> setError(result.message)
        }
    }

    private fun setError(message: StringValue) {
        _error.value = true
        _errorMessage.value = message
    }}