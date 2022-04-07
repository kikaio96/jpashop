package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Lend;
import jpabook.jpashop.domain.LendState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class LendRepository {

    private final EntityManager em;

    public void save(Lend lend) {
        em.persist(lend);
    }

    public List<Lend> getLendList(int start, int limit) {
        return em.createQuery("select l from Lend l order by l.id", Lend.class)
                .setFirstResult(start)
                .setMaxResults(limit)
                .getResultList();
    }

    public int lendCount() {
        return em.createQuery("select l from Lend l")
                .getResultList()
                .size();
    }

    public List<Lend> getLendList(int start, int limit, Long userId) {
        return em.createQuery("select l from Lend l where l.user.id = :userId order by l.id", Lend.class)
                .setFirstResult(start)
                .setMaxResults(limit)
                .setParameter("userId", userId)
                .getResultList();
    }

    public int lendCount(Long userId) {
        return em.createQuery("select l from Lend l where l.user.id = :userId", Lend.class)
                .setParameter("userId", userId)
                .getResultList()
                .size();
    }

    public Lend findOne(Long id) {
        return em.find(Lend.class, id);
    }

    public void changeLendState(Long id, LendState lendState) {
        Lend lend = em.find(Lend.class, id);
        lend.changeLendState(lendState);
    }

    public void updateById(Lend lend) throws NullPointerException {
        Lend oldLend = em.find(Lend.class, lend.getId());
        oldLend.chaneReturnDate(lend.getReturnDate());
    }


}
