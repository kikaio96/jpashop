package jpabook.jpashop.service;

import jpabook.jpashop.domain.User;
import jpabook.jpashop.domain.UserState;
import jpabook.jpashop.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getUserList(int start, int limit) {
        return userRepository.findLimit(start, limit);
    }

    public User getUser(Long id) throws Exception {
        return userRepository.findOne(id);
    }

    public User login(String email, String password) {
        return userRepository.findOne(email, password);
    }

    @Transactional
    public User addUser(User user) {
        userRepository.save(user);
        return user;
    }

    @Transactional
    public void changeState(Long id, UserState userState) {
        userRepository.changeUserState(id, userState);
    }

    @Transactional
    public void updateUserByUser(User user) {
        userRepository.updateByUser(user);
    }

    @Transactional
    public void updateUserByAdmin(User user) {
        userRepository.updateByAdmin(user);
    }

    public int userCount() {
        return userRepository.countUser();
    }
}
