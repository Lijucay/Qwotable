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
import com.lijukay.core.R

sealed class StringValue {
    data class DynamicString(val value: String) : StringValue()

    data object Default : StringValue()

    class StringResource(@StringRes val resId: Int, vararg val args: Any) : StringValue()

    fun asString(context: Context?): String {
        return when (this) {
            is Default -> context?.getString(R.string.default_language) ?: "English"
            is DynamicString -> value
            is StringResource -> {
                return if (context != null) {
                    return when (resId) {
                        R.string.english -> "English"
                        R.string.german -> "German"
                        R.string.french -> "French"
                        else -> context.getString(resId, *args)
                    }
                } else {
                    "English"
                }
            }
        }
    }
}