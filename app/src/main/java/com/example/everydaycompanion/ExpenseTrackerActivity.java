package com.example.everydaycompanion;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ExpenseTrackerActivity extends AppCompatActivity {
    EditText etAmount, etDescription, etDate;
    RadioGroup rgType;
    Button btnAddExpense;
    TableLayout tableLayout;
    ExpenseDatabaseHelper dbHelper;
    Calendar calendar;
    TextView tvBalance;
    Button btnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_tracker);

        etAmount = findViewById(R.id.et_amount);
        etDescription = findViewById(R.id.et_description);
        etDate = findViewById(R.id.et_date);
        rgType = findViewById(R.id.rg_type);
        btnAddExpense = findViewById(R.id.btn_add_expense);
        tableLayout = findViewById(R.id.table_layout);
        tvBalance = findViewById(R.id.tv_balance);
        dbHelper = new ExpenseDatabaseHelper(this);
        calendar = Calendar.getInstance();
        btnHome = findViewById(R.id.btn_home); // Link Home Button

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(ExpenseTrackerActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish(); // Closes current activity
        });

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        etDate.setText(sdf.format(calendar.getTime()));

        etDate.setOnClickListener(v -> openDatePicker());
        btnAddExpense.setOnClickListener(v -> addExpense());

        loadExpenses();
    }

    private void openDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(year, month, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            etDate.setText(sdf.format(calendar.getTime()));
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void addExpense() {
        String amountStr = etAmount.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String date = etDate.getText().toString().trim();
        int selectedId = rgType.getCheckedRadioButtonId();

        if (amountStr.isEmpty() || description.isEmpty() || selectedId == -1) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        // If expense is selected, convert amount to negative
        if (selectedId == R.id.rb_expense) {
            amount = -amount;
        }

        boolean inserted = dbHelper.insertExpense(amount, description, date);
        if (inserted) {
            Toast.makeText(this, "Transaction added!", Toast.LENGTH_SHORT).show();
            etAmount.setText("");
            etDescription.setText("");
            loadExpenses();
        } else {
            Toast.makeText(this, "Error adding transaction", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadExpenses() {
        tableLayout.removeViews(1, Math.max(0, tableLayout.getChildCount() - 1));
        Cursor cursor = dbHelper.getAllExpenses();
        double balance = 0.0;

        if (cursor.getCount() == 0) {
            tvBalance.setText("Remaining Balance: ₹0.00");
            return;
        }

        while (cursor.moveToNext()) {
            double amount = cursor.getDouble(1);
            String description = cursor.getString(2);
            String date = cursor.getString(3);

            balance += amount; // Subtract expenses, add income

            TableRow row = new TableRow(this);
            row.setPadding(4, 4, 4, 4);

            // Determine color: Light green for income, Light red for expense
            int backgroundColor = (amount >= 0) ? Color.parseColor("#E8F5E9") : Color.parseColor("#FFEBEE");
            row.setBackgroundColor(backgroundColor);

            row.addView(createTextView(String.format("₹ %.2f", amount)));
            row.addView(createTextView(description));
            row.addView(createTextView(date));

            tableLayout.addView(row);
        }

        cursor.close();

        // Update balance display
        tvBalance.setText(String.format("Remaining Balance: ₹ %.2f", balance));
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(8, 8, 8, 8);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(16);
        return textView;
    }
}
