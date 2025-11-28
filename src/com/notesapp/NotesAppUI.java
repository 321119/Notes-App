package com.notesapp;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 * NotesAppUI with image upload + image preview.
 */
public class NotesAppUI extends Application {

    private final NoteService service = new NoteService();
    private final ListView<Note> notesList = new ListView<>();
    private final TextArea noteArea = new TextArea();

    // NEW: Image display area
    private final ImageView imagePreview = new ImageView();

    @Override
    public void start(Stage primaryStage) {

        // sidebar
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));

        Button emojiBtn = new Button("😀");
        emojiBtn.setMaxWidth(Double.MAX_VALUE);

        sidebar.getChildren().addAll(new Label("Notes"), notesList, emojiBtn);
        sidebar.setPrefWidth(220);

        // editor + image viewer
        noteArea.setPromptText("Write your note here...");
        noteArea.setWrapText(true);

        Button createBtn = new Button("Create");
        Button editBtn = new Button("Edit");
        Button deleteBtn = new Button("Delete");
        Button uploadImageBtn = new Button("Upload Image");

        HBox buttonRow = new HBox(10, createBtn, editBtn, deleteBtn, uploadImageBtn);
        buttonRow.setAlignment(Pos.CENTER);

        // IMAGE PREVIEW SETTINGS
        imagePreview.setPreserveRatio(true);
        imagePreview.setFitWidth(350);
        imagePreview.setFitHeight(250);

        VBox mainArea = new VBox(10, noteArea, imagePreview, buttonRow);
        mainArea.setPadding(new Insets(10));
        VBox.setVgrow(noteArea, Priority.ALWAYS);

        HBox root = new HBox(sidebar, mainArea);
        HBox.setHgrow(mainArea, Priority.ALWAYS);

        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setTitle("Notes App");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Load notes
        service.loadNotes();
        refreshList();

        // Selecting a note loads content AND image
        notesList.getSelectionModel().selectedItemProperty().addListener((obs, oldNote, newNote) -> {
            if (newNote != null) {
                noteArea.setText(newNote.getContent());
                loadImagePreview(newNote.getImagePath());
            } else {
                noteArea.clear();
                imagePreview.setImage(null);
            }
        });

        // CREATE
        createBtn.setOnAction(e -> {
            String content = noteArea.getText().trim();
            if (!content.isEmpty()) {
                service.createNote(content);
                noteArea.clear();
                imagePreview.setImage(null);
                refreshList();
            }
        });

        // EDIT
        editBtn.setOnAction(e -> {
            Note selected = notesList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                service.editNote(selected.getId(), noteArea.getText());
                refreshList();
                notesList.getSelectionModel().select(selected);
            } else {
                showAlert("No note selected", "Please select a note to edit.");
            }
        });

        // DELETE
        deleteBtn.setOnAction(e -> {
            Note selected = notesList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                service.deleteNoteById(selected.getId());
                noteArea.clear();
                imagePreview.setImage(null);
                refreshList();
            }
        });

        // IMAGE UPLOAD
        uploadImageBtn.setOnAction(e -> uploadImage());

        // Emoji picker
        emojiBtn.setOnAction(e -> showEmojiPicker());

        // CAMERA ICON for notes with images
        notesList.setCellFactory(list -> new ListCell<Note>() {
            @Override
            protected void updateItem(Note note, boolean empty) {
                super.updateItem(note, empty);
                if (empty || note == null) {
                    setText(null);
                } else {
                    String icon = (note.getImagePath() != null && !note.getImagePath().isEmpty())
                            ? "📷 "
                            : "";
                    setText(icon + note.getContent());
                }
            }
        });
    }

    // -----------------------------
    // IMAGE PREVIEW LOGIC
    // -----------------------------
    private void loadImagePreview(String path) {
        if (path == null || path.isEmpty()) {
            imagePreview.setImage(null);
            return;
        }
        File f = new File(path);
        if (!f.exists()) {
            imagePreview.setImage(null);
            return;
        }
        imagePreview.setImage(new Image(f.toURI().toString()));
    }

    // -----------------------------
    // UPLOAD IMAGE
    // -----------------------------
    private void uploadImage() {
        Note selected = notesList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No note selected", "Please select a note first.");
            return;
        }

        FileChooser chooser = new FileChooser();
        chooser.setTitle("Choose an Image");
        chooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );

        File file = chooser.showOpenDialog(null);
        if (file != null) {
            selected.setImagePath(file.getAbsolutePath());
            service.saveNotes();
            loadImagePreview(file.getAbsolutePath());
            refreshList();
        }
    }

    private void refreshList() {
        java.util.List<Note> list = new java.util.ArrayList<>();
        service.getAllNotes().forEach(list::add);
        notesList.getItems().setAll(list);
    }

    private void showAlert(String title, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    // Emoji picker popup
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

        popup.setScene(new Scene(grid));
        popup.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

