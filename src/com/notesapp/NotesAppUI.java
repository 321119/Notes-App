package com.notesapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * NotesAppUI - JavaFX GUI wired to NoteService.
 * Copy this file over your existing NotesAppUI.java (replace), save, then run.
 */
public class NotesAppUI extends Application {

    // ---- backing service + UI controls ----
    private final NoteService noteService = new NoteService();
    private final ListView<Note> noteList = new ListView<>();
    private final TextArea noteContent = new TextArea();

    @Override
    public void start(Stage stage) {
        stage.setTitle("Notes App");

        // list selection -> show content
        noteList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                noteContent.setText(newVal.getContent());
            } else {
                noteContent.clear();
            }
        });

        // Buttons
        Button createBtn = new Button("Create Note");
        Button editBtn   = new Button("Edit Note");
        Button deleteBtn = new Button("Delete Note");

        createBtn.setOnAction(e -> createNote());
        editBtn.setOnAction(e -> editSelectedNote());
        deleteBtn.setOnAction(e -> deleteSelectedNote());

        HBox buttonRow = new HBox(10, createBtn, editBtn, deleteBtn);
        buttonRow.setPadding(new Insets(10));

        // layout: left list, right editor+buttons
        VBox rightSide = new VBox(10, noteContent, buttonRow);
        rightSide.setPadding(new Insets(10));
        rightSide.setVgrow(noteContent, Priority.ALWAYS);

        SplitPane split = new SplitPane(noteList, rightSide);
        split.setDividerPositions(0.3);

        Scene scene = new Scene(split, 800, 500);
        stage.setScene(scene);
        stage.show();

        // initial refresh so list is populated
        refreshList();
    }

    private void createNote() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Note");
        dialog.setHeaderText("Enter note content:");
        dialog.setContentText("Content:");

        dialog.showAndWait().ifPresent(content -> {
            Note note = noteService.createNote(content);
            refreshList();
            // select new note in the list (lookup by id)
            noteList.getItems().stream()
                    .filter(n -> n.getId() == note.getId())
                    .findFirst()
                    .ifPresent(n -> noteList.getSelectionModel().select(n));
        });
    }

    private void editSelectedNote() {
        Note selected = noteList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No note selected", "Please select a note to edit.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(selected.getContent());
        dialog.setTitle("Edit Note");
        dialog.setHeaderText("Update note content:");
        dialog.setContentText("Content:");

        dialog.showAndWait().ifPresent(content -> {
            noteService.editNote(selected.getId(), content);
            refreshList();
            // keep selection on edited note
            noteList.getItems().stream()
                    .filter(n -> n.getId() == selected.getId())
                    .findFirst()
                    .ifPresent(n -> noteList.getSelectionModel().select(n));
        });
    }

    private void deleteSelectedNote() {
        Note selected = noteList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No note selected", "Please select a note to delete.");
            return;
        }

        // confirmation
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Delete Note");
        confirm.setHeaderText("Are you sure you want to delete this note?");
        confirm.setContentText(selected.getContent());
        confirm.showAndWait().ifPresent(button -> {
            if (button == ButtonType.OK) {
                noteService.deleteNoteById(selected.getId());
                refreshList();
                noteContent.clear();
            }
        });
    }

    // converts Iterable<Note> -> List<Note> then sets items
    private void refreshList() {
        List<Note> list = new ArrayList<>();
        noteService.getAllNotes().forEach(list::add);
        noteList.getItems().setAll(list);
    }

    private void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
