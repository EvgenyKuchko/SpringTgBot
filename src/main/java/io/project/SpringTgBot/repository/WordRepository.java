package io.project.SpringTgBot.repository;

import io.project.SpringTgBot.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface WordRepository extends JpaRepository<Word, Long> {
    List<Word> findAllByEnglish(String english);

    @Modifying
    @Query("SELECT w FROM Word w WHERE w.english = :english AND w.russian = :russian")
    Word findWordByFields(@Param("english")String english, @Param("russian")String russian);
}