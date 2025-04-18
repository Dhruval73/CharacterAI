package com.vaaniai.characterai.repository;

import com.vaaniai.characterai.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatSessionIdOrderByTimestampAsc(Long chatSessionId);
}
