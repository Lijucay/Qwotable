package com.lijukay.quotesAltDesign.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.lijukay.quotesAltDesign.R;

public class FavoriteDatabaseHelper extends SQLiteOpenHelper {
    private final Context context;
    private static final String DATABASE_NAME = "FavoriteQwotable.db";
    private static final int DATABASE_VERSION = 2;

    private static final String TABLE_NAME = "favorite_qwotable";
    private static final String COLUMN_QWOTABLE = "qwotable";
    private static final String COLUMN_AUTHOR = "qwotable_author";
    private static final String COLUMN_FOUND_IN = "qwotable_foundin";


    public FavoriteDatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " (" + COLUMN_QWOTABLE + " TEXT PRIMARY KEY, " +
                COLUMN_AUTHOR + " TEXT, " +
                COLUMN_FOUND_IN + " TEXT);";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addQwotable(String qwotable, String author, String found_in) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_QWOTABLE, qwotable);
        cv.put(COLUMN_AUTHOR, author);
        cv.put(COLUMN_FOUND_IN, found_in);
        long result = db.insert(TABLE_NAME, null, cv);


        View layout = LayoutInflater.from(context).inflate(R.layout.toast_view, null);

        TextView message = layout.findViewById(R.id.message);

        if (result == -1) {
            message.setText(R.string.favorite_fail_add);
            assert context != null;
            Toast toast = new Toast(context.getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, 100);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } else {
            message.setText(R.string.favorite_successful_add);
            assert context != null;
            Toast toast = new Toast(context.getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, 100);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
    }

    public Cursor readAllData() {
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;
    }

    public void deleteOneRow(String quote) {
        SQLiteDatabase db = this.getWritableDatabase();

        long result = db.delete(TABLE_NAME, "qwotable=?", new String[]{quote});

        View layout = LayoutInflater.from(context).inflate(R.layout.toast_view, null);

        TextView message = layout.findViewById(R.id.message);

        if (result == -1) {
            message.setText(R.string.favorite_fail_delete);
            assert context != null;
            Toast toast = new Toast(context.getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, 100);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        } else {
            message.setText(R.string.favorite_successful_delete);
            assert context != null;
            Toast toast = new Toast(context.getApplicationContext());
            toast.setGravity(Gravity.BOTTOM, 0, 100);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }
    }

    public boolean isInDB(String qwotable) {
        SQLiteDatabase fdb = this.getReadableDatabase();

        String[] columns = new String[]{
                COLUMN_QWOTABLE,
                COLUMN_AUTHOR,
                COLUMN_FOUND_IN
        };

        try (Cursor cursor = fdb.query(TABLE_NAME, columns, COLUMN_QWOTABLE + " =?", new String[]{qwotable}, null, null, null)) {
            return cursor.getCount() != 0;
        }
    }
}
