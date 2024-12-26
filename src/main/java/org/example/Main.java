package org.example;

import java.util.*;
import java.io.*;

class Book {
    private String title;
    private String author;
    private String ISBN;
    private boolean isBorrowed;
    private String dueDate;

    public Book(String title, String author, String ISBN) {
        this.title = title;
        this.author = author;
        this.ISBN = ISBN;
        this.isBorrowed = false;
        this.dueDate = null;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getISBN() { return ISBN; }
    public boolean isBorrowed() { return isBorrowed; }
    public String getDueDate() { return dueDate; }

    public void borrowBook(String dueDate) {
        this.isBorrowed = true;
        this.dueDate = dueDate;
    }

    public void returnBook() {
        this.isBorrowed = false;
        this.dueDate = null;
    }

    @Override
    public String toString() {
        return "Book [Title=" + title + ", Author=" + author + ", ISBN=" + ISBN +
                ", Borrowed=" + isBorrowed + ", DueDate=" + dueDate + "]";
    }
}


class Library {
    private Map<String, Book> books;

    public Library() {
        books = new HashMap<>();
    }

    public void addBook(Book book) {
        books.put(book.getISBN(), book);
    }

    public void removeBook(String ISBN) {
        books.remove(ISBN);
    }

    public void editBook(String ISBN, Book updatedBook) {
        books.put(ISBN, updatedBook);
    }

    public Book searchBookByISBN(String ISBN) {
        return books.get(ISBN);
    }

    public List<Book> searchBooks(String query) {
        List<Book> results = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    book.getAuthor().toLowerCase().contains(query.toLowerCase()) ||
                    book.getISBN().equals(query)) {
                results.add(book);
            }
        }
        return results;
    }

    public void borrowBook(String ISBN, String dueDate) {
        Book book = books.get(ISBN);
        if (book != null && !book.isBorrowed()) {
            book.borrowBook(dueDate);
        }
    }

    public void returnBook(String ISBN) {
        Book book = books.get(ISBN);
        if (book != null && book.isBorrowed()) {
            book.returnBook();
        }
    }

    public List<Book> getAvailableBooks() {
        List<Book> availableBooks = new ArrayList<>();
        for (Book book : books.values()) {
            if (!book.isBorrowed()) {
                availableBooks.add(book);
            }
        }
        return availableBooks;
    }

    public List<Book> getBorrowedBooks() {
        List<Book> borrowedBooks = new ArrayList<>();
        for (Book book : books.values()) {
            if (book.isBorrowed()) {
                borrowedBooks.add(book);
            }
        }
        return borrowedBooks;
    }

    public void saveToFile(String filename) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filename))) {
            out.writeObject(books);
        }
    }

    @SuppressWarnings("unchecked")
    public void loadFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(filename))) {
            books = (Map<String, Book>) in.readObject();
        }
    }
}

public class Main {
    public static void main(String[] args) {
        Library library = new Library();
        Scanner scanner = new Scanner(System.in);

        try {
            library.loadFromFile("library_data.dat");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("No existing library data found. Starting fresh.");
        }

        while (true) {
            System.out.println("\nLibrary Management System");
            System.out.println("1. Add Book");
            System.out.println("2. Remove Book");
            System.out.println("3. Edit Book");
            System.out.println("4. Search Books");
            System.out.println("5. Borrow Book");
            System.out.println("6. Return Book");
            System.out.println("7. View Available Books");
            System.out.println("8. View Borrowed Books");
            System.out.println("9. Exit");

            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1: // Add Book
                    System.out.print("Enter title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter author: ");
                    String author = scanner.nextLine();
                    System.out.print("Enter ISBN: ");
                    String ISBN = scanner.nextLine();
                    library.addBook(new Book(title, author, ISBN));
                    System.out.println("Book added successfully!");
                    break;
                case 2: // Remove Book
                    System.out.print("Enter ISBN to remove: ");
                    ISBN = scanner.nextLine();
                    library.removeBook(ISBN);
                    System.out.println("Book removed successfully!");
                    break;
                case 3: // Edit Book
                    System.out.print("Enter ISBN to edit: ");
                    ISBN = scanner.nextLine();
                    System.out.print("Enter new title: ");
                    title = scanner.nextLine();
                    System.out.print("Enter new author: ");
                    author = scanner.nextLine();
                    library.editBook(ISBN, new Book(title, author, ISBN));
                    System.out.println("Book edited successfully!");
                    break;
                case 4: // Search Books
                    System.out.print("Enter title, author, or ISBN to search: ");
                    String query = scanner.nextLine();
                    List<Book> results = library.searchBooks(query);
                    if (results.isEmpty()) {
                        System.out.println("No books found.");
                    } else {
                        results.forEach(System.out::println);
                    }
                    break;
                case 5: // Borrow Book
                    System.out.print("Enter ISBN to borrow: ");
                    ISBN = scanner.nextLine();
                    System.out.print("Enter due date (YYYY-MM-DD): ");
                    String dueDate = scanner.nextLine();
                    library.borrowBook(ISBN, dueDate);
                    System.out.println("Book borrowed successfully!");
                    break;
                case 6: // Return Book
                    System.out.print("Enter ISBN to return: ");
                    ISBN = scanner.nextLine();
                    library.returnBook(ISBN);
                    System.out.println("Book returned successfully!");
                    break;
                case 7: // View Available Books
                    List<Book> availableBooks = library.getAvailableBooks();
                    if (availableBooks.isEmpty()) {
                        System.out.println("No available books.");
                    } else {
                        availableBooks.forEach(System.out::println);
                    }
                    break;
                case 8: // View Borrowed Books
                    List<Book> borrowedBooks = library.getBorrowedBooks();
                    if (borrowedBooks.isEmpty()) {
                        System.out.println("No borrowed books.");
                    } else {
                        borrowedBooks.forEach(System.out::println);
                    }
                    break;
                case 9: // Exit
                    try {
                        library.saveToFile("library_data.dat");
                        System.out.println("Library data saved successfully.");
                    } catch (IOException e) {
                        System.out.println("Error saving library data: " + e.getMessage());
                    }
                    System.out.println("Exiting the system. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
}