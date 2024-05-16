package com.example.smishingdetectionapp.sms;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.sms.model.SMSMessage;

import java.util.List;

public class SMSAdapter extends RecyclerView.Adapter<SMSAdapter.MessageViewHolder>{
    private List<SMSMessage> messageList;
    private final SMSClickListener listener;

    public SMSAdapter(SMSClickListener listener) {
        this.listener = listener;
    }

    public void updateMessagesList(List<SMSMessage> messageList) {
        this.messageList = messageList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sms_message_list_item, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        SMSMessage message = messageList.get(position);
        holder.text_sender.setText(message.getSender());
        holder.text_body.setText(message.getBody());

        // Set an onClickListener for each message card
        if (holder.cardView != null) {
            holder.cardView.setOnClickListener(v -> {
                // Notify the listener when a news item is clicked
                if (listener != null) {
                    listener.OnMessageClicked(message);
                } else {
                    Log.e("MessageViewHolder", "The Listener is null");
                }
            });
        } else {
            Log.e("MessageViewHolder", "The holder CardView is null");
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder{
        TextView text_sender, text_body; // TextViews to display the sender and body of messages
        CardView cardView; // Card to house each message item for a better UX
        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            // Finding and assigning each item
            text_sender = itemView.findViewById(R.id.text_sender);
            text_body = itemView.findViewById(R.id.text_body);
            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
