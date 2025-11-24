package com.bookinventory.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.bookinventory.model.Book;
import com.bookinventory.util.DBConnection;

public class BookDAO {

    // INSERT Book
    public boolean addBook(Book book) {
        String sql = "INSERT INTO book (title, author, publication, genre, dateOfPublication, edition) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getPublication());
            ps.setString(4, book.getGenre());
            ps.setString(5, book.getDateOfPublication());
            ps.setString(6, book.getEdition());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // GET ALL Books
    public List<Book> getAllBooks() {
        List<Book> list = new ArrayList<>();
        String sql = "SELECT * FROM book";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Book b = new Book();
                b.setIsbn(rs.getInt("isbn"));
                b.setTitle(rs.getString("title"));
                b.setAuthor(rs.getString("author"));
                b.setPublication(rs.getString("publication"));
                b.setGenre(rs.getString("genre"));
                b.setDateOfPublication(rs.getString("dateOfPublication"));
                b.setEdition(rs.getString("edition"));

                list.add(b);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    // GET ONE BOOK by ISBN
    public Book getBookByIsbn(int isbn) {
        Book b = null;
        String sql = "SELECT * FROM book WHERE isbn = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, isbn);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                b = new Book();
                b.setIsbn(rs.getInt("isbn"));
                b.setTitle(rs.getString("title"));
                b.setAuthor(rs.getString("author"));
                b.setPublication(rs.getString("publication"));
                b.setGenre(rs.getString("genre"));
                b.setDateOfPublication(rs.getString("dateOfPublication"));
                b.setEdition(rs.getString("edition"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return b;
    }

    // UPDATE BOOK
    public boolean updateBook(Book book) {
        String sql = "UPDATE book SET title=?, author=?, publication=?, genre=?, dateOfPublication=?, edition=? WHERE isbn=?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, book.getTitle());
            ps.setString(2, book.getAuthor());
            ps.setString(3, book.getPublication());
            ps.setString(4, book.getGenre());
            ps.setString(5, book.getDateOfPublication());
            ps.setString(6, book.getEdition());
            ps.setInt(7, book.getIsbn());  // this is FIXED

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // DELETE BOOK
    public boolean deleteBook(int isbn) {
        String sql = "DELETE FROM book WHERE isbn = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, isbn);
            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
}
