package com.moodflow.controller;

import com.moodflow.dto.EmotionStatsResponse;
import com.moodflow.dto.MoodEntryRequest;
import com.moodflow.dto.MoodEntryResponse;
import com.moodflow.model.Emotion;
import com.moodflow.service.MoodEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mood-entries")
@RequiredArgsConstructor
public class MoodEntryController {

    private final MoodEntryService service;

    @PostMapping
    public ResponseEntity<MoodEntryResponse> create(@Valid @RequestBody MoodEntryRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    // GET /api/mood-entries              -> todo el historial
    // GET /api/mood-entries?emotion=FELIZ -> filtrado por emocion
    @GetMapping
    public ResponseEntity<List<MoodEntryResponse>> findAll(
            @RequestParam(required = false) Emotion emotion) {

        if (emotion != null) {
            return ResponseEntity.ok(service.findByEmotion(emotion));
        }
        return ResponseEntity.ok(service.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MoodEntryResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MoodEntryResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody MoodEntryRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats/monthly")
    public ResponseEntity<EmotionStatsResponse> getMonthlyStats() {
        return ResponseEntity.ok(service.getMonthlyStats());
    }
}
