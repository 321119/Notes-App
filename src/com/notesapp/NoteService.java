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

    public void editNote(int id, String newContent) {
        Note note = notes.get(id);
        if (note != null) {
            note.setContent(newContent);
        }
    }

    public Note getNoteById(int id) {
        return notes.get(id);
    }

    public boolean deleteNoteById(int id) {
        return notes.remove(id) != null;
    }

    public Iterable<Note> getAllNotes() {
        return notes.values();
    }
}

