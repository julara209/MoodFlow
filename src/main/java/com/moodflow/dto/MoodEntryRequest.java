package com.moodflow.dto;

import com.moodflow.model.Emotion;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class MoodEntryRequest {

    @NotNull(message = "La fecha es obligatoria")
    @PastOrPresent(message = "La fecha no puede ser futura")
    private LocalDate date;

    @NotNull(message = "La emocion es obligatoria")
    private Emotion emotion;

    @Size(max = 500, message = "La nota no puede superar 500 caracteres")
    private String note;
}
