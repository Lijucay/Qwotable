package com.lijukay.quotesAltDesign.data.remote.model

import com.lijukay.quotesAltDesign.data.shared.Qwotable

interface RemoteQwotable : Qwotable {
    override var quote: String
    override var author: String
    override var source: String

    override var hasAuthor: Boolean
    override var hasSource: Boolean

    override var isFavorite: Boolean
        get() = false
        set(value) {}

    var apiName: String
    var apiSource: String
}