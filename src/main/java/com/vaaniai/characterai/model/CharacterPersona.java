package com.vaaniai.characterai.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CharacterPersona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String personaDescription;

    // Relations
    @OneToMany(mappedBy = "characterPersona")
    @JsonManagedReference("character-chat")
    private List<ChatSession> chatSessions;

    public CharacterPersona(String name, String personaDescription) {
        this.name = name;
        this.personaDescription = personaDescription;
    }

}
