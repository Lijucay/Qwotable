package com.lijukay.quotesAltDesign.domain.util.states

import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.data.utils.StringValue


enum class Languages(val displayName: StringValue, val filterName: String) {
    ENGLISH(StringValue.StringResource(R.string.english), "English"),
    GERMAN(StringValue.StringResource(R.string.german), "German"),
    FRENCH(StringValue.StringResource(R.string.french), "French"),
    DEFAULT(StringValue.StringResource(R.string.default_language), "");
}