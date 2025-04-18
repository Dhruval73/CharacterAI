package com.vaaniai.characterai.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ChatSessionDto {
    private Long id;
    private String characterName;
    private LocalDateTime createdAt;
    private String lastMessagePreview;
}
