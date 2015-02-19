package ru.yesdo.db.repository;

import org.springframework.data.repository.CrudRepository;
import ru.yesdo.model.Merchant;
import ru.yesdo.model.User;

import java.util.List;

public interface UserRepository extends CrudRepository<User, Long> {
	List<User> findByLogin(String login);
	List<User> findByMerchant(Merchant merchant);
}