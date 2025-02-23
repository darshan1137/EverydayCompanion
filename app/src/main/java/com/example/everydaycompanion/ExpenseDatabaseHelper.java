package com.example.everydaycompanion;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ExpenseDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Expenses.db";
    private static final String TABLE_NAME = "expenses";
    private static final String COL_ID = "id";
    private static final String COL_AMOUNT = "amount";
    private static final String COL_DESCRIPTION = "description";
    private static final String COL_DATE = "date";

    public ExpenseDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 2); // Increment version to apply changes
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_AMOUNT + " REAL, " +
                COL_DESCRIPTION + " TEXT, " +
                COL_DATE + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertExpense(double amount, String description, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_AMOUNT, amount);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_DATE, date);

        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public Cursor getAllExpenses() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + COL_DATE + " DESC", null);
    }

    public boolean deleteExpense(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedRows = db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
        return deletedRows > 0;
    }

    public boolean updateExpense(int id, double amount, String description, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_AMOUNT, amount);
        values.put(COL_DESCRIPTION, description);
        values.put(COL_DATE, date);

        int updatedRows = db.update(TABLE_NAME, values, COL_ID + "=?", new String[]{String.valueOf(id)});
        return updatedRows > 0;
    }

    public double getBalance() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT SUM(amount) FROM " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            return cursor.getDouble(0);
        }
        cursor.close();
        return 0.0;
    }
}
