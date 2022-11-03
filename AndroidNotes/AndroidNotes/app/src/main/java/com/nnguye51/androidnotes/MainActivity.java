package com.nnguye51.androidnotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.JsonWriter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private RecyclerView rView;

    private Notes currNote;
    private List<Notes> notesList = new ArrayList<>();
    private NotesAdapter notesAdapter;

    /* requestCodes */
    private static final int newNote = 1;
    private static final int updateNote = 2;

    public static final String updateNoteString = "Update Note";

    /*private EditText notesTitle;
    private EditText notes;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;*/

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Android Notes (" + notesList.size() + ")");

        rView = findViewById(R.id.recycle);
        notesAdapter = new NotesAdapter(notesList, this);
        rView.setAdapter(notesAdapter);
        rView.setLayoutManager(new LinearLayoutManager(this));

        readJSON();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onPause() {
        super.onPause();
        writeJSON();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTitle("Android Notes (" + notesList.size() + ")");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.notes_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if  (item.getItemId() == R.id.aboutMenu) {
            Intent i = new Intent(this, AboutNotes.class);
            startActivityForResult(i, 0);
        } else if (item.getItemId() == R.id.addMenu) {
            Intent i = new Intent(this, EditNotes.class);
            startActivityForResult(i, newNote);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        int pos = rView.getChildLayoutPosition(v);
        currNote = notesList.get(pos);

        Intent i = new Intent(this, EditNotes.class);
        i.putExtra(updateNoteString, currNote);
        startActivityForResult(i, updateNote);
    }

    @Override
    public boolean onLongClick(View v) {
        int pos = rView.getChildLayoutPosition(v);
        currNote = notesList.get(pos);

        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Delete Note: '" + currNote.getTitle() + "'?");

        alertBuilder.setPositiveButton("YES", (dialog, id) -> {
           notesList.remove(currNote);
           notesAdapter.notifyDataSetChanged();

            setTitle("Android Notes (" + notesList.size() + ")");
            Toast.makeText(v.getContext(), "Note '" + currNote.getTitle() + "' Deleted", Toast.LENGTH_SHORT).show();
        });

        alertBuilder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                return;
            }
        });

        AlertDialog alertDialog = alertBuilder.create();
        alertDialog.show();

        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == newNote) {
            if (resultCode == RESULT_OK) {
                Notes newNote = (Notes) data.getSerializableExtra(EditNotes.tempNote);

                if (newNote == null)
                    return;

                notesList.add(0, newNote);

                setTitle("Android Notes (" + notesList.size() + ")");

                notesAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == updateNote) {
            if (resultCode == RESULT_OK) {
                Notes newNoteFinal = (Notes) data.getSerializableExtra(EditNotes.tempUpdateNote);

                notesList.remove(currNote);
                currNote = newNoteFinal;
                notesList.add(0, newNoteFinal);

                notesAdapter.notifyDataSetChanged();
            }
        }
        return;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void writeJSON() {
        try {
            FileOutputStream outStream = getApplicationContext().openFileOutput("Notes.json", Context.MODE_PRIVATE);
            JsonWriter jwriter = new JsonWriter(new OutputStreamWriter(outStream, StandardCharsets.UTF_8));

            jwriter.setIndent("  ");
            jwriter.beginArray();

            for (Notes n : notesList) {
                jwriter.beginObject();
                jwriter.name("TITLE").value(n.getTitle());
                jwriter.name("BODY").value(n.getBody());
                jwriter.name("TIME").value(n.getTime());
                jwriter.endObject();
            }

            jwriter.endArray();
            jwriter.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void readJSON() {
        try {
            InputStream inStream = getApplicationContext().openFileInput("Notes.json");
            BufferedReader jreader = new BufferedReader(new InputStreamReader(inStream, StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String currLine;

            while ( (currLine = jreader.readLine()) != null) {
                builder.append(currLine);
            }

            JSONArray jArray = new JSONArray(builder.toString());

            for (int i = 0; i < jArray.length(); i++) {
                JSONObject jObject = jArray.getJSONObject(i);
                String title = jObject.getString("TITLE");
                String body = jObject.getString("BODY");
                String time = jObject.getString("TIME");

                Notes n = new Notes(title, body, time);
                notesList.add(n);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}