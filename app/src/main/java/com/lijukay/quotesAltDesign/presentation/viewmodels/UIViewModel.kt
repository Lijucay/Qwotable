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
import com.lijukay.quotesAltDesign.data.utils.StringValue
import com.lijukay.quotesAltDesign.domain.util.states.Languages
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UIViewModel : ViewModel() {
    private val _showFilterDialog = MutableStateFlow(false)
    val showFilterDialog: StateFlow<Boolean> = _showFilterDialog

    private val _showQwotableOptionsDialog = MutableStateFlow(false)
    val showQwotableOptionsDialog: StateFlow<Boolean> = _showQwotableOptionsDialog

    private val _currentSelectedLocalQwotable = MutableStateFlow<LocalQwotable?>(null)
    val currentSelectedLocalQwotable: StateFlow<LocalQwotable?> = _currentSelectedLocalQwotable

    private val _showFilterToolIcon = MutableStateFlow(false)
    val showFilterToolIcon: StateFlow<Boolean> = _showFilterToolIcon

    private val _showExportIcon = MutableStateFlow(false)
    val showExportIcon: StateFlow<Boolean> = _showExportIcon

    private val _showEditorOptions = MutableStateFlow(false)
    val showEditorOptions: StateFlow<Boolean> = _showEditorOptions

    private val _showAddDialog = MutableStateFlow(false)
    val showAddDialog: StateFlow<Boolean> = _showAddDialog

    private val _showEditDialog = MutableStateFlow(false)
    val showEditDialog: StateFlow<Boolean> = _showEditDialog

    private val _showErrorDialog = MutableStateFlow(false)
    val showErrorDialog: StateFlow<Boolean> = _showErrorDialog

    private val _showThemePicker = MutableStateFlow(false)
    val showThemePicker: StateFlow<Boolean> = _showThemePicker

    private val _errorDialogMessage = MutableStateFlow<String?>(null)
    val errorDialogMessage: StateFlow<String?> = _errorDialogMessage

    private val _showFavoriteQuotes = MutableStateFlow(false)
    val showFavoriteQuotes: StateFlow<Boolean> = _showFavoriteQuotes

    private val _isRandomQuoteLoading = MutableStateFlow(false)
    val isRandomQuoteLoading: StateFlow<Boolean> = _isRandomQuoteLoading

    private val _selectedLanguage = MutableStateFlow(Languages.DEFAULT)
    val selectedLanguage: StateFlow<Languages> = _selectedLanguage

    private val _showInfoDialog = MutableStateFlow(false)
    val showInfoDialog: StateFlow<Boolean> = _showInfoDialog

    private val _infoDialogTitle = MutableStateFlow<String?>(null)
    val infoDialogTitle: StateFlow<String?> = _infoDialogTitle

    private val _infoDialogMessage = MutableStateFlow<String?>(null)
    val infoDialogMessage: StateFlow<String?> = _infoDialogMessage

    private val _infoDialogConfirmAction = MutableStateFlow<(() -> Unit)?>(null)
    val infoDialogConfirmAction: StateFlow<(() -> Unit)?> = _infoDialogConfirmAction

    private val _infoDialogDismissAction = MutableStateFlow {
        resetInfoDialog()
    }
    val infoDialogDismissAction: StateFlow<() -> Unit> = _infoDialogDismissAction

    private val _showSharePreferenceDialog = MutableStateFlow(false)
    val showSharePreferenceDialog: StateFlow<Boolean> = _showSharePreferenceDialog

    private val _currentTitle = MutableStateFlow<StringValue>(
        StringValue.StringResource(R.string.app_name)
    )
    val currentTitle: StateFlow<StringValue> = _currentTitle

    private val _selectedScreenIndex = MutableStateFlow(-1)
    val selectedScreenIndex: StateFlow<Int> = _selectedScreenIndex

    fun setShowSharePreferenceDialog(show: Boolean) {
        _showSharePreferenceDialog.value = show
    }

    fun setShowFavorites(show: Boolean) {
        _showFavoriteQuotes.value = show
    }

    fun setShowInfoDialog(show: Boolean) {
        _showInfoDialog.value = show
    }

    fun setInfoDialogTitle(title: String) {
        _infoDialogTitle.value = title
    }

    fun setInfoDialogMessage(message: String) {
        _infoDialogMessage.value = message
    }

    fun setInfoDialogAction(action: (() -> Unit)?) {
        _infoDialogConfirmAction.value = action
    }

    fun resetInfoDialog() {
        _infoDialogTitle.value = null
        _infoDialogMessage.value = null
        _showInfoDialog.value = false
    }

    fun resetInfoDialogAction() {
        _infoDialogConfirmAction.value = null
    }

    fun setCurrentSelectedQwotable(localQwotable: LocalQwotable?) {
        _currentSelectedLocalQwotable.value = localQwotable
    }

    fun setShowQwotableOptionsDialog(show: Boolean) {
        _showQwotableOptionsDialog.value = show
    }

    fun setShowAddQwotableDialog(show: Boolean) {
        _showAddDialog.value = show
    }

    fun setShowEditQwotableDialog(show: Boolean) {
        _showEditDialog.value = show
    }

    fun setSelectedScreenIndex(index: Int) {
        _selectedScreenIndex.value = index
    }

    fun setShowEditorOptions(show: Boolean) {
        _showEditorOptions.value = show
    }

    fun setShowFilterToolIcon(show: Boolean) {
        _showFilterToolIcon.value = show
    }

    fun setShowExportIcon(show: Boolean) {
        _showExportIcon.value = show
    }

    fun setShowFilterDialog(show: Boolean) {
        _showFilterDialog.value = show
    }

    fun setShowErrorDialog(show: Boolean) {
        _showErrorDialog.value = show
    }

    fun setShowThemePickerDialog(show: Boolean) {
        _showThemePicker.value = show
    }

    fun setErrorDialogMessage(message: String?) {
        _errorDialogMessage.value = message
    }

    fun setLoading(loading: Boolean) {
        _isRandomQuoteLoading.value = loading
    }

    fun setSelectedLanguageOption(language: Languages) {
        _selectedLanguage.value = language
    }

    fun setInfoDialogDismissAction(action: () -> Unit) {
        _infoDialogDismissAction.value = action
    }

    fun resetInfoDialogDismissAction() {
        _infoDialogDismissAction.value = {
            resetInfoDialog()
        }
    }
}