package com.example.testnote.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Toolbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.testnote.R;
import com.example.testnote.adapters.NoteAdapter;
import com.example.testnote.database.NotesDatabase;
import com.example.testnote.entities.Note;
import com.example.testnote.listeners.NoteListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NoteListener {

    private RecyclerView recyclerView;
    private List<Note> noteList;
    private NoteAdapter noteAdapter;
    private int noteClickedPosition = -1;
    SharedPreferences prefs = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab_add = findViewById(R.id.fab_add);
        fab_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), EditNoteActivity.class));
            }
        });

        FloatingActionButton fab_delete = findViewById(R.id.fab_delete);
        fab_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(noteList.size() != 0)
                    deleteNotes();
            }
        });

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        );

        noteList = new ArrayList<Note>();
        noteAdapter = new NoteAdapter(noteList, this);
        recyclerView.setAdapter(noteAdapter);

        prefs = getSharedPreferences("com.example.testnote.activitie", MODE_PRIVATE);

        getNotes();
    }

    private void deleteNotes() {

        class DeleteNotes extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                    NotesDatabase.getNotesDatabase(getApplicationContext()).noteDao().deleteAll();
                return null;
            }

            @Override
            protected void onPostExecute(Void unused) {
                super.onPostExecute(unused);
                noteAdapter.notifyItemRangeRemoved(0, noteList.size());
                noteList.clear();
                recyclerView.smoothScrollToPosition(0);
            }
        }

        new DeleteNotes().execute();
    }

    private void getNotes() {
        class GetNotesTask extends AsyncTask<Void, Void, List<Note>> {

            @Override
            protected List<Note> doInBackground(Void... voids) {
                if (prefs.getBoolean("first_launch", true)) {
                    prefs.edit().putBoolean("first_launch", false).commit();
                    Note note = new Note();
                    note.setTitle("Standard title");
                    note.setContent("Standard content");
                    note.setDate(Calendar.getInstance().getTime().toString());
                    NotesDatabase.getNotesDatabase(getApplicationContext()).noteDao().insertNote(note);
                }
                return NotesDatabase.getNotesDatabase(getApplicationContext()).noteDao().getAllNotes();
            }

            @Override
            protected void onPostExecute(List<Note> notes) {
                super.onPostExecute(notes);
                if(noteList.size() == 0) {
                    noteList.addAll(notes);
                    noteAdapter.notifyDataSetChanged();
                } else {
                    noteList.add(0, notes.get(0));
                    noteAdapter.notifyItemInserted(0);
                }
                recyclerView.smoothScrollToPosition(0);
            }
        }
        new GetNotesTask().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1 && resultCode == RESULT_OK) {
            getNotes();
        }

    }

    @Override
    public void onNoteClicked(Note note, int position) {
        noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), EditNoteActivity.class);
        intent.putExtra("isOpenedNote", true);
        intent.putExtra("note", note);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

    }

}
