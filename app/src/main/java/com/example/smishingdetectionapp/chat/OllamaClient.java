package com.example.smishingdetectionapp.chat;

import android.database.Cursor;
import android.util.Log;

import com.example.smishingdetectionapp.detections.DatabaseAccess;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OllamaClient {

    // Flask proxy base (Android emulator -> host loopback)
    // Your Flask app exposes POST /chat/api/generate on port 5000
    private static final String BASE_URL = "http://10.0.2.2:5000";

    private static final String TAG = "OLLAMA_CLIENT";

    // HTTP client with extended timeouts
    private final OkHttpClient client;

    // Local database instance
    private final DatabaseAccess databaseAccess;

    // Callback for async replies
    public interface ResponseCallback {
        void onResponse(String response);
    }

    public OllamaClient(DatabaseAccess databaseAccess) {
        this.databaseAccess = databaseAccess;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(90, TimeUnit.SECONDS)
                .build();
    }

    // Try answering from local DB for "reports"/"detections"
    private String handleDatabaseQuery(String message) {
        String lower = message == null ? "" : message.toLowerCase(Locale.getDefault());
        if (!(lower.contains("reports") || lower.contains("detections"))) return null;

        databaseAccess.open();
        Cursor cursor = null;
        try {
            if (lower.contains("reports")) {
                if (lower.contains("today")) {
                    String today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                    cursor = databaseAccess.getReportsForDate(today);
                    return formatResults(cursor, "reports", today);
                } else if (lower.contains("yesterday")) {
                    String yest = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
                            .format(new Date(System.currentTimeMillis() - 86400000L));
                    cursor = databaseAccess.getReportsForDate(yest);
                    return formatResults(cursor, "reports", yest);
                } else {
                    // try dd-MM-yyyy or dd/MM/yyyy in the message
                    String datePattern = "(0[1-9]|[12][0-9]|3[01])[-/](0[1-9]|1[012])[-/](\\d{4})";
                    Matcher matcher = Pattern.compile(datePattern).matcher(lower);
                    if (matcher.find()) {
                        String specific = matcher.group().replace("/", "-");
                        specific = normalizeDate(specific);
                        cursor = databaseAccess.getReportsForSpecificDate(specific);
                        if (cursor != null && cursor.getCount() > 0) {
                            return formatResults(cursor, "reports", specific);
                        } else {
                            return "No reports found for " + specific;
                        }
                    } else {
                        cursor = databaseAccess.getAllReports();
                        return formatResults(cursor, "reports", null);
                    }
                }
            }
            // You can extend here for "detections" similar to reports…
            return "I can help with 'reports' or 'detections'. Try 'today's reports' or 'all detections'.";
        } finally {
            if (cursor != null) cursor.close();
            databaseAccess.close();
        }
    }

    private String normalizeDate(String date) {
        try {
            SimpleDateFormat fmt = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            Date parsed = fmt.parse(date);
            return fmt.format(parsed);
        } catch (Exception e) {
            return date;
        }
    }

    private String formatResults(Cursor cursor, String type, String date) {
        if (cursor != null && cursor.moveToFirst()) {
            StringBuilder sb = new StringBuilder();
            if (date != null) {
                sb.append("Here are the ").append(type).append(" for ").append(date).append(":\n\n");
            } else {
                sb.append("Here are all ").append(type).append(":\n\n");
            }

            int phoneIx = cursor.getColumnIndex(DatabaseAccess.DatabaseOpenHelper.KEY_PHONENUMBER);
            int dateIx  = cursor.getColumnIndex(DatabaseAccess.DatabaseOpenHelper.KEY_DATE);
            int msgIx   = cursor.getColumnIndex(DatabaseAccess.DatabaseOpenHelper.KEY_MESSAGE);

            do {
                if (phoneIx >= 0 && dateIx >= 0 && msgIx >= 0) {
                    sb.append(" Phone: ").append(cursor.getString(phoneIx))
                            .append("\n Date: ").append(cursor.getString(dateIx))
                            .append("\n Message: ").append(cursor.getString(msgIx))
                            .append("\n\n");
                } else {
                    sb.append(" Missing expected columns in database.\n\n");
                }
            } while (cursor.moveToNext());

            return sb.toString();
        }
        return (date != null) ? ("No " + type + " found for " + date) : ("No " + type + " found");
    }

    // Public entry: DB first, otherwise call Flask/Ollama
    public void getResponse(String message, ResponseCallback callback) {
        // 1) Local DB?
        String db = handleDatabaseQuery(message);
        if (db != null) {
            callback.onResponse(db);
            return;
        }

        // 2) Call Flask proxy -> Ollama
        try {
            JSONObject json = new JSONObject();
            json.put("model", "llama3.2:1b");
            json.put("stream", false);
            json.put("system",
                    "You are a smishing and cybersecurity assistant. Help users identify scams, "
                            + "offer digital safety tips, and guide them on reporting smishing.");
            json.put("prompt", message);

            RequestBody body = RequestBody.create(
                    json.toString(),
                    MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                    .url(BASE_URL + "/chat/api/generate")
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "Network error: " + e.getMessage(), e);
                    callback.onResponse("Sorry, connection to AI failed.");
                }

                @Override public void onResponse(Call call, Response response) throws IOException {
                    String bodyStr = (response.body() != null) ? response.body().string() : "";
                    if (response.isSuccessful()) {
                        Log.d("OLLAMA_RESPONSE", bodyStr);
                        try {
                            JSONObject obj = new JSONObject(bodyStr);
                            String botReply = obj.optString("response", "").trim();
                            if (botReply.isEmpty()) {
                                callback.onResponse("AI returned empty response.");
                            } else {
                                callback.onResponse(botReply);
                            }
                        } catch (Exception parseEx) {
                            Log.e(TAG, "Parse error", parseEx);
                            callback.onResponse("AI returned invalid response format.");
                        }
                    } else {
                        Log.e(TAG, "AI server error: " + response.code() + " body: " + bodyStr);
                        callback.onResponse("AI server error: " + response.code());
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error preparing AI request", e);
            callback.onResponse("Sorry, an error occurred while contacting AI.");
        }
    }
}