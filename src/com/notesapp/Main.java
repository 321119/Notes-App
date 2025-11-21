package com.notesapp;

import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        NoteService service = new NoteService();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("\n--- NOTES APP ---");
            System.out.println("1. Create Note");
            System.out.println("2. Edit Note");
            System.out.println("3. View Note");
            System.out.println("4. View All Notes");
            System.out.println("5. Delete Note");
            System.out.println("6. Exit");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // clear input

            switch (choice) {
                case 1:
                    System.out.print("Enter note content: ");
                    String content = scanner.nextLine();
                    Note created = service.createNote(content);
                    System.out.println("Created note with ID = " + created.getId());
                    break;

                case 2:
                    System.out.print("Enter ID to edit: ");
                    int editId = scanner.nextInt();
                    scanner.nextLine();
                    System.out.print("Enter new content: ");
                    String newContent = scanner.nextLine();
                    service.editNote(editId, newContent);
                    System.out.println("Note updated.");
                    break;

                case 3:
                    System.out.print("Enter ID to view: ");
                    int viewId = scanner.nextInt();
                    Note note = service.getNoteById(viewId);
                    if (note != null) {
                        System.out.println("ID: " + note.getId());
                        System.out.println("Content: " + note.getContent());
                    } else {
                        System.out.println("Note not found.");
                    }
                    break;

                case 4:
                    System.out.println("All notes:");
                    for (Note n : service.getAllNotes()) {
                        System.out.println(n.getId() + ": " + n.getContent());
                    }
                    break;

                case 5:
                    System.out.print("Enter ID to delete: ");
                    int deleteId = scanner.nextInt();
                    boolean removed = service.deleteNoteById(deleteId);
                    System.out.println(removed ? "Deleted." : "Note not found.");
                    break;

                case 6:
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;

                default:
                    System.out.println("Invalid option.");
            }
        }
    }
}
