package com.example.everydaycompanion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class DashboardActivity extends AppCompatActivity {

    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private TextView thoughtText;
    private ImageView refreshIcon;

    private final int[] icons = {
            R.drawable.task_manager, R.drawable.expense_tracker, R.drawable.bmi_calculator,
            R.drawable.weather, R.drawable.flashlight, R.drawable.qr_scanner
    };

    private final String[] titles = {
            "Task Manager", "Expense Tracker", "BMI Calculator",
            "Weather App", "Flashlight", "QR Scanner"
    };

    private final Class<?>[] activities = {
            TaskManagerActivity.class, ExpenseTrackerActivity.class, BMICalculatorActivity.class,
            WeatherActivity.class, FlashlightActivity.class, QrScannerActivity.class
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        GridLayout gridLayout = findViewById(R.id.gridLayout);
        Button btnLogout = findViewById(R.id.btn_logout);
        thoughtText = findViewById(R.id.thought_text);
        refreshIcon = findViewById(R.id.refresh_icon);

        // Lazy load: fetch only on click
        refreshIcon.setOnClickListener(v -> fetchThought());

        setupWidgets(gridLayout);

        btnLogout.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(DashboardActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupWidgets(GridLayout gridLayout) {
        for (int i = 0; i < icons.length; i++) {
            View widgetView = getLayoutInflater().inflate(R.layout.widget_item, gridLayout, false);
            ImageView icon = widgetView.findViewById(R.id.widget_icon);
            TextView title = widgetView.findViewById(R.id.widget_title);
            TextView desc = widgetView.findViewById(R.id.widget_description);

            icon.setImageResource(icons[i]);
            title.setText(titles[i]);
            desc.setText(titles[i]);

            int finalI = i;
            widgetView.setOnClickListener(v -> {
                Intent intent = new Intent(DashboardActivity.this, activities[finalI]);
                startActivity(intent);
            });

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.width = 0;
            params.height = GridLayout.LayoutParams.WRAP_CONTENT;
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.rowSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            widgetView.setLayoutParams(params);

            gridLayout.addView(widgetView);
        }
    }

    private void fetchThought() {
        new FetchThoughtTask(this).execute("https://flask-api-vigc.onrender.com/daily-reminder");
    }

    // AsyncTask using WeakReference to prevent memory leaks
    private static class FetchThoughtTask extends AsyncTask<String, Void, String> {
        private final WeakReference<DashboardActivity> activityRef;

        FetchThoughtTask(DashboardActivity activity) {
            activityRef = new WeakReference<>(activity);
        }

        @Override
        protected String doInBackground(String... urls) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                return result.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (reader != null) try { reader.close(); } catch (Exception ignored) {}
                if (connection != null) connection.disconnect();
            }
        }

        @Override
        protected void onPostExecute(String result) {
            DashboardActivity activity = activityRef.get();
            if (activity == null || activity.isFinishing()) return;

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    String reminder = jsonObject.getString("reminder");
                    activity.thoughtText.setText(reminder);
                } catch (Exception e) {
                    activity.thoughtText.setText("Failed to load thought. Tap 🔄 to Retry");
                }
            } else {
                activity.thoughtText.setText("Failed to load thought. Tap 🔄 to Retry");
            }
        }
    }
}
