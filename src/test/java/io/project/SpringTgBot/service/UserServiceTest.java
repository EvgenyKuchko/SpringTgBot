package io.project.SpringTgBot.service;

import io.project.SpringTgBot.model.User;
import io.project.SpringTgBot.model.Word;
import io.project.SpringTgBot.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;
    @Mock
    private UserRepository userRepository;

    static User user;

    @BeforeAll
    static void setUp() {
        long id = 123;
        String name = "user";
        List<Word> words = new LinkedList<>();
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
        user = new User();
        user.setId(id);
        user.setFirstName(name);
        user.setWords(words);
    }

    @Test
    public void addNewUser_UserAlreadyExist() {
        when(userRepository.existsById(user.getId())).thenReturn(true);
        userService.addNewUser(user.getId(), user.getFirstName());

        verify(userRepository, times(1)).existsById(user.getId());
        verify(userRepository, times(0)).save(user);
    }

    @Test
    public void addNewUser_UserIsNotExist() {
        String name = "user";
        long chatId = 123L;

        when(userRepository.existsById(user.getId())).thenReturn(false);
        userService.addNewUser(user.getId(), user.getFirstName());

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User user = captor.getValue();
        assertEquals(chatId, user.getId());
        assertEquals(name, user.getFirstName());
    }

    @Test
    public void isWordInUserDictionary_ReturnTrue() {
        String english = "cat";
        String russian = "кот";

        when(userRepository.getUserById(user.getId())).thenReturn(user);
        boolean result = userService.isWordInUserDictionary(user.getId(), english, russian);

        assertTrue(result);
    }

    @Test
    public void isWordInUserDictionary_ReturnFalse() {
        String english = "shark";
        String russian = "акула";

        when(userRepository.getUserById(user.getId())).thenReturn(user);
        boolean result = userService.isWordInUserDictionary(user.getId(), english, russian);

        assertFalse(result);
    }

    @Test
    public void addNewWordToDictionary() {
        Word word = new Word();
        word.setEnglish("shark");
        word.setRussian("акула");

        when(userRepository.getUserById(user.getId())).thenReturn(user);
        userService.addNewWordToDictionary(user.getId(), word);

        verify(userRepository).save(user);
        assertEquals(6, user.getWords().size());
        assertEquals(word, user.getWords().get(user.getWords().size() - 1));
    }

    @Test
    public void getAllWords_WordsIsNotEmpty() {
        when(userRepository.getUserById(user.getId())).thenReturn(user);
        String result = userService.getAllWords(user.getId());

        assertFalse(result.isEmpty());
        assertFalse(result.contains("Your dictionary is empty"));
        assertTrue(result.contains("moon"));
    }

    @Test
    public void getAllWords_WordsIsEmpty() {
        User user = new User();
        List<Word> words = new LinkedList<>();
        user.setWords(words);

        when(userRepository.getUserById(user.getId())).thenReturn(user);
        String result = userService.getAllWords(user.getId());

        assertFalse(result.isEmpty());
        assertTrue(result.contains("Your dictionary is empty"));
    }

    @Test
    public void removeWord() {
        String english = "pilot";
        String russian = "пилот";
        Word removedWord = new Word();
        removedWord.setRussian(russian);
        removedWord.setEnglish(english);
        user.getWords().add(removedWord);

        when(userRepository.getUserById(user.getId())).thenReturn(user);
        userService.removeWord(user.getId(), english, russian);

        assertFalse(user.getWords().contains(removedWord));
    }

    @Test
    public void getWordsForQuiz() {
        when(userRepository.getUserById(user.getId())).thenReturn(user);
        List<Word> listOfWords = userService.getWordsForQuiz(user.getId());

        assertEquals(5, listOfWords.size());
    }

    @Test
    public void getSizeOfDictionary() {
        int expectedSize = user.getWords().size();

        when(userRepository.getUserById(user.getId())).thenReturn(user);
        int result = userService.getSizeOfDictionary(user.getId());

        assertEquals(expectedSize, result);
    }
}