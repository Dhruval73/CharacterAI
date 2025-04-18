package com.vaaniai.characterai.service;

import com.vaaniai.characterai.DTO.ChatSessionDto;
import com.vaaniai.characterai.model.CharacterPersona;
import com.vaaniai.characterai.model.ChatSession;
import com.vaaniai.characterai.model.Message;
import com.vaaniai.characterai.model.User;
import com.vaaniai.characterai.repository.CharacterPersonaRepository;
import com.vaaniai.characterai.repository.ChatSessionRepository;
import com.vaaniai.characterai.repository.MessageRepository;
import com.vaaniai.characterai.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ChatService {

    private final UserRepository userRepository;
    private final CharacterPersonaRepository characterRepo;
    private final ChatSessionRepository sessionRepo;
    private final MessageRepository messageRepo;
    private final GeminiService geminiService;

    public Message handleUserMessage(Long sessionId, String userMessageText) {
        ChatSession session = sessionRepo.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        // 🚨 Validate that the session belongs to the currently logged-in user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();  // email is used as username in Spring Security

        User loggedInUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!session.getUser().getId().equals(loggedInUser.getId())) {
            throw new RuntimeException("You are not authorized to access this chat session.");
        }

        // 1. Save user message
        Message userMessage = new Message();
        userMessage.setRole("user");
        userMessage.setContent(userMessageText);
        userMessage.setTimestamp(LocalDateTime.now());
        userMessage.setChatSession(session);
        messageRepo.save(userMessage);

        // 2. Get full message history
        List<Message> history = messageRepo.findByChatSessionIdOrderByTimestampAsc(sessionId);

        // 3. Build prompt
        String prompt = buildPrompt(session.getCharacterPersona(), history, userMessageText);

        // 4. Get character response from Gemini
        String aiResponse = geminiService.getResponse(prompt);

        // 5. Save character message
        Message characterMessage = new Message();
        characterMessage.setRole("character");
        characterMessage.setContent(aiResponse);
        characterMessage.setTimestamp(LocalDateTime.now());
        characterMessage.setChatSession(session);
        messageRepo.save(characterMessage);

        return characterMessage;
    }

    private String buildPrompt(CharacterPersona character, List<Message> history, String latestUserMessage) {
        int maxMessages = 10;

        StringBuilder prompt = new StringBuilder();
        prompt.append(String.format("You are %s. %s\n", character.getName(), character.getPersonaDescription()));
        prompt.append("Respond in a way that's aligned with this character.\n\n");
        prompt.append("Conversation:\n");

        int start = Math.max(history.size() - maxMessages, 0);
        List<Message> recent = history.subList(start, history.size());

        for (Message msg : recent) {
            String prefix = msg.getRole().equals("user") ? "User: " : character.getName() + ": ";
            prompt.append(prefix).append(msg.getContent()).append("\n");
        }

        prompt.append("User: ").append(latestUserMessage).append("\n");
        prompt.append(character.getName()).append(":");

        return prompt.toString();
    }

    public List<ChatSessionDto> getSessionsByUser(Long userId) {
        List<ChatSession> sessions = sessionRepo.findByUserId(userId);

        return sessions.stream().map(session -> {
            String characterName = session.getCharacterPersona().getName();
            LocalDateTime createdAt = session.getCreatedAt();

            String lastMessage = session.getMessages() != null && !session.getMessages().isEmpty()
                    ? session.getMessages().get(session.getMessages().size() - 1).getContent()
                    : "";

            return new ChatSessionDto(session.getId(), characterName, createdAt, lastMessage);
        }).collect(Collectors.toList());
    }
}
