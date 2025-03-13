package com.webstore.services;

import com.webstore.model.Role;
import com.webstore.model.entities.User;
import com.webstore.exceptions.auth.NotUniqueLoginException;
import com.webstore.repositories.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UsersRepository repository;

    public void register(String login, String password, String nick) throws NotUniqueLoginException {
        if (repository.findById(login).isPresent()) {
            throw new NotUniqueLoginException();
        }

        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setNick(nick);
        user.setRole(Role.CUST);
        repository.save(user);
    }
}
