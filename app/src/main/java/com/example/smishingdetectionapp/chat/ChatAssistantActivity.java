package com.example.smishingdetectionapp.chat;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.detections.DatabaseAccess;
import com.example.smishingdetectionapp.chat.db.ChatDatabase;
import com.example.smishingdetectionapp.chat.db.ChatMessageEntity;

import java.util.HashMap;
import java.util.Map;

/**
 * ChatAssistantActivity
 * Init order:
 * 1) findViewById
 * 2) setup RecyclerView
 * 3) seed welcome
 * 4) init OllamaClient
 * 5) wire listeners
 */
public class ChatAssistantActivity extends AppCompatActivity {

    private EditText userInput;
    private ImageButton sendButton;
    private RecyclerView chatRecyclerView;
    private ProgressBar progressBar;
    private ChatAdapter chatAdapter;
    private OllamaClient ollamaClient;

    private int supportPromptCount = 0;
    private static final int MAX_SUPPORT_PROMPTS = 4;

    // Quick-support / FAQ layer — matched before calling LLM
    private final Map<String, String> supportPrompts = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_assistant);

        // 1) findViewById
        userInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        ImageButton backButton = findViewById(R.id.btnBack);
        ImageButton menuButton = findViewById(R.id.menuButton); // 3-dot menu in header

        // 2) setup RecyclerView
        chatAdapter = new ChatAdapter(this);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);

        // 3) seed welcome
        seedWelcomeIfEmpty();

        // 4) init OllamaClient
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        ollamaClient = new OllamaClient(databaseAccess);

        // 5) wire listeners
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                hideKeyboard();
                finish();
            });
        }

        if (menuButton != null) {
            menuButton.setOnClickListener(v -> {
                PopupMenu popup = new PopupMenu(ChatAssistantActivity.this, v);
                popup.getMenuInflater().inflate(R.menu.chat_assistant_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.action_view_history) {
                        startActivity(new Intent(ChatAssistantActivity.this, ChatHistoryActivity.class));
                        return true;
                    } else if (itemId == R.id.action_feedback) {
                        openSupportFeedback();
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }

        sendButton.setOnClickListener(v -> sendMessage());

        // ---- FAQ / quick-support prompts (centralized) ----
        // Existing quick replies
        supportPrompts.put("i need help", "Sure, I’m here to help. What exactly do you need help with?");
        supportPrompts.put("smishing", "Smishing is SMS-based phishing. You can report such messages via the Community Report section.");
        supportPrompts.put("is this a scam", "Let me check that for you in the system...");
        supportPrompts.put("report", "You can report suspicious SMS under the 'Community Report' tab.");

        // NEW: reviewer-requested fallback FAQ intents
        supportPrompts.put("what is this app",
                "This is a Smishing Detection app. It helps you identify SMS phishing, view detections, and report suspicious texts.");
        supportPrompts.put("how do i report",
                "Open Settings → Community and Reporting → Report. Fill in the suspicious SMS details and submit.");
        supportPrompts.put("how to use the app",
                "Grant SMS permission, review detections on the home screen, and report suspicious messages via the Report section.");
        supportPrompts.put("what is smishing",
                "Smishing is phishing by SMS—attackers try to trick you into clicking malicious links or sharing personal info.");
        supportPrompts.put("is my data safe",
                "Yes. Your data stays on your device. We only analyze SMS locally and do not share messages with third parties.");
        supportPrompts.put("can i use without internet",
                "Yes. Basic detection works offline, but reporting suspicious SMS requires an internet connection.");
        supportPrompts.put("how do i update the app",
                "Go to the Google Play Store, search for Smishing Detection App, and tap 'Update' if a new version is available.");
        supportPrompts.put("who developed this app",
                "This app was developed by students at Deakin University as part of a cybersecurity capstone project.");
        supportPrompts.put("help",
                "You can type your question here, check the FAQ, or report issues through the Community Report section.");
        supportPrompts.put("can i delete my history",
                "Yes. Go to 'View Chat History' and tap 'Clear chat history' to remove all saved messages.");
        supportPrompts.put("does this app block sms",
                "No, the app does not block SMS. It only detects and flags suspicious messages for your review.");
        supportPrompts.put("what if detection is wrong",
                "You can mark it manually in the app and report the message to improve future accuracy.");
        supportPrompts.put("can i use this on ios",
                "Currently the app is only available for Android. iOS support is planned for the future.");
        supportPrompts.put("do i need to allow sms permission",
                "Yes. SMS permission is required so the app can scan and detect suspicious messages on your device.");
        supportPrompts.put("how to contact support",
                "You can use this chat assistant for quick help or raise an issue through the 'Community Report' section.");

        // --- Greetings ---
        supportPrompts.put("hi", "Hi! I’m your Smishing Assistant. How can I help you today?");
        supportPrompts.put("hello", "Hello! I’m here to assist you with smishing detection and reporting.");
        supportPrompts.put("hey", "Hey there! Need help checking a suspicious SMS?");
        supportPrompts.put("good morning", "Good morning! How can I support you today?");
        supportPrompts.put("good afternoon", "Good afternoon! Do you want to learn more about smishing or check a message?");
        supportPrompts.put("good evening", "Good evening! I can help you understand smishing or report an SMS.");

        // --- Politeness & Thanks ---
        supportPrompts.put("thanks", "You’re welcome! Stay safe from smishing.");
        supportPrompts.put("thank you", "Happy to help! Let me know if you have more questions.");
        supportPrompts.put("ok", "Alright! Let me know if you’d like to learn more about smishing.");
        supportPrompts.put("cool", "Glad you think so! You can ask me about smishing anytime.");

        // --- Clarifications / Confusions ---
        supportPrompts.put("who are you", "I’m the Smishing Assistant. I help explain smishing, check suspicious texts, and guide you to report them.");
        supportPrompts.put("what can you do", "I can explain smishing, guide you on using the app, and help you report suspicious SMS.");
        supportPrompts.put("help me", "Sure! You can ask me things like ‘What is smishing?’ or ‘How do I report a message?’");

        // --- Fallback / Fun Responses ---
        supportPrompts.put("bye", "Goodbye! Stay safe and remember to check suspicious messages.");
        supportPrompts.put("see you", "See you later! Don’t forget to stay cautious with SMS links.");
        supportPrompts.put("are you real", "I’m a virtual assistant built to help you with smishing—not human, but always available!");
        supportPrompts.put("how are you", "I’m always doing great, ready to help you with smishing. How are you?");

        // --- Security Tips ---
        supportPrompts.put("how to stay safe", "Avoid clicking unknown links, don’t share personal info via SMS, and verify messages with the official source.");
        supportPrompts.put("tips", "1) Never trust urgent SMS links. 2) Verify with the company directly. 3) Use this app to check suspicious texts.");

        // --- App Troubleshooting ---
        supportPrompts.put("app not working", "Try restarting the app. If the issue persists, reinstall from Google Play or contact support.");
        supportPrompts.put("cant report", "Make sure you’re connected to the internet. Go to Community Report and submit the suspicious SMS again.");

        // --- Account / Data ---
        supportPrompts.put("can i recover deleted history", "No, once chat history is deleted, it cannot be recovered.");
        supportPrompts.put("what data is stored", "Only suspicious SMS metadata and your reports are stored locally. Nothing is shared without consent.");

        // --- Learning / Awareness ---
        supportPrompts.put("examples of smishing", "Messages asking you to click a link for parcel delivery, banking updates, or urgent prizes are common smishing attempts.");
        supportPrompts.put("why is smishing dangerous", "Because attackers trick you into giving away personal info, banking details, or installing malware.");

        // --- Encouragement ---
        supportPrompts.put("im worried", "That’s understandable. Smishing is common, but with awareness and this app, you’re safe.");
        supportPrompts.put("scam message received", "Please don’t click anything. Copy or forward the message here so I can help check it.");
    }

    /** Adds a friendly greeting if the chat is empty. */
    private void seedWelcomeIfEmpty() {
        if (chatAdapter.getItemCount() == 0) {
            respondToUser(getString(R.string.chat_welcome_1));
            respondToUser(getString(R.string.chat_welcome_2));
            respondToUser(getString(R.string.chat_welcome_3));
        }
    }

    /** Handles sending a message, quick-support routing, and fallback to LLM. */
    private void sendMessage() {
        String message = userInput.getText().toString().trim();
        if (message.isEmpty()) return;

        hideKeyboard();

        // Add user message
        chatAdapter.addMessage(new ChatMessage(message, ChatMessage.USER));
        saveMessageToDb("user", message);
        chatRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);

        // UI: busy
        userInput.setText("");
        progressBar.setVisibility(View.VISIBLE);
        sendButton.setEnabled(false);

        String lowerMsg = message.toLowerCase();

        // Step 1: FAQ / quick-support match
        for (String key : supportPrompts.keySet()) {
            if (lowerMsg.contains(key)) {
                String botReply = supportPrompts.get(key);
                supportPromptCount++;

                runOnUiThread(() -> {
                    respondToUser(botReply);

                    if ("is this a scam".equals(key)) {
                        new Handler(Looper.getMainLooper())
                                .postDelayed(() ->
                                                respondToUser("Yes, this appears to be a scam. Please avoid engaging with it."),
                                        2000);
                    }

                    if (supportPromptCount >= MAX_SUPPORT_PROMPTS) {
                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            respondToUser("Transferring you to the assistant for more detailed help…");
                            Log.d("ChatFlow", "Escalating to Ollama after quick-support threshold. Prompt: " + message);
                            Toast.makeText(ChatAssistantActivity.this, "Sending to assistant…", Toast.LENGTH_SHORT).show();

                            callLlm(message);
                        }, 1200);
                    } else {
                        resetUiIdle();
                    }
                });

                return;
            }
        }

        // Step 2: no FAQ match → call LLM
        callLlm(message);
    }

    private void callLlm(String message) {
        ollamaClient.getResponse(message, response ->
                runOnUiThread(() -> respondToUser(response))
        );
    }

    /** Adds a bot response and restores UI state. */
    private void respondToUser(String response) {
        progressBar.setVisibility(View.GONE);
        sendButton.setEnabled(true);

        chatAdapter.addMessage(new ChatMessage(response, ChatMessage.BOT));
        saveMessageToDb("bot", response);
        chatRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount() - 1);
    }

    /** Return UI to idle state (used after quick reply with no escalation). */
    private void resetUiIdle() {
        progressBar.setVisibility(View.GONE);
        sendButton.setEnabled(true);
    }

    /** Saves a message into Room DB. */
    private void saveMessageToDb(String sender, String text) {
        new Thread(() -> {
            try {
                ChatDatabase db = ChatDatabase.getInstance(getApplicationContext());
                db.chatMessageDao().insert(new ChatMessageEntity(sender, text, System.currentTimeMillis()));
            } catch (Exception e) {
                Log.w("ChatDB", "Failed to save message", e);
            }
        }).start();
    }

    /** Hides the soft keyboard safely. */
    private void hideKeyboard() {
        try {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null && getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            }
        } catch (Exception ignored) { }
    }

    // ---------- Support Feedback ----------
    private void openSupportFeedback() {
        Intent intent = new Intent(this,
                com.example.smishingdetectionapp.ui.SupportFeedbackActivity.class);
        startActivity(intent);
    }
}
