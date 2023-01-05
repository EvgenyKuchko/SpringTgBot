package io.project.SpringTgBot.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name = "words")
@Data
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String english;
    private String russian;
    @ManyToMany(mappedBy = "words")
    private Set<User> users;
}