package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.User;
import jpabook.jpashop.domain.UserState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class UserRepository {

    private final EntityManager em;

    public void save(User user) {
        em.persist(user);
    }

    public List<User> findLimit(int start, int limit) {
        return em.createQuery("select u from User u order by u.id", User.class)
                .setFirstResult(start)
                .setMaxResults(limit)
                .getResultList();
    }

    public User findOne(Long id) throws Exception {
        User user = em.find(User.class, id);
        if(user == null) {
            throw new Exception("존재하지 않는 회원");
        }

        return em.find(User.class, id);
    }

    public User findOne(String email, String password) {
        return em.createQuery("select u from User u where u.email = :email and u.password = :password", User.class)
                .setParameter("email", email)
                .setParameter("password", password)
                .getResultList()
                .get(0);
    }

    public void changeUserState(Long id, UserState state) {
        User user = em.find(User.class, id);
        user.changeUserState(state);
    }

    public void updateByUser(User user) throws NullPointerException {
        User oldUser = em.find(User.class, user.getId());
        oldUser.changeUserByUser(user.getPhone(), user.getPassword(), user.getBirthday());
    }

    public void updateByAdmin(User user) throws NullPointerException {
        User oldUser = em.find(User.class, user.getId());
        oldUser.changeUserByAdmin(user.getName(), user.getNumber(), user.getPhone(), user.getBirthday());
    }

    public int countUser() {
        return em.createQuery("select u from User u", User.class)
                .getResultList()
                .size();
    }
}
