package com.lijukay.qwotable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lijukay.core.database.Quote
import com.lijukay.core.database.Wisdom
import kotlin.IllegalArgumentException

class QwotableAdapter(
    private val items: List<*>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : RecyclerView.ViewHolder {
        return if (items.any { it is Quote }) {
            QuoteViewHolder.create(parent)
        } else if (items.any { it is Wisdom }) {
            WisdomViewHolder.create(parent)
        } else {
            throw IllegalArgumentException("${items.javaClass.simpleName} is not a valid element for this adapter")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is QuoteViewHolder -> holder.bind(items[position] as Quote)
            is WisdomViewHolder -> holder.bind(items[position] as Wisdom)
            else -> throw IllegalArgumentException("${holder.javaClass.simpleName} is not a valid element for this adapter")
        }
    }

    override fun getItemCount() = items.size

    class QuoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val quoteTextView: TextView = itemView.findViewById(R.id.quote_text_view)
        private val authorTextView: TextView = itemView.findViewById(R.id.author_text_view)
        private val sourceTextView: TextView = itemView.findViewById(R.id.source_text_view)

        fun bind(item: Quote) {
            quoteTextView.text = item.quote
            authorTextView.text = item.author
            sourceTextView.text = item.source
        }

        companion object {
            fun create(parent: ViewGroup): QuoteViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.quotes_item, parent, false)
                return QuoteViewHolder(view)
            }
        }
    }

    class WisdomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleTextView: TextView = itemView.findViewById(R.id.title_text_view)
        private val wisdomTextView: TextView = itemView.findViewById(R.id.wisdom_text_view)
        private val authorTextView: TextView = itemView.findViewById(R.id.author_text_view)
        private val sourceTextView: TextView = itemView.findViewById(R.id.source_text_view)

        fun bind(item: Wisdom) {
            titleTextView.text = item.title
            wisdomTextView.text = item.wisdom
            authorTextView.text = item.author
            sourceTextView.text = item.source
        }

        companion object {
            fun create(parent: ViewGroup): WisdomViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.wisdom_item, parent, false)
                return WisdomViewHolder(view)
            }
        }
    }
}