package com.notesapp;

public class Note {
    private int id;
    private String content;

    public Note(int id, String content) {
        this.id = id;
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }
}
