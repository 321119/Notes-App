package com.notesapp;

public class Note {

    private int id;
    private String content;

    // NEW: optional attached image
    private String imagePath;  // null if no image

    public Note(int id, String content) {
        this.id = id;
        this.content = content;
        this.imagePath = null;
    }
    
    

    public int getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // NEW image getters/setters
    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    @Override
    public String toString() {
    return content;
    }
}
