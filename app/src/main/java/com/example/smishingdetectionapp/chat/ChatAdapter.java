package com.example.smishingdetectionapp.chat;

import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smishingdetectionapp.R;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {
    private List<ChatMessage> messages = new ArrayList<>();

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        holder.messageText.setText(message.getMessage());

        // Create layout parameters for the FrameLayout
        FrameLayout.LayoutParams frameParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        holder.itemView.setLayoutParams(frameParams);

        // Create layout parameters for the TextView
        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if (message.getMessageType() == ChatMessage.USER_MESSAGE) {
            holder.messageText.setBackgroundResource(R.drawable.user_message_background);
            holder.messageText.setTextColor(Color.WHITE);
            textParams.gravity = Gravity.END;
            textParams.setMargins(50, 8, 16, 8);
        } else {
            holder.messageText.setBackgroundResource(R.drawable.bot_message_background);
            holder.messageText.setTextColor(Color.BLACK);
            textParams.gravity = Gravity.START;
            textParams.setMargins(16, 8, 50, 8);
        }

        holder.messageText.setLayoutParams(textParams);
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
        }
    }
}