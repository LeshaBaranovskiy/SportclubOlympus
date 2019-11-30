package com.example.sportclubolympus.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.sportclubolympus.MemberCursorAdapter;
import com.example.sportclubolympus.data.ClubOlympusContract.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import java.lang.reflect.Member;

public class OlympusContentProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int MEMBERS = 1;
    private static final int MEMBER_ID = 2;

    static {
        sUriMatcher.addURI(ClubOlympusContract.AUTHORITY, ClubOlympusContract.PATH_MEMBERS, MEMBERS);

        sUriMatcher.addURI(ClubOlympusContract.AUTHORITY, ClubOlympusContract.PATH_MEMBERS + "/#", MEMBER_ID);
    }

    private OlympusDbHelper olympusDbHelper;

    @Override
    public boolean onCreate() {
        olympusDbHelper = new OlympusDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase db = olympusDbHelper.getReadableDatabase();
        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match){
            case MEMBERS:
                cursor = db.query(MemberEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MEMBER_ID:
                selection = MemberEntry._ID + "=?";
                selectionArgs =  new String[] {String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(MemberEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                Log.e("insertMethod", "Something happened " + uri);
                Toast.makeText(getContext(), "Incorrect URI", Toast.LENGTH_SHORT).show();
                throw new IllegalArgumentException("Can't query incorrect URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {

        validation(values);

        SQLiteDatabase db = olympusDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        if (match == MEMBERS) {
            long id = db.insert(MemberEntry.TABLE_NAME, null, values);
            if (id == -1) {
                Log.e("insertMethod", "Something happened " + uri);
                return null;
            }

            getContext().getContentResolver().notifyChange(uri, null);
            return ContentUris.withAppendedId(uri, id);
        }
        throw new IllegalArgumentException("Can't insert incorrect URI " + uri);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase db = olympusDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match){
            case MEMBERS:
                rowsDeleted = db.delete(MemberEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case MEMBER_ID:
                selection = MemberEntry._ID + "=?";
                selectionArgs =  new String[] {String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted =  db.delete(MemberEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Can't delete incorrect URI " + uri);
        }

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {

        validation(values);

        SQLiteDatabase db = olympusDbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);
        int rowsUpdated = 0;
        switch (match){
            case MEMBERS:
                rowsUpdated = db.update(MemberEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MEMBER_ID:
                selection = MemberEntry._ID + "=?";
                selectionArgs =  new String[] {String.valueOf(ContentUris.parseId(uri))};

                rowsUpdated = db.update(MemberEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Can't update incorrect URI " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match){
            case MEMBERS:
                return MemberEntry.CONTENT_MULTIPLE_ITEMS;
            case MEMBER_ID:
                return MemberEntry.CONTENT_SINGLE_ITEM;
            default:
                throw new IllegalArgumentException("Unknown uri " + uri);
        }
    }

    private void validation(@Nullable ContentValues values) {
        String firstName = values.containsKey(MemberEntry.KEY_FIRST_NAME) ? values.getAsString(MemberEntry.KEY_FIRST_NAME) : null;
        String lastName = values.containsKey(MemberEntry.KEY_LAST_NAME) ? values.getAsString(MemberEntry.KEY_LAST_NAME) : null;
        Integer gender = values.containsKey(MemberEntry.KEY_GENDER) ? values.getAsInteger(MemberEntry.KEY_GENDER) : null;
        String sport = values.containsKey(MemberEntry.KEY_SPORT) ? values.getAsString(MemberEntry.KEY_SPORT) : null;

        if (firstName == null || lastName == null ||
                gender == null  || sport == null) {
            Toast.makeText(getContext(), "You have to fill all fields", Toast.LENGTH_SHORT).show();
            throw new IllegalArgumentException("You have to fill all fields");
        }
    }
}
