package io.project.SpringTgBot.service;

import io.project.SpringTgBot.model.Word;
import io.project.SpringTgBot.repository.WordRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WordServiceTest {
    @InjectMocks
    private WordService wordService;
    @Mock
    private WordRepository wordRepository;

    static List<Word> words;

    @BeforeAll
    static void setUp() {
        words = new ArrayList<>();
        Word w1 = new Word();
        w1.setEnglish("hello");
        w1.setRussian("привет");
        Word w2 = new Word();
        w2.setEnglish("bye");
        w2.setRussian("пока");
        Word w3 = new Word();
        w3.setEnglish("sun");
        w3.setRussian("солнце");
        Word w4 = new Word();
        w4.setEnglish("moon");
        w4.setRussian("луна");
        Word w5 = new Word();
        w5.setEnglish("cat");
        w5.setRussian("кот");
        words.add(w1);
        words.add(w2);
        words.add(w3);
        words.add(w4);
        words.add(w5);
    }

    @Test
    public void isWordInBotDictionary_ReturnTrue() {
        String english = "hello";
        String russian = "привет";
        Word word = new Word();
        word.setEnglish(english);
        word.setRussian(russian);
        List<Word> listOfWords = words.stream().filter(w -> w.getEnglish().equals(english)).collect(Collectors.toList());

        when(wordRepository.findAllByEnglish(english)).thenReturn(listOfWords);
        boolean result = wordService.isWordInBotDictionary(english, russian);

        assertFalse(listOfWords.isEmpty());
        assertTrue(result);
    }

    @Test
    public void isWordInBotDictionary_ReturnFalse() {
        String english = "hello";
        String russian = "здравствуйте";
        Word word = new Word();
        word.setEnglish(english);
        word.setRussian(russian);
        List<Word> listOfWords = words.stream().filter(w -> w.getEnglish().equals(english)).collect(Collectors.toList());

        when(wordRepository.findAllByEnglish(english)).thenReturn(listOfWords);
        boolean result = wordService.isWordInBotDictionary(english, russian);

        assertFalse(listOfWords.isEmpty());
        assertFalse(result);
    }

    @Test
    public void isWordInBotDictionary_ReturnFalseToo() {
        String english = "dog";
        String russian = "собака";
        Word word = new Word();
        word.setEnglish(english);
        word.setRussian(russian);
        List<Word> listOfWords = words.stream().filter(w -> w.getEnglish().equals(english)).collect(Collectors.toList());

        when(wordRepository.findAllByEnglish(english)).thenReturn(listOfWords);
        boolean result = wordService.isWordInBotDictionary(english, russian);

        assertTrue(listOfWords.isEmpty());
        assertFalse(result);
    }

    @Test
    public void getWordFromBotDictionary() {
        String english = "moon";
        String russian = "луна";
        Word word = new Word();
        word.setRussian(russian);
        word.setEnglish(english);

        when(wordRepository.findByEnglishAndRussian(english, russian)).thenReturn(word);
        Word result = wordService.getWordFromBotDictionary(english, russian);

        assertEquals(word, result);
    }

    @Test
    public void addNewWordToDictionary() {
        String english = "wave";
        String russian = "волна";

        wordService.addNewWordToDictionary(english, russian);

        ArgumentCaptor<Word> captor = ArgumentCaptor.forClass(Word.class);
        verify(wordRepository).save(captor.capture());
        Word word = captor.getValue();
        assertEquals(english, word.getEnglish());
        assertEquals(russian, word.getRussian());
    }
}