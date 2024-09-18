package com.lijukay.quotesAltDesign.domain.util.states

import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.data.utils.StringValue

enum class SharePreferences(val displayName: StringValue, val description: StringValue) {
    QUOTE_ONLY(
        StringValue.StringResource(R.string.quote_only),
        StringValue.StringResource(R.string.quote_only_description)
    ),
    QUOTE_AND_AUTHOR_ONLY(
        StringValue.StringResource(R.string.quote_and_author_only),
        StringValue.StringResource(R.string.quote_and_author_only_description)
    ),
    QUOTE_AND_SOURCE_ONLY(
        StringValue.StringResource(R.string.quote_and_source_only),
        StringValue.StringResource(R.string.quote_and_source_only_description)
    ),
    EVERYTHING(
        StringValue.StringResource(R.string.share_everything),
        StringValue.StringResource(R.string.share_everything_description)
    )
}
