package com.example.sportclubolympus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sportclubolympus.data.ClubOlympusContract;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AddMemberActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText editTextFirstName;
    private EditText editTextLastName;
    private EditText editTextSport;
    private Spinner spinnerGender;

    private ArrayAdapter spinnerAdapter;

    private static final int EDIT_MEMBER_LOADER = 12;
    Uri currentMemberUri;
    MemberCursorAdapter memberCursorAdapter;

    private int gender = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member);

        editTextFirstName = findViewById(R.id.editTextFirstName);
        editTextLastName = findViewById(R.id.editTextLastName);
        editTextSport = findViewById(R.id.editTextSport);
        spinnerGender = findViewById(R.id.spinnerGender);

        currentMemberUri = getIntent().getData();

        if (currentMemberUri == null) {
            setTitle(R.string.add_member);
            invalidateOptionsMenu();
        } else {setTitle(R.string.edit_member);
        getSupportLoaderManager().initLoader(EDIT_MEMBER_LOADER, null, AddMemberActivity.this);}

        memberCursorAdapter = new MemberCursorAdapter(this, null, false);


        spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.array_gender, android.R.layout.simple_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerGender.setAdapter(spinnerAdapter);

        spinnerGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedGender = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selectedGender)) {
                    if (selectedGender.equals("Male")) {
                        gender = ClubOlympusContract.MemberEntry.GENDER_MALE;
                    } else if (selectedGender.equals("Female")) {
                        gender = ClubOlympusContract.MemberEntry.GENDER_FEMALE;
                    } else {
                        gender = ClubOlympusContract.MemberEntry.GENDER_UNKNOWN;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gender = 0;
            }
        });
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (currentMemberUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_member);
            menuItem.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.members_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_member:
                saveMember();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                return true;
            case R.id.delete_member:
                showDeleteMemberDialog();
                return true;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveMember() {
        String firstName = editTextFirstName.getText().toString().trim();
        String lastName = editTextLastName.getText().toString().trim();
        String sport = editTextSport.getText().toString().trim();

        if (TextUtils.isEmpty(firstName)) {
            Toast.makeText(this, "Input the first name", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(lastName)) {
            Toast.makeText(this, "Input the last name", Toast.LENGTH_SHORT).show();
            return;
        } else if (TextUtils.isEmpty(sport)) {
            Toast.makeText(this, "Input the sport", Toast.LENGTH_SHORT).show();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(ClubOlympusContract.MemberEntry.KEY_FIRST_NAME, firstName);
        contentValues.put(ClubOlympusContract.MemberEntry.KEY_LAST_NAME, lastName);
        contentValues.put(ClubOlympusContract.MemberEntry.KEY_SPORT, sport);
        contentValues.put(ClubOlympusContract.MemberEntry.KEY_GENDER, gender);

        if (currentMemberUri == null) {
            ContentResolver contentResolver = getContentResolver();
            Uri uri = contentResolver.insert(ClubOlympusContract.MemberEntry.CONTENT_URI, contentValues);

            if (uri == null) {
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsChanged = getContentResolver().update(currentMemberUri, contentValues, null, null);

            if (rowsChanged == 0) {
                Toast.makeText(this, "Saving data in the table failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Member updated", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String projection[] = {
                ClubOlympusContract.MemberEntry._ID,
                ClubOlympusContract.MemberEntry.KEY_FIRST_NAME,
                ClubOlympusContract.MemberEntry.KEY_LAST_NAME,
                ClubOlympusContract.MemberEntry.KEY_GENDER,
                ClubOlympusContract.MemberEntry.KEY_SPORT
        };

        CursorLoader cursorLoader = new CursorLoader(this, currentMemberUri,
                projection, null, null, null);

        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            int firstNameIndex = data.getColumnIndex(ClubOlympusContract.MemberEntry.KEY_FIRST_NAME);
            int lastNameIndex = data.getColumnIndex(ClubOlympusContract.MemberEntry.KEY_LAST_NAME);
            int genderIndex = data.getColumnIndex(ClubOlympusContract.MemberEntry.KEY_GENDER);
            int sportIndex = data.getColumnIndex(ClubOlympusContract.MemberEntry.KEY_SPORT);

            String firstName = data.getString(firstNameIndex);
            String lastName = data.getString(lastNameIndex);
            int gender = data.getInt(genderIndex);
            String sport = data.getString(sportIndex);

            editTextFirstName.setText(firstName);
            editTextLastName.setText(lastName);
            editTextSport.setText(sport);

            switch (gender) {
                case ClubOlympusContract.MemberEntry.GENDER_MALE:
                    spinnerGender.setSelection(1);
                    break;
                case ClubOlympusContract.MemberEntry.GENDER_FEMALE:
                    spinnerGender.setSelection(2);
                    break;
                default:
                    spinnerGender.setSelection(0);
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        memberCursorAdapter.swapCursor(null);
    }

    private void showDeleteMemberDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want delete the member?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteMember();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteMember() {
        if (currentMemberUri != null) {
            int rowsDeleted = getContentResolver().delete(currentMemberUri, null, null);

            if (rowsDeleted == 0) {
                Toast.makeText(this, "Deleting data from table failed", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Member is deleted", Toast.LENGTH_SHORT).show();
            }

            finish();
        }
    }
}
