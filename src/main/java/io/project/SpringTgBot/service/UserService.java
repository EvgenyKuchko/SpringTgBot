package io.project.SpringTgBot.service;

import io.project.SpringTgBot.model.User;
import io.project.SpringTgBot.model.Word;
import io.project.SpringTgBot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void addNewUser(long chatId, String name) {
        boolean isExist = userRepository.existsById(chatId);
        if (!isExist) {
            User user = new User();
            user.setId(chatId);
            user.setFirstName(name);
            user.setWords(new HashSet<>());
            userRepository.save(user);
        }
    }

    @Transactional
    public boolean isWordInUserDictionary(long chatId, String english, String russian) {
        User user = userRepository.getUserById(chatId);
        var words = user.getWords();
        Optional<Word> w = words.stream()
                .filter(x -> x.getEnglish().equals(english) && x.getRussian().equals(russian))
                .findFirst();
        Word word = w.get();
        return !word.getEnglish().isEmpty();
    }
}