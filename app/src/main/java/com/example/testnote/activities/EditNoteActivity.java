package com.example.testnote.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.testnote.R;
import com.example.testnote.database.NotesDatabase;
import com.example.testnote.entities.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;

public class EditNoteActivity extends AppCompatActivity {

    private EditText title, content;
    private Note currentNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        title = findViewById(R.id.title_note);
        content = findViewById(R.id.content_note);

        FloatingActionButton fab_save = findViewById(R.id.fab_save);
        fab_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveNote()){
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }
        });

        FloatingActionButton fab_delete_note = findViewById(R.id.fab_delete_note);
        fab_delete_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

        if(getIntent().getBooleanExtra("isOpenedNote", false)){
            currentNote = (Note) getIntent().getSerializableExtra("note");
            isOpenedNote();
        }

    }

    private void isOpenedNote() {
        title.setText(currentNote.getTitle());
        content.setText(currentNote.getContent());

    }

    private boolean deleteNote() {

        class DeleteNoteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                if(currentNote != null)
                    NotesDatabase.getNotesDatabase(getApplicationContext()).noteDao().deleteNote(currentNote);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid){
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

        }

        new DeleteNoteTask().execute();
        return true;
    }

    private boolean saveNote() {
        if(title.getText().toString().trim().isEmpty() || content.getText().toString().trim().isEmpty()){
            Toast.makeText(this, "Fields cant be empty", Toast.LENGTH_SHORT).show();
            return false;
        }

        final Note note = new Note();
        note.setTitle(title.getText().toString());
        note.setContent(content.getText().toString());
        note.setDate(Calendar.getInstance().getTime().toString());

        class SaveNoteTask extends AsyncTask<Void, Void, Void> {

            @Override
            protected Void doInBackground(Void... voids) {
                if(getIntent().getBooleanExtra("isOpenedNote", false)){
                    currentNote.setTitle(title.getText().toString());
                    currentNote.setContent(content.getText().toString());
                    NotesDatabase.getNotesDatabase(getApplicationContext()).noteDao().updateNote(currentNote);
                } else {
                    NotesDatabase.getNotesDatabase(getApplicationContext()).noteDao().insertNote(note);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid){
                super.onPostExecute(aVoid);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }

        }

        new SaveNoteTask().execute();
        return true;
    }

    @Override
    public void onBackPressed() {

    }

}