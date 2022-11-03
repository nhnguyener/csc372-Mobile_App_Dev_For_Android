package com.nnguye51.androidnotes;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesViewHolder> {

    private final List<Notes> notesList;
    private final MainActivity mainAct;

    NotesAdapter(List<Notes> nList, MainActivity ma) {
        this.notesList = nList;
        this.mainAct = ma;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.notes_list_row, parent, false);
        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new NotesViewHolder(itemView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int pos) {
        Notes note = notesList.get(pos);
        String noteBody = note.getBody();

        if (noteBody.length() > 80) {
            noteBody = noteBody.substring(0, 80) + "...";
        }

        holder.title.setText(note.getTitle());
        holder.body.setText(noteBody);
        holder.time.setText(note.getTime());
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }
}
