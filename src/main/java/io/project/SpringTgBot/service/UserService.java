package io.project.SpringTgBot.service;

import io.project.SpringTgBot.model.User;
import io.project.SpringTgBot.model.Word;
import io.project.SpringTgBot.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

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

    @Transactional
    public void addNewWordToDictionary(long chatId, Word word) {
        User user = userRepository.getUserById(chatId);
        var words = user.getWords();
        words.add(word);
    }

    @Transactional
    public String getAllWords(long chatId) {
        User user = userRepository.getUserById(chatId);
        String words = "List of your words:\n";
        List<Word> list = user.getWords();
        if(!list.isEmpty()) {
            for (Word w : list) {
                words += "\n" + w.getEnglish() + " - " + w.getRussian();
            }
        }else {
            words += "Your dictionary is empty";
        }
        return words;
    }
}