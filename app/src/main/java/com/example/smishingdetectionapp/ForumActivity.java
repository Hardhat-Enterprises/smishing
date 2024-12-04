package com.example.smishingdetectionapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.ui.CommunityPost;

import java.util.ArrayList;
import java.util.List;

public class ForumActivity extends AppCompatActivity {

    private List<CommunityPost> posts = new ArrayList<>();
    private CommunityPostAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forums);

        // Back button setup
        ImageButton report_back = findViewById(R.id.forum_back_button);
        report_back.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
            finish();
        });

        // RecyclerView setup
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommunityPostAdapter(posts);
        recyclerView.setAdapter(adapter);


        // Submit new post functionality
        EditText userThoughtsInput = findViewById(R.id.userThoughtsInput);
        Button submitThoughtsButton = findViewById(R.id.submitThoughtsButton);

        submitThoughtsButton.setOnClickListener(v -> {
            String content = userThoughtsInput.getText().toString().trim();
            if (!content.isEmpty()) {
                posts.add(new CommunityPost("New Post", content, "You"));
                adapter.notifyItemInserted(posts.size() - 1);
                userThoughtsInput.setText("");
                Toast.makeText(this, "Post added successfully!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Adapter for RecyclerView
    private static class CommunityPostAdapter extends RecyclerView.Adapter<CommunityPostAdapter.CommunityPostViewHolder> {

        private final List<CommunityPost> posts;

        public CommunityPostAdapter(List<CommunityPost> posts) {
            this.posts = posts;
        }

        @NonNull
        @Override
        public CommunityPostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new CommunityPostViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull CommunityPostViewHolder holder, int position) {
            CommunityPost post = posts.get(position);
            holder.title.setText(post.getTitle());
            holder.content.setText(post.getContent());
        }

        @Override
        public int getItemCount() {
            return posts.size();
        }

        static class CommunityPostViewHolder extends RecyclerView.ViewHolder {
            TextView title;
            TextView content;

            public CommunityPostViewHolder(@NonNull View itemView) {
                super(itemView);
                title = itemView.findViewById(android.R.id.text1);
                content = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}
