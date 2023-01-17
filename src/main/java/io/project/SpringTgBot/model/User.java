package io.project.SpringTgBot.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    private long id;
    private String firstName;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "words_users",
            joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "word_id", referencedColumnName = "id"))
    private List<Word> words = new LinkedList<>();
}