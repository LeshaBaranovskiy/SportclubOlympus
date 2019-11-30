package com.example.sportclubolympus;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.sportclubolympus.data.ClubOlympusContract;

public class MemberCursorAdapter extends CursorAdapter {

    public MemberCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.member_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView textViewFirstName = view.findViewById(R.id.textViewFirstNameValue);
        TextView textViewLastName = view.findViewById(R.id.textViewLastNameValue);
        TextView textViewGender = view.findViewById(R.id.textViewGenderValue);
        TextView textViewSport = view.findViewById(R.id.textViewSportValue);

        String firstName = cursor.getString(cursor.getColumnIndexOrThrow(ClubOlympusContract.MemberEntry.KEY_FIRST_NAME));
        String lastName = cursor.getString(cursor.getColumnIndexOrThrow(ClubOlympusContract.MemberEntry.KEY_LAST_NAME));
        int gender = cursor.getInt(cursor.getColumnIndexOrThrow(ClubOlympusContract.MemberEntry.KEY_GENDER));
        String sport = cursor.getString(cursor.getColumnIndexOrThrow(ClubOlympusContract.MemberEntry.KEY_SPORT));

        textViewFirstName.setText(firstName);
        textViewLastName.setText(lastName);
        if (gender == 1) {
            textViewGender.setText("Male");
        } else if (gender == 2) {
            textViewGender.setText("Female");
        } else {
            textViewGender.setText("Unknown");
        }
        textViewSport.setText(sport);
    }
}
