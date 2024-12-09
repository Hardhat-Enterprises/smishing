package com.example.smishingdetectionapp.chat;

import android.content.Context;
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
import io.noties.markwon.Markwon;
import io.noties.markwon.html.HtmlPlugin;
import io.noties.markwon.image.ImagesPlugin;
import java.util.ArrayList;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.MessageViewHolder> {

    private List<ChatMessage> messages = new ArrayList<>();
    private final Markwon markwon;

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
        
        FrameLayout.LayoutParams textParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        if (message.getMessageType() == ChatMessage.USER_MESSAGE) {
            holder.messageText.setBackgroundResource(R.drawable.user_message_background);
            holder.messageText.setTextColor(Color.WHITE);
            textParams.gravity = Gravity.END;
            textParams.setMargins(50, 8, 16, 8);
            holder.messageText.setText(message.getMessage());
        } else {
            holder.messageText.setBackgroundResource(R.drawable.bot_message_background);
            holder.messageText.setTextColor(Color.BLACK);
            textParams.gravity = Gravity.START;
            textParams.setMargins(16, 8, 50, 8);
            markwon.setMarkdown(holder.messageText, message.getMessage());
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

    public ChatAdapter(Context context) {
        messages = new ArrayList<>();
        markwon = Markwon.builder(context)
                .usePlugin(HtmlPlugin.create())
                .usePlugin(ImagesPlugin.create())
                .build();
    }
}