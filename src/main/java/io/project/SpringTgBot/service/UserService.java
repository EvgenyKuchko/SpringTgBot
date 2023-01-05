package io.project.SpringTgBot.service;

import io.project.SpringTgBot.model.User;
import io.project.SpringTgBot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public void addNewUser(long chatId, String name) {
        boolean isExist = userRepository.existsById(chatId);
        if(!isExist) {
            User user = new User();
            user.setId(chatId);
            user.setFirstName(name);
            user.setWords(new HashSet<>());
            userRepository.save(user);
        }
    }
}