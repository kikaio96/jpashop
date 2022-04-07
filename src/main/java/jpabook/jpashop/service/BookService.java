package jpabook.jpashop.service;

import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.BookState;
import jpabook.jpashop.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;

    public List<Book> getBookList(int start, int end) {
        return bookRepository.findLimit(start, end);
    }

    public int countBook() {
        return bookRepository.countBook();
    }

    public List<Book> getBookListByUser(int start, int end) {
        return bookRepository.findLimitByUser(start, end);
    }

    public int countBookByUser() {
        return bookRepository.countBookByUser();
    }

    public Book getBook(Long book_id) {
        return bookRepository.findOne(book_id);
    }

    @Transactional
    public Book addBook(Book book) {
        bookRepository.save(book);
        return book;
    }

    @Transactional
    public void changeState(Long id, BookState bookState) {
        bookRepository.changeBookState(id, bookState);
    }

    @Transactional
    public void updateBook(Book book) {
        bookRepository.updateById(book);
    }
}
