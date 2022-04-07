package jpabook.jpashop.service;

import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.Lend;
import jpabook.jpashop.domain.LendState;
import jpabook.jpashop.repository.BookRepository;
import jpabook.jpashop.repository.LendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class LendService {

    private final LendRepository lendRepository;
    private final BookRepository bookRepository;

    public List<Lend> getLendList(int start, int limit) {
        return lendRepository.getLendList(start, limit);
    }

    public int lendCount() {
        return lendRepository.lendCount();
    }

    public List<Lend> getLendList(int start, int limit, Long userId) {
        return lendRepository.getLendList(start, limit, userId);
    }

    public int lendCount(Long userId) {
        return lendRepository.lendCount(userId);
    }

    @Transactional
    public void save(Lend lend) throws Exception {
        Book book = bookRepository.findOne(lend.getBook().getId());
        int remain = book.getRemain();
        if(remain == 0) {
            throw new Exception("대여 가능 수량 부족");
        } else {
            book.changeBookHold(book.getTotal_count(), book.getRemain() - 1);
            bookRepository.updateById(book);

            lendRepository.save(lend);
        }
    }

    @Transactional
    public void returnLend(Long id, LendState lendState) {
        Lend lend = lendRepository.findOne(id);
        lendRepository.changeLendState(id, lendState);

        Book book = lend.getBook();
        book.changeBookHold(book.getTotal_count(), book.getRemain() + 1);
        bookRepository.updateById(book);
    }

    @Transactional
    public void updateLend(Lend lend) {
        lendRepository.updateById(lend);
    }
}
