package com.martiandeveloper.easyenglish.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.martiandeveloper.easyenglish.holder.PhraseViewHolder
import com.martiandeveloper.easyenglish.model.Phrase
import java.util.*
import kotlin.collections.ArrayList

class PhraseAdapter(
    private val languageList: ArrayList<Phrase>,
    private val context: Context,
    private val itemCLickListener: ItemClickListener,
    private val phraseMeaning: String?,
) : RecyclerView.Adapter<PhraseViewHolder>(), Filterable {

    var phraseFilterList = ArrayList<Phrase>()

    init {
        phraseFilterList = languageList
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhraseViewHolder {
        val inflater = LayoutInflater.from(context)
        return PhraseViewHolder(
            inflater,
            parent
        )
    }

    override fun onBindViewHolder(holder: PhraseViewHolder, position: Int) {
        val phrase: Phrase = phraseFilterList[position]
        holder.bind(phrase, itemCLickListener, context, phraseMeaning)
    }

    override fun getItemCount(): Int = phraseFilterList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                phraseFilterList = if (charSearch.isEmpty()) {
                    languageList
                } else {
                    val resultList = ArrayList<Phrase>()
                    for (i in languageList) {
                        if (i.phrase.toLowerCase(Locale.ROOT)
                                .contains(charSearch.toLowerCase(Locale.ROOT)) || i.meaning
                                .toLowerCase(
                                    Locale.ROOT
                                ).contains(charSearch.toLowerCase(Locale.ROOT))
                        ) {
                            resultList.add(i)
                        }
                    }
                    resultList
                }
                val filterResults = FilterResults()
                filterResults.values = phraseFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                phraseFilterList = results?.values as ArrayList<Phrase>
                notifyDataSetChanged()
            }

        }
    }

    interface ItemClickListener {
        fun onItemClick(phrase: Phrase)
    }
}
