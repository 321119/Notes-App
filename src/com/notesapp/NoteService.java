package com.notesapp;

import java.util.HashMap;

public class NoteService {

    private int idCounter = 1;
    private HashMap<Integer, Note> notes = new HashMap<>();

    public Note createNote(String content) {
        Note note = new Note(idCounter++, content);
        notes.put(note.getId(), note);
        return note;
    }
}
