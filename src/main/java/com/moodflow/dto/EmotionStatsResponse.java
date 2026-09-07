package com.moodflow.dto;

import com.moodflow.model.Emotion;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class EmotionStatsResponse {

    // Ej: "Tu emocion mas registrada este mes es FELIZ"
    private Emotion emotionMasFrecuente;
    private long totalRegistros;

    // Conteo por cada emocion, ej: { FELIZ: 5, TRISTE: 2, MOTIVADO: 3 }
    private Map<Emotion, Long> conteoPorEmocion;
}
