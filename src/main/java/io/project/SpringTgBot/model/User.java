package io.project.SpringTgBot.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.LinkedList;

@Entity
@Data
@Table(name = "users")
public class User {
    @Id
    private long chatId;
    private String firstName;
    @OneToMany
    private LinkedList<Word> words;
}