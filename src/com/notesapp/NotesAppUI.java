package com.notesapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

/**
 * NotesAppUI - corrected to use existing NoteService class.
 * Drop this into src/com/notesapp/NotesAppUI.java (replace), save, then Run As → Java Application.
 */
public class NotesAppUI extends Application {

    private final NoteService service = new NoteService(); // <-- uses your existing NoteService
    private final ListView<Note> notesList = new ListView<>();
    private final TextArea noteArea = new TextArea();

    @Override
    public void start(Stage primaryStage) {

        // Sidebar (notes list + emoji button)
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));

        Button emojiBtn = new Button("😀");  // opens emoji picker
        emojiBtn.setMaxWidth(Double.MAX_VALUE);

        sidebar.getChildren().addAll(new Label("Notes"), notesList, emojiBtn);
        sidebar.setPrefWidth(220);

        // Note editor
        noteArea.setPromptText("Write your note here...");
        noteArea.setWrapText(true);

        // Buttons for CRUD operations
        Button createBtn = new Button("Create Note");
        Button editBtn = new Button("Edit Note");
        Button deleteBtn = new Button("Delete Note");

        HBox buttonRow = new HBox(10, createBtn, editBtn, deleteBtn);
        buttonRow.setAlignment(Pos.CENTER);

        VBox mainArea = new VBox(10, noteArea, buttonRow);
        mainArea.setPadding(new Insets(10));
        VBox.setVgrow(noteArea, Priority.ALWAYS);

        // Layout containers
        HBox root = new HBox(sidebar, mainArea);
        HBox.setHgrow(mainArea, Priority.ALWAYS);

        Scene scene = new Scene(root, 900, 550);
        primaryStage.setTitle("Notes App");
        primaryStage.setScene(scene);
        primaryStage.show();

        // -------- Load saved notes and refresh UI --------
        service.loadNotes(); // if you have this method; if not, safe to ignore or implement
        refreshList();

        // -------- Ensure selection loads content (fixes Edit button) --------
        notesList.getSelectionModel().selectedItemProperty().addListener((obs, oldNote, newNote) -> {
            if (newNote != null) {
                noteArea.setText(newNote.getContent());
            } else {
                noteArea.clear();
            }
        });

        // -------- Button logic --------
        createBtn.setOnAction(e -> {
            String content = noteArea.getText().trim();
            if (!content.isEmpty()) {
                service.createNote(content);
                noteArea.clear();
                refreshList();
            }
        });

        editBtn.setOnAction(e -> {
            Note selected = notesList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String updated = noteArea.getText();
                service.editNote(selected.getId(), updated);
                refreshList();
                // keep selection on edited note
                notesList.getItems().stream()
                        .filter(n -> n.getId() == selected.getId())
                        .findFirst()
                        .ifPresent(n -> notesList.getSelectionModel().select(n));
            } else {
                showAlert("No note selected", "Please select a note to edit.");
            }
        });

        deleteBtn.setOnAction(e -> {
            Note selected = notesList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                service.deleteNoteById(selected.getId());
                refreshList();
                noteArea.clear();
            } else {
                showAlert("No note selected", "Please select a note to delete.");
            }
        });

        // Emoji picker
        emojiBtn.setOnAction(e -> showEmojiPicker());

    }

    private void refreshList() {
        // convert Iterable<Note> -> List<Note> then setAll
        java.util.List<Note> list = new java.util.ArrayList<>();
        service.getAllNotes().forEach(list::add);
        notesList.getItems().setAll(list);
    }

    private void showAlert(String title, String message) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(message);
        a.showAndWait();
    }

    // Simple emoji picker as a popup Stage
    private void showEmojiPicker() {
        Stage popup = new Stage();
        popup.setTitle("Emoji Picker");

        String[] emojis = {
                "😀","😁","😂","🤣","🙂","😊","😍","😎","🤔","😢",
                "😭","😡","👍","👎","🙏","🔥","✨","❤","💀","🎉"
        };

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10));
        grid.setHgap(8);
        grid.setVgap(8);

        int col = 0, row = 0;
        for (String emoji : emojis) {
            Button b = new Button(emoji);
            b.setPrefSize(40, 40);
            b.setOnAction(e -> {
                noteArea.appendText(emoji);
                popup.close();
            });
            grid.add(b, col, row);
            col++;
            if (col == 5) {
                col = 0;
                row++;
            }
        }

        Scene s = new Scene(grid);
        popup.setScene(s);
        popup.initOwner(notesList.getScene().getWindow());
        popup.show();
    }

    private void showAlertAndWait(String title, String msg) {
        showAlert(title, msg);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
