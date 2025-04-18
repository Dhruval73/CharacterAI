package com.vaaniai.characterai.controllers;

import com.vaaniai.characterai.model.CharacterPersona;
import com.vaaniai.characterai.repository.CharacterPersonaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterPersonaController {

    private final CharacterPersonaRepository characterRepo;

    // Create new character
    @PostMapping
    public ResponseEntity<CharacterPersona> createCharacter(@RequestBody CharacterPersona character) {
        return ResponseEntity.ok(characterRepo.save(character));
    }

    // Get all characters
    @GetMapping
    public ResponseEntity<List<CharacterPersona>> getAllCharacters() {
        return ResponseEntity.ok(characterRepo.findAll());
    }

    // Get specific character
    @GetMapping("/{id}")
    public ResponseEntity<CharacterPersona> getCharacter(@PathVariable Long id) {
        return ResponseEntity.of(characterRepo.findById(id));
    }

    // Delete character
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacter(@PathVariable Long id) {
        characterRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
