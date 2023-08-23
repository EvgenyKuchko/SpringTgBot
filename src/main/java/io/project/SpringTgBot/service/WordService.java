package io.project.SpringTgBot.service;

import io.project.SpringTgBot.model.Word;
import io.project.SpringTgBot.repository.WordRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class WordService {

    @Autowired
    private WordRepository wordRepository;

    @Transactional
    public boolean isWordInBotDictionary(String english, String russian) {
        List<Word> words = wordRepository.findAllByEnglish(english);
        log.info("Check is word(" + english + " - " + russian + ") in the bot dictionary or not");
        if (words.isEmpty()) {
            log.info("Word isn't in the bot dictionary");
            return false;
        }
        boolean result = false;
        for (Word w : words) {
            if (w.getRussian().equals(russian)) {
                result = true;
                log.info("Word is in the bot dictionary");
                break;
            }
        }
        return result;
    }

    @Transactional
    public Word getWordFromBotDictionary(String english, String russian) {
        log.info("Get word(" + english + " - " + russian + ") from bot dictionary");
        return wordRepository.findByEnglishAndRussian(english, russian);
    }

    @Transactional
    public void addNewWordToDictionary(String english, String russian) {
        Word word = new Word();
        word.setEnglish(english);
        word.setRussian(russian);
        wordRepository.save(word);
        log.info("Word: " + english + " - " + russian + " added in the dictionary");
    }
}