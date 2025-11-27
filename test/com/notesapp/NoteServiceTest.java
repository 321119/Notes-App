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

        assertNotNull(note);                      // note should not be null
        assertEquals("Hello World", note.getContent()); // content must match
        assertTrue(note.getId() > 0);             // ID must be positive
    }

@Test
public void testSaveNotesToFile() {
    NoteService service = new NoteService();

    // Arrange – create some notes
    service.createNote("First note");
    service.createNote("Second note");

    // Act – save them to a test file
    String filepath = "test_notes.txt";
    service.saveNotesToFile(filepath);

    // Assert – the file should exist
    File file = new File(filepath);
    assertTrue(file.exists(), "File should be created");

    // Optional: read content back out to verify
    try {
        String content = Files.readString(file.toPath());
        assertTrue(content.contains("First note"));
        assertTrue(content.contains("Second note"));
    } catch (IOException e) {
        fail("Could not read saved file");
    }

    // Clean up (avoid clutter)
    file.delete();
}

@Test
public void testCreateNoteWithSpecialCharacters() {
    NoteService service = new NoteService();

    String content = "Hello 😊🔥🎉 — café — 中文 — عربى — symbols: !@#$%^&*()";
    Note note = service.createNote(content);

    assertEquals(content, note.getContent());
}
}
