package com.example.smishingdetectionapp;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.Manifest;
import androidx.appcompat.app.AppCompatActivity;
import com.chaquo.python.Python;
import com.chaquo.python.PyObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.smishingdetectionapp.databinding.ActivityMainBinding;
import com.example.smishingdetectionapp.detections.DatabaseAccess;
import com.example.smishingdetectionapp.detections.DetectionsActivity;

import com.example.smishingdetectionapp.notifications.NotificationPermissionDialogFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Chaquopy Python instance
        Python py = Python.getInstance();

        // Test the Python script with pickled files
        testPythonScriptWithPickledFiles(py);
    }

    @SuppressLint("SetTextI18n")
    private void testPythonScriptWithPickledFiles(Python py) {
        AssetManager assetManager = getAssets();
        try {
            // Load pickled files
            InputStream modelStream = assetManager.open("rf_model_pickle.pkl");
            InputStream vectorizerStream = assetManager.open("rf_vectorizer_pickle.pkl");

            byte[] modelBytes = readBytesFromInputStream(modelStream);
            byte[] vectorizerBytes = readBytesFromInputStream(vectorizerStream);

            // Define Python code as a string
            String pythonCode =
                    "import pickle\n" +
                            "import io\n" +
                            "def load_model_and_vectorizer(model_data, vectorizer_data):\n" +
                            "    model = pickle.load(io.BytesIO(model_data))\n" +
                            "    vectorizer = pickle.load(io.BytesIO(vectorizer_data))\n" +
                            "    return model, vectorizer\n" +
                            "def predict(model, vectorizer, text):\n" +
                            "    features = vectorizer.transform([text])\n" +
                            "    prediction = model.predict(features)\n" +
                            "    return prediction.tolist()\n";

            // Execute Python code
            PyObject pyBuiltins = py.getModule("builtins");
            pyBuiltins.callAttr("exec", pythonCode);

            // Load pickled files and call functions
            PyObject result = pyBuiltins.callAttr("load_model_and_vectorizer", modelBytes, vectorizerBytes);
            PyObject model = result.get("model");
            PyObject vectorizer = result.get("vectorizer");

            // Test the prediction function
            PyObject prediction = pyBuiltins.callAttr("predict", model, vectorizer, "Sample text for prediction");
            Log.d("MainActivity", "Prediction result: " + prediction.toString());

        } catch (IOException e) {
            Log.e("MainActivity", "Error loading pickled files or executing Python code", e);
        } catch (Exception e) {
            Log.e("MainActivity", "Unexpected error", e);
        }

        BottomNavigationView nav = findViewById(R.id.bottom_navigation);
        nav.setSelectedItemId(R.id.nav_home);
        nav.setOnItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_news) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
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

        Button debug_btn = findViewById(R.id.debug_btn);
        debug_btn.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, DebugActivity.class)));

        Button detections_btn = findViewById(R.id.detections_btn);
        detections_btn.setOnClickListener(v -> {
            startActivity(new Intent(this, DetectionsActivity.class));
            finish();
        });

        // Database connection
        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(getApplicationContext());
        databaseAccess.open();
        //setting counter from result
        TextView total_count;
        total_count = findViewById(R.id.total_counter);
        total_count.setText(""+databaseAccess.getCounter());
        //closing the connection
        //databaseAccess.close();
        //TODO: Add functionality for new detections.

        // Setting counter from the result
        total_count = findViewById(R.id.total_counter);
        total_count.setText("" + databaseAccess.getCounter());

        // Closing the connection
        databaseAccess.close();

    }

    private byte[] readBytesFromInputStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        inputStream.close();
        return byteArrayOutputStream.toByteArray();
    private boolean areNotificationsEnabled() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, 1);
        }

        return NotificationManagerCompat.from(this).areNotificationsEnabled();
    }

    private void showNotificationPermissionDialog() {
        NotificationPermissionDialogFragment dialogFragment = new NotificationPermissionDialogFragment();
        dialogFragment.show(getSupportFragmentManager(), "notificationPermission");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration) || super.onSupportNavigateUp();
    }
}
