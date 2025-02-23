package com.example.everydaycompanion;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class BMICalculatorActivity extends AppCompatActivity {
    EditText etHeight, etWeight;
    Button btnCalculate, btnHome;
    TextView tvResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi); // Link to XML layout

        // Initialize UI Elements
        etHeight = findViewById(R.id.et_height);
        etWeight = findViewById(R.id.et_weight);
        btnCalculate = findViewById(R.id.btn_calculate);
        btnHome = findViewById(R.id.btn_home);
        tvResult = findViewById(R.id.tv_result);

        // Calculate BMI on button click
        btnCalculate.setOnClickListener(v -> calculateBMI());

        // Navigate to Home (Dashboard)
        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(BMICalculatorActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish(); // Close current activity
        });
    }

    private void calculateBMI() {
        String heightStr = etHeight.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();

        // Validate Input
        if (heightStr.isEmpty() || weightStr.isEmpty()) {
            Toast.makeText(this, "Please enter height and weight!", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double height = Double.parseDouble(heightStr) / 100; // Convert cm to meters
            double weight = Double.parseDouble(weightStr);

            if (height <= 0 || weight <= 0) {
                Toast.makeText(this, "Invalid height or weight!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Calculate BMI
            double bmi = weight / (height * height);
            String category = getBMICategory(bmi);

            // Display Result
            tvResult.setText(String.format("Your BMI: %.2f\nCategory: %s", bmi, category));

        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter valid numbers!", Toast.LENGTH_SHORT).show();
        }
    }

    private String getBMICategory(double bmi) {
        if (bmi < 18.5) return "Underweight";
        else if (bmi < 24.9) return "Normal Weight";
        else if (bmi < 29.9) return "Overweight";
        else return "Obese";
    }
}
