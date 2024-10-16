package com.example.smishingdetectionapp.forum

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.smishingdetectionapp.R
import com.example.smishingdetectionapp.forum.model.ForumTopic

class ForumTopicAdapter(private val listener: ClickListener) :
    RecyclerView.Adapter<ForumTopicAdapter.ViewHolder>() {
    private var forumList: List<ForumTopic> = ArrayList()

    fun updateList(items: List<ForumTopic>) {
        this.forumList = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.forum_topic_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val topic = forumList[position]
        holder.topicTitle.text = topic.title
        holder.topicDescription.text = topic.description

        // Set an onClickListener for each card
        holder.cardView.setOnClickListener {
            // Notify the listener when an item is clicked
            listener.OnTopicClicked(topic)
        }
    }

    override fun getItemCount(): Int {
        return forumList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Finding and assigning each item
        val topicTitle: TextView = itemView.findViewById(R.id.text_topic)
        val topicDescription: TextView = itemView.findViewById(R.id.text_description)
        var cardView: CardView = itemView.findViewById(R.id.card_view)
    }

    interface ClickListener {
        fun OnTopicClicked(topic: ForumTopic)
    }
}
