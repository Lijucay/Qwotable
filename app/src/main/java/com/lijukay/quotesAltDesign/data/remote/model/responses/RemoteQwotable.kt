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

package com.lijukay.quotesAltDesign.data.remote.model.responses

/**
 * The object used returned by Retrofit when getting remote Qwotables
 * @param qwotable The quote
 * @param language The language, the quote is in
 * @param addedIn The version, this Qwotable was added in
 * @param oldQwotable The old Qwotable, can be null if there was no update
 * @param lastChanged The last version, this Qwotable was changed
 * @param author The author of the quote, can be null if there is no author or the author is unknown
 * @param source The source of the quote, can be null if there is no source or the source is unknown
 * */

data class RemoteQwotable(
    val qwotable: String,
    val language: String,
    val addedIn: Int,
    val oldQwotable: String? = null,
    val lastChanged: Int? = null,
    val author: String? = null,
    val source: String? = null
)