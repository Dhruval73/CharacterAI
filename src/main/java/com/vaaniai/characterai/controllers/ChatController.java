package com.vaaniai.characterai.controllers;


import com.vaaniai.characterai.DTO.ChatSessionDto;
import com.vaaniai.characterai.model.CharacterPersona;
import com.vaaniai.characterai.model.ChatSession;
import com.vaaniai.characterai.model.Message;
import com.vaaniai.characterai.model.User;
import com.vaaniai.characterai.repository.CharacterPersonaRepository;
import com.vaaniai.characterai.repository.ChatSessionRepository;
import com.vaaniai.characterai.repository.MessageRepository;
import com.vaaniai.characterai.repository.UserRepository;
import com.vaaniai.characterai.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatSessionRepository chatSessionRepo;
    private final UserRepository userRepo;
    private final CharacterPersonaRepository characterRepo;
    private final MessageRepository messageRepo;
    private final ChatService chatService;

    // 1. Create a new chat session (Authenticated via JWT)
    @PostMapping("/start")
    public ResponseEntity<?> startChat(@RequestParam Long characterId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> userOpt = userRepo.findByEmail(email);
        Optional<CharacterPersona> characterOpt = characterRepo.findById(characterId);

        if (userOpt.isEmpty() || characterOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User or CharacterPersona not found");
        }

        User user = userOpt.get();

        // Check for existing session
        Optional<ChatSession> existingSession = chatSessionRepo
                .findFirstByUserIdAndCharacterPersona_IdOrderByCreatedAtDesc(user.getId(), characterId);

        if (existingSession.isPresent()) {
            return ResponseEntity.ok(existingSession.get());
        }

        // Create new session
        ChatSession session = new ChatSession();
        session.setUser(user);
        session.setCharacterPersona(characterOpt.get());
        session.setCreatedAt(LocalDateTime.now());

        ChatSession saved = chatSessionRepo.save(session);
        return ResponseEntity.ok(saved);
    }

    // 2. Send a message in an existing session (Authenticated via JWT)
    @PostMapping("/{sessionId}/message")
    public ResponseEntity<Message> chat(
            @PathVariable Long sessionId,
            @RequestBody Map<String, String> payload) {

        // Optional: Add a check to confirm the session belongs to the logged-in user
        String userMessage = payload.get("message");
        Message response = chatService.handleUserMessage(sessionId, userMessage);
        return ResponseEntity.ok(response);
    }

    // 3. Get full message history for a session (Authenticated via JWT)
    @GetMapping("/{sessionId}/messages")
    public ResponseEntity<List<Message>> getMessages(@PathVariable Long sessionId) {
        // Optional: Add a check to confirm the session belongs to the logged-in user
        List<Message> messages = messageRepo.findByChatSessionIdOrderByTimestampAsc(sessionId);
        return ResponseEntity.ok(messages);
    }

    // 4. Reset a chat session (Authenticated via JWT)
    @DeleteMapping("/{sessionId}/reset")
    public ResponseEntity<Void> resetChat(@PathVariable Long sessionId) {
        // Optional: Add a check to confirm the session belongs to the logged-in user
        messageRepo.deleteAll(messageRepo.findByChatSessionIdOrderByTimestampAsc(sessionId));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my-sessions")
    public ResponseEntity<List<ChatSessionDto>> getMySessions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        Optional<User> userOpt = userRepo.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Long userId = userOpt.get().getId();
        List<ChatSessionDto> sessions = chatService.getSessionsByUser(userId);
        return ResponseEntity.ok(sessions);
    }

}

