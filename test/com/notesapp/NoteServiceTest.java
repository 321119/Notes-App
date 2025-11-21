package com.notesapp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class NoteServiceTest {

    @Test
    public void testCreateNote() {
        NoteService service = new NoteService();
        Note note = service.createNote("Hello World");

        assertNotNull(note);                      // note should not be null
        assertEquals("Hello World", note.getContent()); // content must match
        assertTrue(note.getId() > 0);             // ID must be positive
    }
}
