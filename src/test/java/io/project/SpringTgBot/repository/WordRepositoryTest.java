package io.project.SpringTgBot.repository;

import io.project.SpringTgBot.model.Word;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class WordRepositoryTest {

    @Autowired
    private WordRepository wordRepository;

    private final String STRIKE = "strike";
    private final String STRIKE_TR = "удар";

    @Before
    public void init() {
        Word w1 = new Word();
        w1.setEnglish(STRIKE);
        w1.setRussian(STRIKE_TR);
        wordRepository.save(w1);
        Word w2 = new Word();
        w2.setEnglish(STRIKE);
        w2.setRussian("протест");
        wordRepository.save(w2);
        Word w3 = new Word();
        w3.setEnglish("slowly");
        w3.setRussian("медленно");
        wordRepository.save(w3);
    }

    @Test
    public void findAllByEnglish_ShouldReturnList() {
        var words = wordRepository.findAllByEnglish(STRIKE);

        assertThat(words).isNotNull();
        assertEquals(2, words.size());
    }

    @Test
    public void findByEnglishAndRussian_ShouldReturnWord() {
        Word word = wordRepository.findByEnglishAndRussian(STRIKE, STRIKE_TR);

        assertThat(word).isNotNull();
        assertEquals(word.getEnglish(), STRIKE);
        assertEquals(word.getRussian(), STRIKE_TR);
    }

    @Test
    public void save_ShouldSaveWord() {
        Word word = new Word();
        word.setRussian("молоко");
        word.setEnglish("milk");

        Word expectedWord = wordRepository.save(word);

        assertThat(expectedWord).isNotNull();
        assertEquals(expectedWord.getRussian(), word.getRussian());
        assertEquals(expectedWord.getEnglish(), word.getEnglish());
    }
}