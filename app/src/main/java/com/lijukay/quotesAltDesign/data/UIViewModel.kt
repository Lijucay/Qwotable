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

package com.lijukay.quotesAltDesign.data

import android.content.Context
import androidx.annotation.StringRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lijukay.core.R
import com.lijukay.core.database.Qwotable
import com.lijukay.core.utils.RandomQuote
import com.lijukay.quotesAltDesign.data.model.Screens

class UIViewModel : ViewModel() {
    private val _showFilterBottomSheet = MutableLiveData(false)
    private val _showQwotableOptionsBottomSheet = MutableLiveData(false)
    private val _currentSelectedQwotable = MutableLiveData<Qwotable?>(null)
    private val _showFilterIcon = MutableLiveData(false)
    private val _showAddFloatingActionButton = MutableLiveData(false)
    private val _showAddEditQwotableDialog = MutableLiveData(false)
    private val _showErrorWarningDialog = MutableLiveData(false)
    private val _isLoadingRandomQuote = MutableLiveData(false)
    private val _randomQuote = MutableLiveData("")
    private val _selectedLanguage = MutableLiveData<StringValue>(StringValue.StringResource(R.string.default_language))
    private val _showInformationDialog = MutableLiveData(false)

    val showFilterBottomSheet: LiveData<Boolean> = _showFilterBottomSheet
    val showQwotableOptionsBottomSheet: LiveData<Boolean> = _showQwotableOptionsBottomSheet
    val currentSelectedQwotable: LiveData<Qwotable?> = _currentSelectedQwotable
    val showFilterIcon: LiveData<Boolean> = _showFilterIcon
    val showAddFloatingActionButton: LiveData<Boolean> = _showAddFloatingActionButton
    val showAddEditQwotableDialog: LiveData<Boolean> = _showAddEditQwotableDialog
    val showErrorWarningDialog: LiveData<Boolean> = _showErrorWarningDialog
    val isLoadingRandomQuote: LiveData<Boolean> = _isLoadingRandomQuote
    val randomQuote: LiveData<String> = _randomQuote
    val selectedLanguage: LiveData<StringValue> = _selectedLanguage
    val showInformationDialog: LiveData<Boolean> = _showInformationDialog

    fun setShowFilterBottomSheet(show: Boolean) {
        _showFilterBottomSheet.value = show
    }

    fun setShowQwotableOptionsBottomSheet(show: Boolean) {
        _showQwotableOptionsBottomSheet.value = show
    }

    fun setCurrentSelectedQwotable(qwotable: Qwotable?) {
        _currentSelectedQwotable.value = qwotable
    }

    fun setCurrentPageRoute(pageRoute: String) {
        _showFilterIcon.value = (pageRoute == Screens.Qwotable.route)
        _showAddFloatingActionButton.value = (pageRoute == Screens.OwnQwotables.route)
    }

    fun setShowAddEditQwotableDialog(show: Boolean) {
        _showAddEditQwotableDialog.value = show
    }

    fun setShowDeletionWarningDialog(show: Boolean) {
        _showErrorWarningDialog.value = show
    }

    fun getRandomQuote(context: Context) {
        _isLoadingRandomQuote.postValue(true)
        val randomNum = (0..1).random()

        RandomQuote(context).getRandomQuote(randomNum) { result ->
            _randomQuote.postValue(result)
            _isLoadingRandomQuote.postValue(false)
        }
    }

    fun setSelectedLanguageOption(@StringRes languageId: Int) {
        _selectedLanguage.value = StringValue.StringResource(languageId)
    }

    fun setShowInformationDialog(show: Boolean) {
        _showInformationDialog.value = show
    }
}