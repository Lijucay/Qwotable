package com.lijukay.quotesAltDesign.data.remote.model.responses

import com.lijukay.quotesAltDesign.data.remote.model.GameOfThronesQuote

data class GameOfThronesAPIResponse(
    val sentence: String,
    val character: Character?
) {
    companion object {
        fun GameOfThronesAPIResponse.toQwotable(): GameOfThronesQuote {
            return GameOfThronesQuote(
                quote = sentence,
                author = character?.name ?: ""
            )
        }
    }
}

data class Character(
    val name: String?,
    val slug: String?,
    val house: House?
)

data class House(
    val name: String?,
    val slug: String?
)