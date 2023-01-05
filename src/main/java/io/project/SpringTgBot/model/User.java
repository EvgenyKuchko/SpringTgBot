package io.project.SpringTgBot.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    private long id;
    private String firstName;
    @ManyToMany
    @JoinTable(
            name = "words_users",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "word_id"))
    private Set<Word> words;
}