package com.example.smishingdetectionapp.ui.faq

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.smishingdetectionapp.R
import com.example.smishingdetectionapp.models.FaqItem

class FaqAdapter : RecyclerView.Adapter<FaqAdapter.VH>() {
    private val items = mutableListOf<FaqItem>()

    fun submit(list: List<FaqItem>) {
        items.clear(); items.addAll(list); notifyDataSetChanged()
    }

    inner class VH(v: View) : RecyclerView.ViewHolder(v) {
        private val q: TextView = v.findViewById(R.id.questionTextView)
        private val a: TextView = v.findViewById(R.id.answerTextView)
        private var expanded = false
        fun bind(item: FaqItem) {
            q.text = item.question
            a.text = item.answer
            a.visibility = if (expanded) View.VISIBLE else View.GONE
            itemView.setOnClickListener {
                expanded = !expanded
                a.visibility = if (expanded) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onCreateViewHolder(p: ViewGroup, t: Int) =
        VH(LayoutInflater.from(p.context).inflate(R.layout.item_faq, p, false))
    override fun onBindViewHolder(h: VH, i: Int) = h.bind(items[i])
    override fun getItemCount() = items.size
}
