package com.example.smishingdetectionapp.detections;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smishingdetectionapp.R;


public class ReportsAdapter extends RecyclerView.Adapter<ReportsAdapter.ViewHolder> {
    private Context context;
    private Cursor cursor;

    public ReportsAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    public void updateCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.detection_items, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (!cursor.moveToPosition(position)) {
            return;
        }

        String phoneNumber = cursor.getString(cursor.getColumnIndexOrThrow("Phone_Number"));
        String message = cursor.getString(cursor.getColumnIndexOrThrow("Message"));
        String date = cursor.getString(cursor.getColumnIndexOrThrow("Date"));

        holder.phoneTextView.setText(phoneNumber);
        holder.messageTextView.setText(message);
        holder.dateTextView.setText(date);

        holder.itemView.setOnLongClickListener(v -> {
            String reportText = String.format("Phone Number: %s\nMessage: %s\nDate: %s",
                    phoneNumber, message, date);

            android.content.ClipboardManager clipboard = (android.content.ClipboardManager)
                    context.getSystemService(Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("Report Details", reportText);
            clipboard.setPrimaryClip(clip);

            Toast.makeText(context, "Report copied to clipboard", Toast.LENGTH_SHORT).show();
            return true;});
    }

    @Override
    public int getItemCount() {
        return cursor != null ? cursor.getCount() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView phoneTextView;
        TextView messageTextView;
        TextView dateTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            phoneTextView = itemView.findViewById(R.id.detectionPhoneText);
            messageTextView = itemView.findViewById(R.id.detectionMessageText);
            dateTextView = itemView.findViewById(R.id.detectionDateText);
        }
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }
}