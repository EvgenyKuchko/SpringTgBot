package io.project.SpringTgBot.repository;

import io.project.SpringTgBot.model.User;
import io.project.SpringTgBot.model.Word;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User getUserById(long id);
}