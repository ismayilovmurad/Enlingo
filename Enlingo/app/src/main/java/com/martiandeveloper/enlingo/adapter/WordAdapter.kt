package com.martiandeveloper.enlingo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.martiandeveloper.enlingo.R
import com.martiandeveloper.enlingo.databinding.RecyclerviewWordItemBinding
import com.martiandeveloper.enlingo.holder.WordViewHolder
import com.martiandeveloper.enlingo.model.Word
import java.util.*
import kotlin.collections.ArrayList

class WordAdapter(
    private val languageList: ArrayList<Word>,
    private val context: Context,
    private val itemCLickListener: ItemClickListener,
    private val wordMeaning: String?,
) : RecyclerView.Adapter<WordViewHolder>(), Filterable {

    var wordFilterList = ArrayList<Word>()

    init {
        wordFilterList = languageList
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): WordViewHolder {
        val binding: RecyclerviewWordItemBinding = DataBindingUtil
            .inflate(
                LayoutInflater.from(parent.context),
                R.layout.recyclerview_word_item,
                parent,
                false
            )
        return WordViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word: Word = wordFilterList[position]
        holder.bind(word, itemCLickListener, context, wordMeaning)
    }

    override fun getItemCount(): Int = wordFilterList.size

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val charSearch = constraint.toString()
                wordFilterList = if (charSearch.isEmpty()) {
                    languageList
                } else {
                    val resultList = ArrayList<Word>()
                    for (i in languageList) {
                        if (i.word.toLowerCase(Locale.ROOT)
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
                filterResults.values = wordFilterList
                return filterResults
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                wordFilterList = results?.values as ArrayList<Word>
                notifyDataSetChanged()
            }

        }
    }

    interface ItemClickListener {
        fun onItemClick(word: Word)
    }
}
