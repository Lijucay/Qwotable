package com.lijukay.quotesAltDesign.domain.util

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

object DefUtils {
    @OptIn(ExperimentalContracts::class)
    fun String?.isNotNullOrBlank(): Boolean {
        contract { returns(true) implies (this@isNotNullOrBlank != null) }

        return if (this == null) false
        else if (this.all { it.isWhitespace() }) false
        else true
    }
}