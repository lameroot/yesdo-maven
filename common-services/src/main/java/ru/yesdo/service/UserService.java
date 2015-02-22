package ru.yesdo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.yesdo.db.repository.MerchantRepository;
import ru.yesdo.db.repository.UserRepository;
import ru.yesdo.exception.AlreadyExistException;
import ru.yesdo.graph.repository.MerchantGraphRepository;
import ru.yesdo.graph.repository.UserGraphRepository;
import ru.yesdo.model.Merchant;
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
    @Resource
    private UserGraphRepository userGraphRepository;
    @Resource
    private MerchantRepository merchantRepository;
    @Resource
    private MerchantGraphRepository merchantGraphRepository;


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public User create(UserData userData) {
        if ( !userRepository.findByLogin(userData.getLogin()).isEmpty() ) throw new AlreadyExistException(userData.getLogin());
        User user = new User();
        user.setLogin(userData.getLogin());
        Merchant merchant = null;
        if ( null == (merchant = merchantRepository.findByName(userData.getMerchant().getName())) ) throw new IllegalArgumentException("Merchant with name: " + userData.getMerchant().getName() + " is absent in database");
        if ( userData.isPartial() && null == merchantGraphRepository.findByName(merchant.getName()) ) {
            throw new IllegalArgumentException("Merchant tish name: " + userData.getMerchant().getName() + " is absent in graph database");
        }

        user.setMerchant(merchant);
        userRepository.save(user);
        if ( userData.isPartial() ) {
            userGraphRepository.save(user);
        }

        return user;
    }

}
