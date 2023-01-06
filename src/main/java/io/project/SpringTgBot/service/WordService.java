package io.project.SpringTgBot.service;

import io.project.SpringTgBot.model.Word;
import io.project.SpringTgBot.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WordService {

    @Autowired
    private WordRepository wordRepository;

    @Transactional
    public boolean isWordInBotDictionary(String english, String russian) {
        var words = wordRepository.findAllByEnglish(english);
        if (words.isEmpty()) {
            return false;
        }
        var result = false;
        for (Word w : words) {
            if (w.getRussian().equals(russian)) {
                result = true;
                break;
            }
        }
        return result;
    }

    @Transactional
    public Word getWordFromBotDictionary(String english, String russian) {
        return wordRepository.findByEnglishAndRussian(english, russian);
    }

    @Transactional
    public void addNewWordToDictionary(String english, String russian) {
        Word word = new Word();
        word.setEnglish(english);
        word.setRussian(russian);
        wordRepository.save(word);
    }
}