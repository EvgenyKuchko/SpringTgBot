package io.project.SpringTgBot.repository;

import io.project.SpringTgBot.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findAllByEnglish(String english);

    Word findByEnglishAndRussian(String english, String russian);
}