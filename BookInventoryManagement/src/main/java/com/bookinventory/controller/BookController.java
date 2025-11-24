package com.bookinventory.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.bookinventory.dao.BookDAO;
import com.bookinventory.model.Book;
import com.google.gson.Gson;

@WebServlet("/books/*")
public class BookController extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private BookDAO dao = new BookDAO();
    private Gson gson = new Gson();

    // ---------------------- GET ----------------------------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        String path = req.getPathInfo();

        if (path == null || path.equals("/")) {
            List<Book> list = dao.getAllBooks();
            out.print(gson.toJson(list));
        } else {
            int isbn = Integer.parseInt(path.substring(1));
            Book b = dao.getBookByIsbn(isbn);
            if (b != null)
                out.print(gson.toJson(b));
            else
                out.print("{\"message\":\"Book Not Found\"}");
        }
    }

    // ---------------------- POST ----------------------------
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        BufferedReader reader = req.getReader();
        Book book = gson.fromJson(reader, Book.class);

        boolean status = dao.addBook(book);

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        if (status)
            out.print("{\"message\":\"Book Added Successfully\"}");
        else
            out.print("{\"message\":\"Failed to Add Book\"}");
    }

    // ---------------------- PUT (UPDATE) ----------------------
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String path = req.getPathInfo();  // format: /1001
        int isbn = Integer.parseInt(path.substring(1)); 
        BufferedReader reader = req.getReader();

        Book book = gson.fromJson(reader, Book.class);
        book.setIsbn(isbn);  // IMPORTANT: bind URL param to model

        boolean status = dao.updateBook(book);

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        if (status)
            out.print("{\"message\":\"Book Updated Successfully\"}");
        else
            out.print("{\"message\":\"Failed to Update Book\"}");
    }

    // ---------------------- DELETE ----------------------------
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String path = req.getPathInfo(); // /1001
        int isbn = Integer.parseInt(path.substring(1));

        boolean status = dao.deleteBook(isbn);

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        if (status)
            out.print("{\"message\":\"Book Deleted Successfully\"}");
        else
            out.print("{\"message\":\"Failed to Delete Book\"}");
    }
}
