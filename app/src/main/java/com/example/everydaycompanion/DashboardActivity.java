package com.example.everydaycompanion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardActivity extends AppCompatActivity {

    private static final String PREF_NAME = "LoginPrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        GridLayout gridLayout = findViewById(R.id.gridLayout);
        Button btnLogout = findViewById(R.id.btn_logout);

        // Widget Data
        int[] icons = {R.drawable.task_manager, R.drawable.expense_tracker, R.drawable.bmi_calculator,
                R.drawable.weather, R.drawable.news, R.drawable.flashlight};

        String[] titles = {"Task Manager", "Expense Tracker", "BMI Calculator",
                "Weather App", "News Feed", "Flashlight"};

        Class<?>[] activities = {TaskManagerActivity.class, ExpenseTrackerActivity.class,BMICalculatorActivity.class};
//                BmiCalculatorActivity.class, WeatherActivity.class,
//                NewsFeedActivity.class, FlashlightActivity.class};

        for (int i = 0; i < icons.length; i++) {
            View widgetView = getLayoutInflater().inflate(R.layout.widget_item, null);
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

        // Logout Button
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
}
