package com.example.smishingdetectionapp.detections;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.example.smishingdetectionapp.ui.WidgetDataManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.radiobutton.MaterialRadioButton;

import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DetectionsActivity extends AppCompatActivity {

    private ListView detectionLV;
    DatabaseAccess databaseAccess;

    private ActivityResultLauncher<Intent> createCsvLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detections);

        // Bottom navigation
        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_home);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (id == R.id.nav_report) {
                Intent i = new Intent(this, CommunityReportActivity.class);
                i.putExtra("source", "home");
                startActivity(i);
                overridePendingTransition(0,0);
                finish();
                return true;

            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;

            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            return false;
        });

        // Back button
        ImageButton detections_back = findViewById(R.id.detections_back);
        detections_back.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        // List + DataBase
        detectionLV = findViewById(R.id.lvDetectionsList);
        databaseAccess = new DatabaseAccess(getApplicationContext());
        databaseAccess.open();
        refreshList();

        // Widgets updating function
        int detectionCount = databaseAccess.getCounter();
        WidgetDataManager.updateDetectionCount(this, detectionCount);
        WidgetDataManager.updateSafeDayStreak(this);

        // Initial adapter
        Cursor cursor = DatabaseAccess.db.rawQuery("SELECT * FROM Detections", null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        // Search bar
        EditText detSearch = findViewById(R.id.searchTextBox);
        detSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchDB(s.toString());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Filtering feature
        ImageView filterBtn = findViewById(R.id.filterBtn);
        filterBtn.setOnClickListener(v -> {
            SmartFilterBottomSheet filterFragment = new SmartFilterBottomSheet();
            filterFragment.setFilterListener((newestFirst, containsLink, todayOnly, last7DaysOnly, selectedYears, startDate, endDate) -> {
                StringBuilder query = new StringBuilder("SELECT * FROM Detections");
                boolean hasCondition = false;

                if (containsLink) {
                    query.append(" WHERE (Message LIKE '%http%' OR Message LIKE '%www%')");
                    hasCondition = true;
                }

                if (todayOnly) {
                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    query.append(hasCondition ? " AND " : " WHERE ").append("Date LIKE '").append(today).append("%'");
                    hasCondition = true;
                }

                if (last7DaysOnly) {
                    long sevenDaysAgoMillis = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
                    String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    String sevenDaysAgo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(sevenDaysAgoMillis));
                    query.append(hasCondition ? " AND " : " WHERE ").append("Date BETWEEN '").append(sevenDaysAgo).append("' AND '").append(today).append("'");
                    hasCondition = true;
                }

                if (startDate != null && endDate != null) {
                    query.append(hasCondition ? " AND " : " WHERE ").append("Date BETWEEN '").append(startDate).append("' AND '").append(endDate).append("'");
                    hasCondition = true;
                }

                if (!selectedYears.isEmpty()) {
                    StringBuilder yearCondition = new StringBuilder();
                    for (int i = 0; i < selectedYears.size(); i++) {
                        if (i > 0) yearCondition.append(" OR ");
                        yearCondition.append("SUBSTR(Date, 1, 4) = '").append(selectedYears.get(i)).append("'");
                    }
                    query.append(hasCondition ? " AND (" : " WHERE (").append(yearCondition).append(")");
                }

                query.append(newestFirst ? " ORDER BY Date DESC" : " ORDER BY Date ASC");

                Cursor filteredCursor = DatabaseAccess.db.rawQuery(query.toString(), null);
                DisplayDataAdapterView filteredAdapter = new DisplayDataAdapterView(this, filteredCursor);
                detectionLV.setAdapter(filteredAdapter);
                filteredAdapter.notifyDataSetChanged();
            });

            filterFragment.show(getSupportFragmentManager(), filterFragment.getTag());
        });

        // Delete on long press
        detectionLV.setOnItemLongClickListener((parent, view, position, id) -> {
            View bottomSheetDel = getLayoutInflater().inflate(R.layout.popup_deleteitem, null);
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(DetectionsActivity.this);
            bottomSheetDialog.setContentView(bottomSheetDel);
            bottomSheetDialog.show();

            Button cancel = bottomSheetDel.findViewById(R.id.delItemCancel);
            Button confirm = bottomSheetDel.findViewById(R.id.DelItemConfirm);

            cancel.setOnClickListener(v1 -> bottomSheetDialog.dismiss());
            confirm.setOnClickListener(v12 -> {
                DeleteRow(String.valueOf(id));
                refreshList();
                bottomSheetDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Detection Deleted!", Toast.LENGTH_SHORT).show();
            });

            return true;
        });


        // CSV create-document launcher
        createCsvLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        Uri uri = result.getData().getData();
                        if (uri != null) {
                            Cursor toExport = null;
                            if (detectionLV.getAdapter() instanceof CursorAdapter) {
                                toExport = ((CursorAdapter) detectionLV.getAdapter()).getCursor();
                            }
                            boolean ok = exportCursorToCsvUri(uri, toExport);
                            Toast.makeText(this, ok ? "CSV saved" : "Failed to save CSV", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "No file selected", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Export cancelled", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Export button
        Button exportReportBtn = findViewById(R.id.exportReportBtn);
        exportReportBtn.setOnClickListener(v -> showExportDialog());
    }

    // Centered popup dialog that inflates popup_export_report.xml
    private void showExportDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.popup_export_report);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setGravity(Gravity.CENTER);
        }

        MaterialRadioButton rbPdf = dialog.findViewById(R.id.radioPdf);
        MaterialRadioButton rbCsv = dialog.findViewById(R.id.radioCsv);
        MaterialButton exportNow = dialog.findViewById(R.id.exportNowBtn);

        exportNow.setOnClickListener(v -> {
            dialog.dismiss();
            if (rbPdf != null && rbPdf.isChecked()) {
                exportDetectionsToPDF();
            } else {
                launchCreateCsv();
            }
        });

        dialog.show();
    }

    // Launch the SAF to create CSV file
    private void launchCreateCsv() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        String name = "detections_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".csv";
        intent.putExtra(Intent.EXTRA_TITLE, name);
        createCsvLauncher.launch(intent);
    }

    // Search/sort/list helpers
    public void searchDB(String search) {
        String searchQuery = "SELECT * FROM Detections WHERE Phone_Number LIKE '%" + search + "%' OR Message LIKE '%" + search + "%' OR Date LIKE '%" + search + "%'";
        Cursor cursor = DatabaseAccess.db.rawQuery(searchQuery, null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void sortONDB() {
        String searchQuery = "SELECT * FROM Detections ORDER BY Date ASC";
        Cursor cursor = DatabaseAccess.db.rawQuery(searchQuery, null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void sortNODB() {
        String searchQuery = "SELECT * FROM Detections ORDER BY Date DESC";
        Cursor cursor = DatabaseAccess.db.rawQuery(searchQuery, null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void refreshList() {
        Cursor cursor = DatabaseAccess.db.rawQuery("SELECT * FROM Detections", null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void DeleteRow(String id) {
        DatabaseAccess.db.delete("Detections", "_id = ?", new String[]{id});
    }


    // PDF export feature
    private void exportDetectionsToPDF() {
        Cursor cursor = DatabaseAccess.db.rawQuery("SELECT * FROM Detections", null);
        if (cursor.getCount() == 0) {
            Toast.makeText(this, "No detections to export", Toast.LENGTH_SHORT).show();
            return;
        }

        Document document = new Document();
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "detections_report.pdf");
        String filePath = file.getAbsolutePath();

        try {
            PdfWriter.getInstance(document, new FileOutputStream(filePath));
            document.open();
            document.add(new Paragraph("Smishing Detections Report\n\n"));

            while (cursor.moveToNext()) {
                String phone = cursor.getString(cursor.getColumnIndexOrThrow("Phone_Number"));
                String message = cursor.getString(cursor.getColumnIndexOrThrow("Message"));
                String date = cursor.getString(cursor.getColumnIndexOrThrow("Date"));

                document.add(new Paragraph("Phone: " + phone));
                document.add(new Paragraph("Message: " + message));
                document.add(new Paragraph("Date: " + date));
                document.add(new Paragraph("\n"));
            }

            document.close();

            MediaScannerConnection.scanFile(
                    this,
                    new String[] { file.getAbsolutePath() },
                    new String[] { "application/pdf" },
                    null
            );

            Toast.makeText(this, "PDF exported to: " + filePath, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to export PDF", Toast.LENGTH_SHORT).show();
        }
    }


    // CSV export helpers
    private boolean exportCursorToCsvUri(Uri uri, Cursor currentCursor) {
        Cursor cursor = null;
        boolean closeAtEnd = false;

        try {
            if (currentCursor != null) {
                cursor = currentCursor;
            } else {
                cursor = DatabaseAccess.db.rawQuery(
                        "SELECT Phone_Number, Message, Date FROM Detections", null
                );
                closeAtEnd = true;
            }

            String csv = buildCsvFromCursor(cursor);

            try (OutputStream os = getContentResolver().openOutputStream(uri);
                 OutputStreamWriter osw = new OutputStreamWriter(os);
                 BufferedWriter bw = new BufferedWriter(osw)) {
                bw.write(csv);
                bw.flush();
            }

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (closeAtEnd && cursor != null) {
                cursor.close();
            }
        }
    }

    private String buildCsvFromCursor(Cursor cursor) {
        StringBuilder sb = new StringBuilder();
        sb.append("Phone Number,Message,Date\n");

        if (cursor == null) return sb.toString();

        int colPhone = cursor.getColumnIndex("Phone_Number");
        int colMessage = cursor.getColumnIndex("Message");
        int colDate = cursor.getColumnIndex("Date");

        cursor.moveToPosition(-1);
        while (cursor.moveToNext()) {
            String phone = (colPhone >= 0) ? cursor.getString(colPhone) : "";
            String message = (colMessage >= 0) ? cursor.getString(colMessage) : "";
            String date = (colDate >= 0) ? cursor.getString(colDate) : "";

            sb.append(safeCsv(phone)).append(',')
                    .append(safeCsv(message)).append(',')
                    .append(safeCsv(date)).append('\n');
        }
        return sb.toString();
    }

    // PROTECTED CSV ENCODER (Security Feature)
    private String safeCsv(String s) {
        if (s == null) return "";

        // Find first non-whitespace character (spaces, tabs, etc.)
        int i = 0;
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;

        boolean dangerous = false;
        if (i < s.length()) {
            char c = s.charAt(i);
            // Excel/Sheets treat = + - @ as formulas when first non-whitespace
            if (c == '=' || c == '+' || c == '-' || c == '@') {
                dangerous = true;
            }
        }

        // CSV-escape quotes
        String out = s.replace("\"", "\"\"");

        // Neutralize formula evaluation by prefixing apostrophe
        if (dangerous) {
            out = "'" + out;
        }

        // Quote fields that contain CSV special chars
        boolean needsQuoting = out.contains(",") || out.contains("\"") || out.contains("\n") || out.contains("\r");
        return needsQuoting ? "\"" + out + "\"" : out;
    }
}