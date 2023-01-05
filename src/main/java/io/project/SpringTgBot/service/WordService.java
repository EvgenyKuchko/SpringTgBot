package io.project.SpringTgBot.service;

import io.project.SpringTgBot.model.Word;
import io.project.SpringTgBot.repository.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WordService {

    @Autowired
    private WordRepository wordRepository;

    private boolean isWordInDictionary(String english, String russian) {
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
}