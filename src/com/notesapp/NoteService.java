package com.notesapp;

import java.util.HashMap;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

public class NoteService {

    private int idCounter = 1;
    private HashMap<Integer, Note> notes = new HashMap<>();

    // CREATE NOTE
    public Note createNote(String content) {
        Note note = new Note(idCounter++, content);
        notes.put(note.getId(), note);
        return note;
    }

    // EDIT NOTE
    public void editNote(int id, String newContent) {
        Note note = notes.get(id);
        if (note != null) {
            note.setContent(newContent);
        }
    }

    // GET NOTE
    public Note getNoteById(int id) {
        return notes.get(id);
    }

    // DELETE NOTE
    public boolean deleteNoteById(int id) {
        return notes.remove(id) != null;
    }

    // RETURN ALL NOTES
    public Iterable<Note> getAllNotes() {
        return notes.values();
    }

    // ----------------------------------------------
    //  SAVE TO CUSTOM FILE (Used by unit test)
    // ----------------------------------------------
    public void saveNotesToFile(String filepath) {
        try (PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(filepath), StandardCharsets.UTF_8))) {

            for (Note note : notes.values()) {
                String safe = note.getContent().replace("\n", "\\n");
                writer.println(note.getId() + "\t" + safe);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ----------------------------------------------
    // DEFAULT FILE SYSTEM
    // ----------------------------------------------
    private static final String DATA_FILENAME = "notes_data.txt";

    // SAVE TO DEFAULT FILE
    public void saveNotes() {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(DATA_FILENAME), StandardCharsets.UTF_8))) {

            for (Note note : notes.values()) {
                String safeContent = note.getContent().replace("\n", "\\n");
                writer.write(note.getId() + "\t" + safeContent);
                writer.newLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // LOAD FROM DEFAULT FILE
    public void loadNotes() {
        File file = new File(DATA_FILENAME);
        if (!file.exists()) return;

        notes.clear();
        int maxId = 0;

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("\t")) continue;

                String[] parts = line.split("\t", 2);
                int id = Integer.parseInt(parts[0]);
                String restored = parts[1].replace("\\n", "\n");

                Note note = new Note(id, restored);
                notes.put(id, note);

                if (id > maxId) maxId = id;
            }

            idCounter = maxId + 1;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
