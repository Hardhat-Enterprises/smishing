package com.example.smishingdetectionapp;
import android.annotation.SuppressLint;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import com.chaquo.python.Python;
import com.chaquo.python.PyObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

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
    }
}
