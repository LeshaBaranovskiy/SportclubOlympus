package com.example.sportclubolympus.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class OlympusDbHelper extends SQLiteOpenHelper {

    public OlympusDbHelper(@Nullable Context context) {
        super(context, ClubOlympusContract.DATABASE_NAME, null, ClubOlympusContract.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_MEMBER_TABLE = "CREATE TABLE " + ClubOlympusContract.MemberEntry.TABLE_NAME + "(" +
                ClubOlympusContract.MemberEntry.KEY_ID + " INTEGER PRIMARY KEY," +
                ClubOlympusContract.MemberEntry.KEY_FIRST_NAME + " TEXT," +
                ClubOlympusContract.MemberEntry.KEY_LAST_NAME + " TEXT," +
                ClubOlympusContract.MemberEntry.KEY_GENDER + " INTEGER NOT NULL," +
                ClubOlympusContract.MemberEntry.KEY_SPORT + " TEXT)";
        db.execSQL(CREATE_MEMBER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + ClubOlympusContract.MemberEntry.TABLE_NAME);
    }
}
