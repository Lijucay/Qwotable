package com.lijukay.quotesAltDesign.domain.util

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.content.Intent.EXTRA_SUBJECT
import android.content.Intent.EXTRA_TEXT
import android.view.View
import com.lijukay.quotesAltDesign.R
import com.lijukay.quotesAltDesign.data.shared.Qwotable
import com.lijukay.quotesAltDesign.domain.util.states.SharePreferences

object ShareUtil {
    fun String.share(context: Context) {
        with(context) {
            val intent = Intent(ACTION_SEND).apply {
                setType("text/plain")
                putExtra(EXTRA_SUBJECT, getString(R.string.app_name))
                putExtra(EXTRA_TEXT, this@share)
            }

            startActivity(Intent.createChooser(intent, getString(R.string.share_using)))
        }
    }

    fun Qwotable.share(context: Context, sharePreferences: Int) {
        when (sharePreferences) {
            SharePreferences.QUOTE_ONLY.ordinal -> this.quote.share(context)
            SharePreferences.QUOTE_AND_AUTHOR_ONLY.ordinal -> {
                if (!this.hasAuthor) {
                    this.quote.share(context)
                } else {
                    share(
                        context = context,
                        quote = this.quote,
                        author = this.author
                    )
                }
            }
            SharePreferences.QUOTE_AND_SOURCE_ONLY.ordinal -> {
                if (!this.hasSource) quote.share(context)
                else share(context, quote, source = source)
            }
            SharePreferences.EVERYTHING.ordinal -> {
                if (!this.hasAuthor) {
                    share(
                        context = context,
                        quote = quote,
                        source = source
                    )
                } else { share(context, quote, author, source) }
            }
        }
    }

    fun share(context: Context, quote: String, author: String? = null, source: String? = null) {
        with(context) {
            val intent = Intent(ACTION_SEND).apply {
                setType("text/plain")
                putExtra(EXTRA_SUBJECT, getString(R.string.app_name))
                putExtra(EXTRA_TEXT, getShareText(context, quote, author, source))
            }

            startActivity(Intent.createChooser(intent, getString(R.string.share_using)))
        }
    }

    private fun getShareText(context: Context, quote: String, author: String?, source: String?): String {
        return if (quote.isNotEmpty()) {
            buildString {
                append(quote)
                if (author != null || source != null) {
                    appendLine().appendLine()
                }
                author?.let {
                    if (source != null) {
                        appendLine(context.getString(R.string.by, it))
                    } else {
                        append(context.getString(R.string.by, it))
                    }
                }
                source?.let { append(context.getString(R.string.in_source, it)) }
            }
        } else {
            quote
        }
    }

    fun View.share(context: Context) {
        // todo: Create function to share the card as a view.
    }
}