package com.example.testnote.listeners;

import com.example.testnote.entities.Note;

public interface NoteListener {
    void onNoteClicked(Note note, int position);
}
