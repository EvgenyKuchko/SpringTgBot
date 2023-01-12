package io.project.SpringTgBot.model;

import lombok.Data;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

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
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "word_id", referencedColumnName = "id"))
    private List<Word> words = new LinkedList<>();
}