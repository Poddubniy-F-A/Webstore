package com.webstore.services;

import com.webstore.entities.User;
import com.webstore.exceptions.NotUniqueLoginException;
import com.webstore.repositories.UsersRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class RegistrationService {
    private UsersRepository repository;

    public void register(String login, String password, String nick) throws NotUniqueLoginException {
        Optional<User> response = repository.findById(login);
        if (response.isEmpty()) {
            User user = new User();
            user.setLogin(login);
            user.setPassword(password);
            user.setNick(nick);
            user.setStatus(User.Status.cust);
            repository.save(user);
        } else {
            throw new NotUniqueLoginException();
        }
    }
}
