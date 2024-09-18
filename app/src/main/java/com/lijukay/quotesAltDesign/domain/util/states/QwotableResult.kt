package com.lijukay.quotesAltDesign.domain.util.states

import com.lijukay.quotesAltDesign.data.utils.StringValue

sealed class QwotableResult<T>(open val data: T? = null, open val message: StringValue? = null) {
    class Success<T>(override val data: T) : QwotableResult<T>(data)
    class Failure<T>(
        override val message: StringValue,
        data: T? = null
    ) : QwotableResult<T>(data, message)
}