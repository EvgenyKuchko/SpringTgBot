package io.project.SpringTgBot.repository;

import io.project.SpringTgBot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}