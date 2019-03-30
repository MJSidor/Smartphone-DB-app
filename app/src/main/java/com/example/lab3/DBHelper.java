package com.example.lab3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
    public final static int DB_VERSION = 1;
    public final static String ID = "_id";
    public final static String DB_NAME = "smartphones.db";
    public final static String TABLE_NAME = "devices";
    public final static String COLUMN1 = "brand";
    public final static String COLUMN2 = "model";
    public final static String DB_CREATE = "CREATE TABLE " + TABLE_NAME + "(" + ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN1 + " TEXT NOT NULL," + COLUMN2 + " TEXT);";
    private static final String DB_DELETE = "DROP TABLE IF EXISTS " + TABLE_NAME;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //tworzenie bazy
        db.execSQL(DB_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //aktualizacja bazy do nowej wersji
        db.execSQL(DB_DELETE);
        onCreate(db);
    }
}

