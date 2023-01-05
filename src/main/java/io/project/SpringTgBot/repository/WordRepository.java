package io.project.SpringTgBot.repository;

import io.project.SpringTgBot.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findAllByEnglish(String english);
}