package com.lijukay.quotesAltDesign.domain.util.states

import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.data.utils.StringValue

enum class ThemeMode(val displayName: StringValue) {
    LIGHT_MODE(StringValue.StringResource(R.string.light_mode)),
    DARK_MODE(StringValue.StringResource(R.string.dark_mode)),
    SYSTEM_MODE(StringValue.StringResource(R.string.system_mode))
}