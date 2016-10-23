package com.app.andrew.moviesviewer.DataBase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.app.andrew.moviesviewer.DataBase.DataBaseContract.MovieTable;
import com.app.andrew.moviesviewer.DataBase.DataBaseContract.ReviewTable;
import com.app.andrew.moviesviewer.DataBase.DataBaseContract.TrailerTable;

/**
 * Created by andrew on 10/19/16.
 */

public class DataBaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "movieapp.db";
    private static final int VERSION = 1;

    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE = "CREATE TABLE " + MovieTable.TABLE_NAME + " ("
                + MovieTable._ID + " INTEGER PRIMARY KEY, "
                + MovieTable.COLUMN_TITLE + " TEXT NOT NULL, "
                + MovieTable.COLUMN_IMAGE_URL + " STRING NOT NULL, "
                + MovieTable.COLUMN_RATING + " REAL NOT NULL, "
                + MovieTable.COLUMN_DATE + " TEXT NOT NULL, "
                + MovieTable.COLUMN_OVERVIEW + " TEXT NOT NULL ,"
                + MovieTable.COLUMN_IMAGE + " BLOB NOT NULL "
                + " )";

        final String SQL_CREATE_REVIEW = "CREATE TABLE " + ReviewTable.TABLE_NAME + " ("
                + ReviewTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + ReviewTable.COLUMN_AUTHOR + " TEXT NOT NULL, "
                + ReviewTable.COLUMN_COMMENT + " TEXT NOT NULL, "
                + ReviewTable.COLUMN_REFERENCE + " INTEGER NOT NULL, "
                + " FOREIGN KEY (" + ReviewTable.COLUMN_REFERENCE + ") REFERENCES "
                + MovieTable.TABLE_NAME + " (" + MovieTable._ID + " ) " + " ON DELETE CASCADE "
                + " )";
        final String SQL_CREATE_TRAILER = "CREATE TABLE " + TrailerTable.TABLE_NAME + " ("
                + TrailerTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TrailerTable.COLUMN_URL + " TEXT NOT NULL ,"
                + TrailerTable.COLUMN_REFERENCE + " INTEGER NOT NULL ,"
                + " FOREIGN KEY (" + TrailerTable.COLUMN_REFERENCE + ") REFERENCES "
                + MovieTable.TABLE_NAME + " (" + MovieTable._ID + ") " + " ON DELETE CASCADE "
                + " )";

        db.execSQL(SQL_CREATE_MOVIE);
        db.execSQL(SQL_CREATE_TRAILER);
        db.execSQL(SQL_CREATE_REVIEW);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ReviewTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailerTable.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + MovieTable.TABLE_NAME);
        onCreate(db);
    }
}
