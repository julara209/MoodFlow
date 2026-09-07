package com.moodflow.service;

import com.moodflow.dto.EmotionStatsResponse;
import com.moodflow.dto.MoodEntryRequest;
import com.moodflow.dto.MoodEntryResponse;
import com.moodflow.exception.ResourceNotFoundException;
import com.moodflow.model.Emotion;
import com.moodflow.model.MoodEntry;
import com.moodflow.repository.MoodEntryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MoodEntryService {

    private final MoodEntryRepository repository;

    public MoodEntryResponse create(MoodEntryRequest request) {
        MoodEntry entry = MoodEntry.builder()
                .date(request.getDate())
                .emotion(request.getEmotion())
                .intensity(request.getIntensity())
                .note(request.getNote())
                .build();

        MoodEntry saved = repository.save(entry);
        return toResponse(saved);
    }

    public List<MoodEntryResponse> findAll() {
        return repository.findAllByOrderByDateDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public MoodEntryResponse findById(Long id) {
        MoodEntry entry = getEntityOrThrow(id);
        return toResponse(entry);
    }

    public List<MoodEntryResponse> findByEmotion(Emotion emotion) {
        return repository.findByEmotion(emotion)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public MoodEntryResponse update(Long id, MoodEntryRequest request) {
        MoodEntry entry = getEntityOrThrow(id);

        entry.setDate(request.getDate());
        entry.setEmotion(request.getEmotion());
        entry.setIntensity(request.getIntensity());
        entry.setNote(request.getNote());

        MoodEntry updated = repository.save(entry);
        return toResponse(updated);
    }

    public void delete(Long id) {
        MoodEntry entry = getEntityOrThrow(id);
        repository.delete(entry);
    }

    public EmotionStatsResponse getMonthlyStats() {
        // 1. Rango del mes actual
        LocalDate hoy = LocalDate.now();
        LocalDate inicioMes = hoy.withDayOfMonth(1);
        LocalDate finMes = hoy.withDayOfMonth(hoy.lengthOfMonth());

        // 2. Registros de ese rango
        List<MoodEntry> registrosDelMes = repository.findByDateBetween(inicioMes, finMes);

        // 3. Agrupar y contar por emocion
        Map<Emotion, Long> conteo = registrosDelMes.stream()
                .collect(Collectors.groupingBy(MoodEntry::getEmotion, Collectors.counting()));

        // 4. Emocion con mayor conteo (null si no hay registros este mes)
        Emotion masFrecuente = conteo.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        // 5. Armar la respuesta, incluyendo la racha actual
        return EmotionStatsResponse.builder()
                .emotionMasFrecuente(masFrecuente)
                .totalRegistros(registrosDelMes.size())
                .conteoPorEmocion(conteo)
                .rachaActualDias(calcularRachaActual())
                .build();
    }

    /**
     * Calcula cuantos dias consecutivos tiene el usuario registrando alguna
     * emocion, contando hacia atras desde hoy.
     *
     * Reglas:
     * - Si el ultimo registro es de hoy o de ayer, la racha sigue viva.
     * - Si el ultimo registro es de hace 2 dias o mas, la racha esta rota (0).
     * - Un dia con varios registros solo cuenta una vez.
     */
    private int calcularRachaActual() {
        List<LocalDate> fechasUnicas = repository.findAll().stream()
                .map(MoodEntry::getDate)
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();

        if (fechasUnicas.isEmpty()) {
            return 0;
        }

        LocalDate hoy = LocalDate.now();
        LocalDate masReciente = fechasUnicas.get(0);

        boolean rachaViva = masReciente.equals(hoy) || masReciente.equals(hoy.minusDays(1));
        if (!rachaViva) {
            return 0;
        }

        int racha = 1;
        LocalDate fechaEsperada = masReciente.minusDays(1);

        for (int i = 1; i < fechasUnicas.size(); i++) {
            if (fechasUnicas.get(i).equals(fechaEsperada)) {
                racha++;
                fechaEsperada = fechaEsperada.minusDays(1);
            } else {
                break;
            }
        }

        return racha;
    }

    // ---- helpers privados ----

    private MoodEntry getEntityOrThrow(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe un registro con id " + id));
    }

    /*
     * TODO (tuyo): revisa que este mapeo tenga todos los campos que necesitas exponer.
     * Si mas adelante agregas campos a MoodEntry, recuerda actualizarlos aqui tambien.
     */
    private MoodEntryResponse toResponse(MoodEntry entry) {
        return MoodEntryResponse.builder()
                .id(entry.getId())
                .date(entry.getDate())
                .emotion(entry.getEmotion())
                .intensity(entry.getIntensity())
                .note(entry.getNote())
                .createdAt(entry.getCreatedAt())
                .build();
    }
}
