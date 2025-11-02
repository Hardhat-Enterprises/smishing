package com.example.smishingdetectionapp.chat;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.chat.db.ChatDatabase;
import com.example.smishingdetectionapp.chat.db.ChatMessageEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatHistoryActivity extends AppCompatActivity {

    // Keep messages for export
    private List<ChatMessageEntity> allMessages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        // Back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Menu button (3 dots)
        ImageButton menuButton = findViewById(R.id.menuButton);
        menuButton.setOnClickListener(v -> {
            androidx.appcompat.widget.PopupMenu popup =
                    new androidx.appcompat.widget.PopupMenu(this, v);
            popup.getMenuInflater().inflate(R.menu.menu_chat_history, popup.getMenu());
            popup.setOnMenuItemClickListener(item -> {
                int id = item.getItemId();
                if (id == R.id.action_export_txt) {
                    exportChatHistoryTxt();
                    return true;
                } else if (id == R.id.action_export_pdf) {
                    exportChatHistoryAsPdf();
                    return true;
                } else if (id == R.id.action_clear) {
                    confirmAndDeleteHistory();
                    return true;
                }
                return false;
            });
            popup.show();
        });

        // RecyclerView setup
        RecyclerView historyRecyclerView = findViewById(R.id.historyRecyclerView);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        ChatAdapter chatAdapter = new ChatAdapter(this);
        historyRecyclerView.setAdapter(chatAdapter);

        // Load messages in background
        new Thread(() -> {
            ChatDatabase db = ChatDatabase.getInstance(getApplicationContext());
            allMessages = db.chatMessageDao().getAll();

            runOnUiThread(() -> {
                if (allMessages == null || allMessages.isEmpty()) {
                    Toast.makeText(this, "No chat history available.", Toast.LENGTH_SHORT).show();
                } else {
                    for (ChatMessageEntity e : allMessages) {
                        chatAdapter.addMessage(new ChatMessage(
                                e.text,
                                e.sender.equalsIgnoreCase("user") ? ChatMessage.USER : ChatMessage.BOT,
                                e.timestamp
                        ));
                    }
                    historyRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
                }
            });
        }).start();
    }

    // ---------- Delete History Dialog ----------
    private void confirmAndDeleteHistory() {
        new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setTitle("Delete History")
                .setMessage("Are you sure you want to clear all chat history?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    new Thread(() -> {
                        ChatDatabase db = ChatDatabase.getInstance(getApplicationContext());
                        db.chatMessageDao().deleteAll();

                        runOnUiThread(() -> {
                            if (allMessages != null) {
                                allMessages.clear();
                            }
                            Toast.makeText(this, "Chat history cleared.", Toast.LENGTH_SHORT).show();
                        });
                    }).start();
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // ---------- TXT Export ----------
    private void exportChatHistoryTxt() {
        if (allMessages == null || allMessages.isEmpty()) {
            Toast.makeText(this, "No chat history available.", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        StringBuilder sb = new StringBuilder();
        for (ChatMessageEntity e : allMessages) {
            String time = e.timestamp > 0 ? sdf.format(new Date(e.timestamp)) + " AEST" : "";
            sb.append("[").append(time).append("] ");
            sb.append(e.sender.equalsIgnoreCase("user") ? "User: " : "Bot: ");
            sb.append(e.text).append("\n");
        }

        try {
            File exportDir = new File(getExternalFilesDir(null), "exports");
            if (!exportDir.exists()) exportDir.mkdirs();

            File exportFile = new File(exportDir, "chat_history.txt");
            try (FileOutputStream fos = new FileOutputStream(exportFile)) {
                fos.write(sb.toString().getBytes());
            }

            Uri fileUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    exportFile
            );

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "Share chat history TXT"));
            Toast.makeText(this, "Chat history exported as TXT.", Toast.LENGTH_SHORT).show();

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to export chat history TXT.", Toast.LENGTH_SHORT).show();
        }
    }

    // ---------- PDF Export ----------
    private void exportChatHistoryAsPdf() {
        if (allMessages == null || allMessages.isEmpty()) {
            Toast.makeText(this, "No chat history available.", Toast.LENGTH_SHORT).show();
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        PdfDocument pdfDocument = new PdfDocument();

        // Normal message paint
        Paint paint = new Paint();
        paint.setTextSize(12);

        // Heading paint
        Paint headingPaint = new Paint();
        headingPaint.setTextSize(18);
        headingPaint.setFakeBoldText(true);
        headingPaint.setTextAlign(Paint.Align.CENTER);
        headingPaint.setColor(getResources().getColor(R.color.smish_text_primary, getTheme()));

        // Subheading paint
        Paint subHeadingPaint = new Paint();
        subHeadingPaint.setTextSize(12);
        subHeadingPaint.setTextAlign(Paint.Align.CENTER);
        subHeadingPaint.setColor(getResources().getColor(R.color.smish_text_secondary, getTheme()));

        // User message paint
        Paint userPaint = new Paint(paint);
        userPaint.setColor(getResources().getColor(R.color.smish_blue_dark, getTheme()));

        // Bot message paint
        Paint botPaint = new Paint(paint);
        botPaint.setColor(getResources().getColor(R.color.smish_bot_icon, getTheme()));

        PdfDocument.PageInfo pageInfo =
                new PdfDocument.PageInfo.Builder(595, 842, 1).create();
        PdfDocument.Page page = pdfDocument.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        int x = 40, y = 90; // header space

        // Heading
        canvas.drawText("Smishing Smart Assistant – Chat History",
                pageInfo.getPageWidth() / 2f, 40, headingPaint);

        String dateText = "Exported on: " + sdf.format(new Date()) + " AEST";
        canvas.drawText(dateText, pageInfo.getPageWidth() / 2f, 60, subHeadingPaint);

        // Chat Messages
        for (ChatMessageEntity e : allMessages) {
            String time = e.timestamp > 0 ? sdf.format(new Date(e.timestamp)) + " AEST" : "";
            String prefix = e.sender.equalsIgnoreCase("user") ? "User: " : "Bot: ";
            String line = "[" + time + "] " + prefix + e.text;

            Paint currentPaint = e.sender.equalsIgnoreCase("user") ? userPaint : botPaint;

            // Naive wrap every ~60 chars
            for (String textLine : line.split("(?<=\\G.{60})")) {
                canvas.drawText(textLine, x, y, currentPaint);
                y += 18;
            }

            y += 10; // spacing between messages

            // New page if overflowing
            if (y > 800) {
                pdfDocument.finishPage(page);
                pageInfo = new PdfDocument.PageInfo.Builder(595, 842, pdfDocument.getPages().size() + 1).create();
                page = pdfDocument.startPage(pageInfo);
                canvas = page.getCanvas();
                y = 90;

                // redraw header
                canvas.drawText("Smishing Smart Assistant – Chat History",
                        pageInfo.getPageWidth() / 2f, 40, headingPaint);
                canvas.drawText(dateText,
                        pageInfo.getPageWidth() / 2f, 60, subHeadingPaint);
            }
        }

        pdfDocument.finishPage(page);

        File exportDir = new File(getExternalFilesDir(null), "exports");
        if (!exportDir.exists()) exportDir.mkdirs();

        File pdfFile = new File(exportDir, "chat_history.pdf");
        try (FileOutputStream fos = new FileOutputStream(pdfFile)) {
            pdfDocument.writeTo(fos);
            pdfDocument.close();

            Uri fileUri = FileProvider.getUriForFile(
                    this,
                    getPackageName() + ".fileprovider",
                    pdfFile
            );

            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("application/pdf");
            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(shareIntent, "Share chat history PDF"));
            Toast.makeText(this, "Chat history exported as PDF.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to export chat history PDF.", Toast.LENGTH_SHORT).show();
        }
    }
}
