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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.smishingdetectionapp.Community.CommunityReportActivity;
import com.example.smishingdetectionapp.MainActivity;
import com.example.smishingdetectionapp.R;
import com.example.smishingdetectionapp.SettingsActivity;
import com.example.smishingdetectionapp.navigation.BottomNavCoordinator;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetectionsActivity extends AppCompatActivity {

    private ListView detectionLV;
    DatabaseAccess databaseAccess;

    private ActivityResultLauncher<Intent> createCsvLauncher;
    private TextView activeFilterLabel;

    private com.google.android.material.chip.Chip chipAll;
    private com.google.android.material.chip.Chip chipContainsLink;
    private com.google.android.material.chip.Chip chipToday;
    private com.google.android.material.chip.Chip chipLast7Days;
    private TextView noResultsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detections);

        BottomNavCoordinator.setup(this, R.id.nav_home);

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
        activeFilterLabel = findViewById(R.id.activeFilterLabel);
        noResultsText = findViewById(R.id.noResultsText);
        // Quick filter chips
        chipAll = findViewById(R.id.chipAll);
        chipContainsLink = findViewById(R.id.chipContainsLink);
        chipToday = findViewById(R.id.chipToday);
        chipLast7Days = findViewById(R.id.chipLast7Days);

        chipAll.setOnClickListener(v -> {
            chipContainsLink.setChecked(false);
            chipToday.setChecked(false);
            chipLast7Days.setChecked(false);
            chipAll.setChecked(true);
            refreshList();
            activeFilterLabel.setVisibility(View.GONE);
            noResultsText.setVisibility(View.GONE);
            detectionLV.setVisibility(View.VISIBLE);
        });

        chipContainsLink.setOnClickListener(v -> {
            chipAll.setChecked(false);
            chipToday.setChecked(false);
            chipLast7Days.setChecked(false);
            chipContainsLink.setChecked(true);
            filterByQuery("SELECT * FROM Detections WHERE Message LIKE '%http%' OR Message LIKE '%www%'");
            activeFilterLabel.setText("Filter: Contains Link");
            activeFilterLabel.setVisibility(View.VISIBLE);
        });

        chipToday.setOnClickListener(v -> {
            chipAll.setChecked(false);
            chipContainsLink.setChecked(false);
            chipLast7Days.setChecked(false);
            chipToday.setChecked(true);
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            filterByQuery("SELECT * FROM Detections WHERE Date LIKE '" + today + "%'");
            activeFilterLabel.setText("Filter: Today");
            activeFilterLabel.setVisibility(View.VISIBLE);
        });

        chipLast7Days.setOnClickListener(v -> {
            chipAll.setChecked(false);
            chipContainsLink.setChecked(false);
            chipToday.setChecked(false);
            chipLast7Days.setChecked(true);
            long sevenDaysAgoMillis = System.currentTimeMillis() - (7L * 24 * 60 * 60 * 1000);
            String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
            String sevenDaysAgo = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date(sevenDaysAgoMillis));
            filterByQuery("SELECT * FROM Detections WHERE Date BETWEEN '" + sevenDaysAgo + "' AND '" + today + "'");
            activeFilterLabel.setText("Filter: Last 7 Days");
            activeFilterLabel.setVisibility(View.VISIBLE);
        });
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
                List<String> activeFilters = new ArrayList<>();
                if (containsLink) activeFilters.add("Contains Link");
                if (todayOnly) activeFilters.add("Today");
                if (last7DaysOnly) activeFilters.add("Last 7 Days");
                if (newestFirst) activeFilters.add("Newest First");
                if (startDate != null) activeFilters.add("Date Range");

                if (activeFilters.isEmpty()) {
                    activeFilterLabel.setVisibility(View.GONE);
                } else {
                    activeFilterLabel.setText("Filters: " + String.join(", ", activeFilters));
                    activeFilterLabel.setVisibility(View.VISIBLE);
                }
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
    }

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

    private void launchCreateCsv() {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("text/csv");
        String name = "detections_" + new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date()) + ".csv";
        intent.putExtra(Intent.EXTRA_TITLE, name);
        createCsvLauncher.launch(intent);
    }

    public void searchDB(String search) {
        String searchQuery = "SELECT * FROM Detections WHERE Phone_Number LIKE '%" + search + "%' OR Message LIKE '%" + search + "%' OR Date LIKE '%" + search + "%'";
        Cursor cursor = DatabaseAccess.db.rawQuery(searchQuery, null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void filterByQuery(String query) {
        Cursor cursor = DatabaseAccess.db.rawQuery(query, null);
        DisplayDataAdapterView adapter = new DisplayDataAdapterView(this, cursor);
        detectionLV.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        if (cursor.getCount() == 0) {
            detectionLV.setVisibility(View.GONE);
            noResultsText.setVisibility(View.VISIBLE);
        } else {
            detectionLV.setVisibility(View.VISIBLE);
            noResultsText.setVisibility(View.GONE);
        }
    }

    private View getNoResultsView() {
        TextView noResults = new TextView(this);
        noResults.setText("No detections match this filter.\nTap 'All' to reset.");
        noResults.setGravity(android.view.Gravity.CENTER);
        noResults.setTextSize(16);
        noResults.setPadding(32, 64, 32, 32);
        noResults.setTextColor(getResources().getColor(R.color.grey, getTheme()));
        return noResults;
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

    private String safeCsv(String s) {
        if (s == null) return "";

        int i = 0;
        while (i < s.length() && Character.isWhitespace(s.charAt(i))) i++;

        boolean dangerous = false;
        if (i < s.length()) {
            char c = s.charAt(i);
            if (c == '=' || c == '+' || c == '-' || c == '@') {
                dangerous = true;
            }
        }

        String out = s.replace("\"", "\"\"");

        if (dangerous) {
            out = "'" + out;
        }

        boolean needsQuoting = out.contains(",") || out.contains("\"") || out.contains("\n") || out.contains("\r");
        return needsQuoting ? "\"" + out + "\"" : out;
    }
}
