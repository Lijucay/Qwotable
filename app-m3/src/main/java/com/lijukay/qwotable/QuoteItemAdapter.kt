package com.lijukay.qwotable

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lijukay.core.database.Quote

class QuoteItemAdapter(
    private val quotes: List<Quote>
) : RecyclerView.Adapter<QuoteItemAdapter.QuoteViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QuoteViewHolder {
        return QuoteViewHolder.create(parent)
    }

    override fun getItemCount() = quotes.size

    override fun onBindViewHolder(holder: QuoteViewHolder, position: Int) {
        val current = quotes[position]
        holder.bind(current)
    }

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
                    .inflate(R.layout.item_card_quote, parent, false)
                return QuoteViewHolder(view)
            }
        }

    }
}