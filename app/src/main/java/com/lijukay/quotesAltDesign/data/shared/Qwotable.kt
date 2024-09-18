package com.lijukay.quotesAltDesign.data.shared

/**
 * Interface that represents a Qwotable
 * */
interface Qwotable {
    /**
     * A quotable text
     * */
    var quote: String

    /**
     * The author of that quotable text
     * */
    var author: String

    /**
     * The source of the quotable text
     * */
    var source: String

    /**
     * The language of the quotable text
     * */
    var language: String

    /**
     * Indicates whether or not the quote is marked as favorite
     * */
    var isFavorite: Boolean

    /**
     * Indicates whether or not the quote has an author
     * */
    var hasAuthor: Boolean

    /**
     * Indicates whether or not the quote has a source
     * */
    var hasSource: Boolean
}

/**
 * Representation of an empty Qwotable with default fields.
 * */
data class DefaultQwotable(
    override var quote: String = "",
    override var author: String = "",
    override var source: String = "",
    override var language: String = "",
    override var isFavorite: Boolean = false,
    override var hasAuthor: Boolean = true,
    override var hasSource: Boolean = true
): Qwotable