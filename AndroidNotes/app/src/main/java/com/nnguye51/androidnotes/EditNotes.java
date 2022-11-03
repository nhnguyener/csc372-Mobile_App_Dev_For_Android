package com.nnguye51.androidnotes;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditNotes extends AppCompatActivity {

    EditText editTitle;
    EditText editBody;
    TextView time;
    SimpleDateFormat tempTime = new SimpleDateFormat("E MMM dd, hh:mm a", Locale.US);

    private Notes currNote;
    public static final String tempNote = "Temp Note";
    public static final String tempUpdateNote = "Temp Update";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_notes);

        setTitle("Android Notes: Edit Note");

        editTitle = findViewById(R.id.editTitle);
        editBody = findViewById(R.id.editBody);
        time = findViewById(R.id.viewTime);

        Intent i = getIntent();

        if (i.hasExtra(MainActivity.updateNoteString)) {
            currNote = (Notes) i.getSerializableExtra(MainActivity.updateNoteString);
            editTitle.setText(currNote.getTitle());
            editBody.setText(currNote.getBody());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_edit_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if  (item.getItemId() == R.id.saveMenu) {
            if (currNote == null) {
                if (editTitle.getText().toString().length() == 0) {
                    Toast.makeText(editTitle.getContext(), "Title required to save note", Toast.LENGTH_SHORT).show();
                    finish();
                }

                Notes n = new Notes(editTitle.getText().toString(), editBody.getText().toString(), tempTime.format(new Date()));
                Intent i = new Intent();
                i.putExtra(tempNote, n);
                setResult(RESULT_OK, i);
                finish();

            } else {
                returnData();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Save Note?");

        alertBuilder.setPositiveButton("YES", (dialog, id) -> {
            if (currNote == null) {
                if (editTitle.getText().toString().length() == 0) {
                    Toast.makeText(editTitle.getContext(), "Title required to save note", Toast.LENGTH_SHORT).show();
                    finish();
                }

                Notes n = new Notes(editTitle.getText().toString(), editBody.getText().toString(), tempTime.format(new Date()));
                Intent i = new Intent();
                i.putExtra(tempNote, n);
                setResult(RESULT_OK, i);
                finish();

                //Toast.makeText(.getContext(), "Note Saved", Toast.LENGTH_SHORT).show();
            } else {
                returnData();
            }
        });

        alertBuilder.setNegativeButton("NO", (dialog, which) -> finish());

        AlertDialog alertSave = alertBuilder.create();
        alertSave.show();
    }

    public void returnData() {
        String title = editTitle.getText().toString();
        String body = editBody.getText().toString();

        if (title.length() == 0) {
            Toast.makeText(editTitle.getContext(), "Title required to save note", Toast.LENGTH_SHORT).show();
            finish();
        }

        if ( (currNote.getTitle().equals(title)) && (currNote.getBody().equals(body)) ) {
            finish();
        }

        currNote = new Notes(title, body, tempTime.format(new Date()));
        Intent i = new Intent();
        i.putExtra(tempUpdateNote, currNote);
        setResult(RESULT_OK, i);
        finish();
    }
}