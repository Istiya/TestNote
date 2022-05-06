package com.example.testnote.adapters;


import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.testnote.R;
import com.example.testnote.entities.Note;
import com.example.testnote.listeners.NoteListener;

import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private List<Note> notesList;
    private NoteListener noteListener;

    public NoteAdapter(List<Note> notesList, NoteListener noteListener) {
        this.notesList = notesList;
        this.noteListener = noteListener;

    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new NoteViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.note_container,
                        parent,
                        false));
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setNote(notesList.get(position));
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noteListener.onNoteClicked(notesList.get(position), position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, date;
        private CardView cardView;

        NoteViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.textView_title);
            content = view.findViewById(R.id.textView_content);
            date = view.findViewById(R.id.textView_date);
            cardView = view.findViewById(R.id.card_view);
        }

        void setNote(Note note) {
            title.setText(note.getTitle());
            content.setText(note.getContent());
            date.setText(note.getDate());
        }
    }

}
