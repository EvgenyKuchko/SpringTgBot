package io.project.SpringTgBot.repository;

import io.project.SpringTgBot.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<Word, Long> {
}