package com.nnguye51.androidnotes;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class NotesViewHolder extends RecyclerView.ViewHolder {

    TextView title;
    TextView body;
    TextView time;

    NotesViewHolder(View view) {
        super(view);
        title = view.findViewById(R.id.viewTitle);
        body = view.findViewById(R.id.viewBody);
        time = view.findViewById(R.id.viewTime);
    }
}
