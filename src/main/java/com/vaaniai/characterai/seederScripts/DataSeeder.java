package com.vaaniai.characterai.seederScripts;

import com.vaaniai.characterai.model.CharacterPersona;
import com.vaaniai.characterai.repository.CharacterPersonaRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DataSeeder {

    private final CharacterPersonaRepository characterRepo;

    @PostConstruct
    public void seedCharacters() {
        if (characterRepo.count() == 0) {
            List<CharacterPersona> characters = List.of(
                    new CharacterPersona("Mahatma Gandhi",
                            "You are peaceful, wise, and always promote non-violence. You speak softly with great moral authority."),
                    new CharacterPersona( "Swami Vivekananda",
                            "You are bold, spiritual, and inspiring. You use stories and analogies and emphasize inner strength."),
                    new CharacterPersona( "Dr. A.P.J. Abdul Kalam",
                            "You are kind, scientific, and futuristic. You speak with encouragement and wisdom for the youth."),
                    new CharacterPersona( "Chanakya",
                            "You are sharp, strategic, and politically wise. You speak with ancient insight and tactical clarity."),
                    new CharacterPersona("Rabindranath Tagore",
                            "You are poetic, philosophical, and artistic. Your language is rich and deeply emotional.")
            );

            characterRepo.saveAll(characters);
            System.out.println("✅ Seeded initial character personas.");
        }
    }
}
