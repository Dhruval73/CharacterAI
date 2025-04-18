package com.vaaniai.characterai.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class ChatSession {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JsonBackReference("user-chat")
    private User user;

    @ManyToOne
    @JsonBackReference("character-chat")
    private CharacterPersona characterPersona;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "chatSession", cascade = CascadeType.ALL)
    @JsonManagedReference("chat-messages")
    private List<Message> messages;
}
