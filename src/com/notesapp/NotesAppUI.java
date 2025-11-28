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

import org.fxmisc.richtext.StyleClassedTextArea;

import java.io.File;
import java.util.*;

public class NotesAppUI extends Application {

    private final NoteService service = new NoteService();
    private final ListView<Note> notesList = new ListView<>();
    private final StyleClassedTextArea editor = new StyleClassedTextArea();

    // 🔥 Image support (easy mode)
    private final ImageView imagePreview = new ImageView();
    private File lastImageFile = null;

    @Override
    public void start(Stage primaryStage) {

        // ==== Sidebar ====
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));

        Button emojiBtn = new Button("😀");
        emojiBtn.setMaxWidth(Double.MAX_VALUE);

        sidebar.getChildren().addAll(new Label("Notes"), notesList, emojiBtn);
        sidebar.setPrefWidth(220);

        // ==== Rich Text Editor ====
        editor.setWrapText(true);
        editor.setPadding(new Insets(10));

        // ==== Image Preview ====
        imagePreview.setPreserveRatio(true);
        imagePreview.setFitWidth(350);

        VBox imageBox = new VBox(5, new Label("Attached Image:"), imagePreview);
        imageBox.setPadding(new Insets(10));

        // ==== Formatting Toolbar ====
        Button boldBtn = new Button("B");
        boldBtn.setStyle("-fx-font-weight: bold;");

        Button italicBtn = new Button("I");
        italicBtn.setStyle("-fx-font-style: italic;");

        Button underlineBtn = new Button("U");
        underlineBtn.setStyle("-fx-underline: true;");

        Button uploadImgBtn = new Button("Upload Image");

        HBox toolbar = new HBox(10, boldBtn, italicBtn, underlineBtn, uploadImgBtn);
        toolbar.setAlignment(Pos.CENTER_LEFT);

        // ==== CRUD buttons ====
        Button createBtn = new Button("Create Note");
        Button editBtn = new Button("Save Edits");
        Button deleteBtn = new Button("Delete Note");

        HBox crud = new HBox(10, createBtn, editBtn, deleteBtn);
        crud.setAlignment(Pos.CENTER);

        VBox main = new VBox(10, toolbar, editor, imageBox, crud);
        main.setPadding(new Insets(10));
        VBox.setVgrow(editor, Priority.ALWAYS);

        // ==== Layout ====
        HBox root = new HBox(sidebar, main);
        HBox.setHgrow(main, Priority.ALWAYS);

        Scene scene = new Scene(root, 1000, 700);
        scene.getStylesheets().add(getClass().getResource("notes.css").toExternalForm());
        primaryStage.setTitle("Notes App");
        primaryStage.setScene(scene);
        primaryStage.show();

        // ==== Load existing notes ====
        service.loadNotes();
        refreshList();

        // ==== When selecting a note ====
        notesList.getSelectionModel().selectedItemProperty().addListener((obs, oldN, newN) -> {
            if (newN != null) {
                editor.replaceText(newN.getContent());

                // Load image if saved
                if (newN.getImagePath() != null && !newN.getImagePath().isEmpty()) {
                    File f = new File(newN.getImagePath());
                    if (f.exists()) {
                        Image img = new Image(f.toURI().toString());
                        imagePreview.setImage(img);
                        lastImageFile = f;
                    } else {
                        imagePreview.setImage(null);
                        lastImageFile = null;
                    }
                } else {
                    imagePreview.setImage(null);
                    lastImageFile = null;
                }
            }
        });

        // ==== Formatting ====
        boldBtn.setOnAction(e -> toggleStyle("bold"));
        italicBtn.setOnAction(e -> toggleStyle("italic"));
        underlineBtn.setOnAction(e -> toggleStyle("underline"));

        // ==== Upload Image Button ====
        uploadImgBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Image");
            chooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
            );
            File file = chooser.showOpenDialog(primaryStage);

            if (file != null) {
                lastImageFile = file;
                Image img = new Image(file.toURI().toString());
                imagePreview.setImage(img);
            }
        });

        // ==== Create Note ====
        createBtn.setOnAction(e -> {
            String text = editor.getText();
            String imgPath = (lastImageFile != null) ? lastImageFile.getAbsolutePath() : "";

            service.createNote(text, imgPath);
            editor.clear();
            imagePreview.setImage(null);
            lastImageFile = null;

            refreshList();
        });

        // ==== Edit Note ====
        editBtn.setOnAction(e -> {
            Note selected = notesList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                String imgPath = (lastImageFile != null) ? lastImageFile.getAbsolutePath() : "";
                service.editNote(selected.getId(), editor.getText(), imgPath);
                refreshList();
            }
        });

        // ==== Delete Note ====
        deleteBtn.setOnAction(e -> {
            Note selected = notesList.getSelectionModel().getSelectedItem();
            if (selected != null) {
                service.deleteNoteById(selected.getId());
                editor.clear();
                imagePreview.setImage(null);
                lastImageFile = null;
                refreshList();
            }
        });

        emojiBtn.setOnAction(e -> showEmojiPicker());
    }

    // === Text Formatting ===
    private void toggleStyle(String style) {
        int start = editor.getSelection().getStart();
        int end = editor.getSelection().getEnd();

        if (start == end) return;

        if (editor.getStyleAtPosition(start).contains(style)) {
            editor.setStyle(start, end, Collections.emptyList());
        } else {
            editor.setStyle(start, end, List.of(style));
        }
    }

    // === Refresh sidebar list ===
    private void refreshList() {
        List<Note> list = new ArrayList<>();
        service.getAllNotes().forEach(list::add);
        notesList.getItems().setAll(list);
    }

    // === Emoji Picker ===
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
                editor.appendText(emoji);
                popup.close();
            });
            grid.add(b, col, row);
            if (++col == 5) { col = 0; row++; }
        }

        Scene s = new Scene(grid);
        popup.setScene(s);
        popup.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

