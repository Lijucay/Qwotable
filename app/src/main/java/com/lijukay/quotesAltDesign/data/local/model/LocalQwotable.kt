package com.lijukay.quotesAltDesign.data.local.model

import com.lijukay.quotesAltDesign.data.shared.Qwotable

/**
 * Interface that represents a local Qwotable. Local Qwotables are stored within the app, either
 * in a room database or in a json file.
 *
 * It inherits from [Qwotable]
 * */
interface LocalQwotable : Qwotable {
    override var quote: String
    override var author: String
    override var source: String

    override var hasAuthor: Boolean
    override var hasSource: Boolean
}