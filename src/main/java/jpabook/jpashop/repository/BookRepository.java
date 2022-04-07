package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.BookState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class BookRepository {

    private final EntityManager em;

    public void save(Book book) {
        em.persist(book);
    }

    public Book findOne(Long id) {
        return em.find(Book.class, id);
    }

    public List<Book> findLimit(int start, int limit) {
        return em.createQuery("select b from Book b order by b.id", Book.class)
                .setFirstResult(start)
                .setMaxResults(limit)
                .getResultList();
    }

    public int countBook() {
        return em.createQuery("select b from Book b", Book.class)
                .getResultList()
                .size();
    }

    public List<Book> findLimitByUser(int start, int limit) {
        return em.createQuery("select b from Book b where b.state = 'ACTIVATE' order by b.id", Book.class)
                .setFirstResult(start)
                .setMaxResults(limit)
                .getResultList();
    }

    public int countBookByUser() {
        return em.createQuery("select b from Book b where b.state = 'ACTIVATE'", Book.class)
                .getResultList()
                .size();
    }

    public void changeBookState(Long id, BookState bookState) {
        Book book = em.find(Book.class, id);
        book.changeBookState(bookState);
    }

    public void updateById(Book book) throws NullPointerException {
        Book oldBook = em.find(Book.class, book.getId());

        //책 정보 업데이트
        oldBook.chaneBook(book.getName(), book.getInfo(), book.getIsbn(),
                            book.getGenre(), book.getAuthor(), book.getPlait(),
                            book.getPublisher());

        //책 수량 업데이트
        int remain = oldBook.getRemain() + (book.getTotal_count() - oldBook.getTotal_count());
        oldBook.changeBookHold(book.getTotal_count(), remain);
    }
}
