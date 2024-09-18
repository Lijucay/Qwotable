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

package com.lijukay.quotesAltDesign.data.local.model

/**
 * A representation of a programming quote, found in programming_quotes.json
 * */
data class ProgrammingQuote(
    override var quote: String,
    override var author: String
): LocalQwotable {
    override var language: String = ""
    override var isFavorite: Boolean = false
    override var hasAuthor: Boolean = true
    override var hasSource: Boolean = true
    override var source: String = "ProgrammingQuotes"
}
