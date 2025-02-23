package com.example.everydaycompanion;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class TaskManagerActivity extends AppCompatActivity {
    private TaskDatabaseHelper dbHelper;
    private EditText etTask;
    private LinearLayout taskContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_manager);

        dbHelper = new TaskDatabaseHelper(this);
        etTask = findViewById(R.id.et_task);
        taskContainer = findViewById(R.id.task_list);
        Button btnAddTask = findViewById(R.id.btn_add_task);
        Button btnHome = findViewById(R.id.btn_home);

        loadTasks();

        btnAddTask.setOnClickListener(v -> {
            String taskName = etTask.getText().toString().trim();
            if (!taskName.isEmpty()) {
                dbHelper.insertTask(taskName, false);
                etTask.setText("");
                loadTasks();
            }
        });

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(TaskManagerActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void loadTasks() {
        taskContainer.removeAllViews();
        Cursor cursor = dbHelper.getTasks();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(0);
            String taskName = cursor.getString(1);
            boolean isCompleted = cursor.getInt(2) == 1;

            View taskView = getLayoutInflater().inflate(R.layout.task_item, null);
            TextView taskText = taskView.findViewById(R.id.task_text);
            CheckBox checkBox = taskView.findViewById(R.id.task_checkbox);

            taskText.setText(taskName);
            checkBox.setChecked(isCompleted);
            if (isCompleted) {
                taskText.setPaintFlags(taskText.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
            }

            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                dbHelper.updateTask(id, isChecked);
                if (isChecked) {
                    taskText.setPaintFlags(taskText.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    taskText.setPaintFlags(taskText.getPaintFlags() & ~android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                }
            });

            taskContainer.addView(taskView);
        }
        cursor.close();
    }
}
