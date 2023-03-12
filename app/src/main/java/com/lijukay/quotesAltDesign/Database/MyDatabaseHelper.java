package com.lijukay.quotesAltDesign.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.lijukay.quotesAltDesign.R;

public class MyDatabaseHelper extends SQLiteOpenHelper {

    private final Context context;
    private static final String DATABASE_NAME = "OwnQwotable.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "my_qwotable";
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_QWOTABLE = "qwotable";
    private static final String COLUMN_AUTHOR = "qwotable_author";
    private static final String COLUMN_FOUNDIN = "qwotable_foundin";


    public MyDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_QWOTABLE + " TEXT, " +
                COLUMN_AUTHOR + " TEXT, " +
                COLUMN_FOUNDIN + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addQwotable(String qwotable, String author, String found_in){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_QWOTABLE, qwotable);
        cv.put(COLUMN_AUTHOR, author);
        cv.put(COLUMN_FOUNDIN, found_in);
        long result = db.insert(TABLE_NAME, null, cv);

        if (result == -1){
            //Failed
            Toast.makeText(context, R.string.fail, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.Success, Toast.LENGTH_SHORT).show();
        }
    }

    public Cursor readAllData(){
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null){
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public void updateData(String row_id, String qwotable, String author, String found_in){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_QWOTABLE, qwotable);
        values.put(COLUMN_AUTHOR, author);
        values.put(COLUMN_FOUNDIN, found_in);

        long result = db.update(TABLE_NAME, values, "_id=?", new String[]{row_id});

        if (result == -1){ //There is no data
            Toast.makeText(context, R.string.no_data, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.Success, Toast.LENGTH_SHORT).show();
        }
    }

    public void deleteOneRow(String row_id){
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(TABLE_NAME, "_id=?", new String[]{row_id});

        if (result == -1){
            Toast.makeText(context, R.string.fail, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, R.string.Success, Toast.LENGTH_SHORT).show();
        }

    }

    void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_NAME);
    }
}
