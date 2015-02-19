package ru.yesdo.service;

import org.springframework.stereotype.Service;
import ru.yesdo.db.repository.UserRepository;
import ru.yesdo.model.User;
import ru.yesdo.model.data.UserData;

import javax.annotation.Resource;

/**
 * Created by lameroot on 18.02.15.
 */
@Service
public class UserService {

    @Resource
    private UserRepository userRepository;


    public User create(UserData userData) {
        User user = new User();

        return user;
    }

}
