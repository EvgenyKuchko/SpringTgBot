package io.project.SpringTgBot.model;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "words")
@Data
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String english;
    private String russian;
    @ManyToMany(mappedBy = "words", fetch = FetchType.EAGER)
    private List<User> users = new ArrayList<>();
}