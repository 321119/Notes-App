// Delete feature implemented (PR marker)


package com.notesapp;

import java.util.HashMap;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class NoteService {

    private int idCounter = 1;
    private HashMap<Integer, Note> notes = new HashMap<>();

    public Note createNote(String content) {
        Note note = new Note(idCounter++, content);
        notes.put(note.getId(), note);
        return note;
    }

    public boolean editNote(int id, String newContent) {
        Note note = notes.get(id);
        if (note != null) {
            note.setContent(newContent);
            return true;
        }
        return false;
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

    public void saveNotesToFile(String filepath) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
        	for (Note note : getAllNotes()) {
        	    writer.println(note.getContent());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
