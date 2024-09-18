package com.lijukay.quotesAltDesign.data.utils

import android.content.Context
import androidx.annotation.StringRes
import com.lijukay.quotesAltDesign.R

sealed class StringValue {
    data class DynamicString(val value: String) : StringValue()

    data object Default : StringValue()

    class StringResource(@StringRes val resId: Int, vararg val args: Any) : StringValue()

    fun asString(context: Context?): String {
        return when (this) {
            is Default -> context?.getString(R.string.default_language) ?: "English"
            is DynamicString -> value
            is StringResource -> return context?.getString(resId, args) ?: ""
        }
    }
}