package io.project.SpringTgBot.service;

import io.project.SpringTgBot.model.User;
import io.project.SpringTgBot.model.Word;
import io.project.SpringTgBot.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void addNewUser(long chatId, String name) {
        boolean isExist = userRepository.existsById(chatId);
        log.info("Check " + name + " is in the DB");
        if (!isExist) {
            log.info("Add " + name + " in the DB");
            User user = new User();
            user.setId(chatId);
            user.setFirstName(name);
            userRepository.save(user);
        }
    }

    @Transactional
    public boolean isWordInUserDictionary(long chatId, String english, String russian) {
        log.info("Check word: " + english + " - " + russian + ", is in the user dictionary");
        User user = userRepository.getUserById(chatId);
        var words = user.getWords();
        for (Word w : words) {
            if (w.getEnglish().equals(english) && w.getRussian().equals(russian)) {
                log.info("Word is in the user dictionary");
                return true;
            }
        }
        log.info("Word isn't in the user dictionary");
        return false;
    }

    @Transactional
    public void addNewWordToDictionary(long chatId, Word word) {
        log.info("Add new " + word + " to user dictionary");
        User user = userRepository.getUserById(chatId);
        user.getWords().add(word);
        userRepository.save(user);
    }

    @Transactional
    public String getAllWords(long chatId) {
        User user = userRepository.getUserById(chatId);
        String words = ":book: List of your words:\n";
        List<Word> list = user.getWords();
        if (!list.isEmpty()) {
            for (Word w : list) {
                words += "\n" + w.getEnglish() + " - " + w.getRussian();
            }
            log.info("Get all words from " + user.getFirstName() + "'s dictionary");
        } else {
            words += "Your dictionary is empty";
            log.info(user.getFirstName() + "'s dictionary is empty");
        }
        return words;
    }

    @Transactional
    public void removeWord(long chatId, String english, String russian) {
        User user = userRepository.getUserById(chatId);
        List<Word> words = user.getWords();
        words.removeIf(w -> w.getEnglish().equals(english) && w.getRussian().equals(russian));
        userRepository.save(user);
        log.info("Word: " + english + " - " + russian + " removed from " + user.getFirstName() + "'s dictionary");
    }

    @Transactional
    public List<Word> getWordsForQuiz(long chatId) {
        User user = userRepository.getUserById(chatId);
        List<Word> words = user.getWords();
        Collections.shuffle(words);
        log.info("Get words from " + user.getFirstName() + "'s dictionary for quiz");
        return words.subList(0, 5);
    }

    @Transactional
    public int getSizeOfDictionary(long chatId) {
        User user = userRepository.getUserById(chatId);
        log.info("Get size of " + user.getFirstName() + "'s dictionary");
        return user.getWords().size();
    }
}