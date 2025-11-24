package com.bookinventory.model;

public class Book {
    private int isbn;
    private String title;
    private String author;
    private String publication;
    private String genre;
    private String dateOfPublication;
    private String edition;

    public int getIsbn() { return isbn; }
    public void setIsbn(int isbn) { this.isbn = isbn; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getPublication() { return publication; }
    public void setPublication(String publication) { this.publication = publication; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getDateOfPublication() { return dateOfPublication; }
    public void setDateOfPublication(String dateOfPublication) { this.dateOfPublication = dateOfPublication; }

    public String getEdition() { return edition; }
    public void setEdition(String edition) { this.edition = edition; }
}
