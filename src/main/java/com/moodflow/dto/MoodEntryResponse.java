package com.moodflow.dto;

import com.moodflow.model.Emotion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MoodEntryResponse {
    private Long id;
    private LocalDate date;
    private Emotion emotion;
    private Integer intensity;
    private String note;
    private LocalDateTime createdAt;
}
