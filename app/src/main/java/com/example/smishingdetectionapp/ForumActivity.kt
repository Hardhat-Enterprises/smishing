package com.example.smishingdetectionapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.smishingdetectionapp.forum.CreateForumTopicActivity
import com.example.smishingdetectionapp.forum.ForumTopicAdapter
import com.example.smishingdetectionapp.forum.model.ForumTopic
import com.example.smishingdetectionapp.sms.SMSAdapter


class ForumActivity : AppCompatActivity(), ForumTopicAdapter.ClickListener {

    lateinit var recyclerView: RecyclerView
    lateinit var adapter: ForumTopicAdapter
    lateinit var noForumTopicsText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forums)

        // Initialize the back button to go back to the previous screen
        val report_back = findViewById<ImageButton>(R.id.forum_back_button)
        report_back.setOnClickListener {
            finish()
        }

        noForumTopicsText = findViewById(R.id.no_forums_text)
        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.setHasFixedSize(true) // Set to improve performance since changes in content do not change layout size
        recyclerView.setLayoutManager(LinearLayoutManager(this)) // Set layout manager for RecyclerView
        adapter = ForumTopicAdapter(this) // Initialize the adapter with context
        recyclerView.setAdapter(adapter)

        // Start a new forum button
        val startNewPostButton = findViewById<Button>(R.id.startNewForumButton)
        startNewPostButton.setOnClickListener {
            startActivity(Intent(this, CreateForumTopicActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        //start database connection
        val databaseAccess = DatabaseAccess.getInstance(applicationContext)
        databaseAccess.open()
        val forumTopics: List<ForumTopic> = databaseAccess.getForumTopics()
        //closing the connection
        databaseAccess.close()

        adapter.updateList(forumTopics)
        if (forumTopics.isEmpty()) {
            noForumTopicsText.visibility = View.VISIBLE
        } else {
            noForumTopicsText.visibility = View.GONE
        }
    }

    override fun OnTopicClicked(topic: ForumTopic) {
        // Need to complete this to open the forum topic
    }
}

