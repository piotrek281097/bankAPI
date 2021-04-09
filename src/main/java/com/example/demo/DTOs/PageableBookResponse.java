package com.example.demo.DTOs;

import com.example.demo.domain.Book;

import java.util.List;

public class PageableBookResponse {

    private List<Book> books;

    private int totalPages;

    private int pageNumber;

    private int pageSize;

    public PageableBookResponse() {
    }

    public PageableBookResponse(List<Book> books, int totalPages, int pageNumber, int pageSize) {
        this.books = books;
        this.totalPages = totalPages;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
