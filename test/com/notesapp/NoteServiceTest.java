package com.notesapp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class NoteServiceTest {

    @Test
    public void testCreateNote() {
        NoteService service = new NoteService();
        Note note = service.createNote("Hello World");

        assertNotNull(note);
        assertEquals("Hello World", note.getContent());
        assertTrue(note.getId() > 0);
    }

    @Test
    public void testSaveNotesToFile() {
        NoteService service = new NoteService();

        service.createNote("First note");
        service.createNote("Second note");

        String filepath = "test_notes.txt";
        service.saveNotesToFile(filepath);

        File file = new File(filepath);
        assertTrue(file.exists(), "File should be created");

        try {
            String content = Files.readString(file.toPath());
            assertTrue(content.contains("First note"));
            assertTrue(content.contains("Second note"));
        } catch (IOException e) {
            fail("Could not read saved file");
        }

        file.delete();
    }

    @Test
    public void testCreateNoteWithSpecialCharacters() {
        NoteService service = new NoteService();

        String content = "Hello 😊🔥🎉 — café — 中文 — عربى — symbols: !@#$%^&*()";
        Note note = service.createNote(content);

        assertEquals(content, note.getContent());
    }

    @Test
    public void testEditNote() {
        NoteService service = new NoteService();

        Note note = service.createNote("Original content");

        assertTrue(service.editNote(note.getId(), "Updated content"));

        Note updated = service.getNoteById(note.getId());
        assertEquals("Updated content", updated.getContent());
    }
}

