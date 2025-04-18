package com.vaaniai.characterai.repository;

import com.vaaniai.characterai.model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {
    List<ChatSession> findByUserId(Long userId);
    List<ChatSession> findByCharacterPersonaId(Long characterPersonaId);

    Optional<ChatSession> findFirstByUserIdAndCharacterPersona_IdOrderByCreatedAtDesc(Long userId, Long characterId);
}