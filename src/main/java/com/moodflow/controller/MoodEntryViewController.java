package com.moodflow.controller;

import com.moodflow.dto.MoodEntryRequest;
import com.moodflow.dto.MoodEntryResponse;
import com.moodflow.model.Emotion;
import com.moodflow.service.MoodEntryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/entries")
@RequiredArgsConstructor
public class MoodEntryViewController {

    private final MoodEntryService service;

    @GetMapping
    public String list(@RequestParam(required = false) Emotion emotion, Model model) {
        model.addAttribute("entries", emotion != null ? service.findByEmotion(emotion) : service.findAll());
        model.addAttribute("selectedEmotion", emotion != null ? emotion.name() : null);
        return "index";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("moodEntryRequest", new MoodEntryRequest());
        model.addAttribute("isEdit", false);
        return "form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("moodEntryRequest") MoodEntryRequest request,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "form";
        }
        service.create(request);
        return "redirect:/entries";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        MoodEntryResponse existing = service.findById(id);

        MoodEntryRequest request = new MoodEntryRequest();
        request.setDate(existing.getDate());
        request.setEmotion(existing.getEmotion());
        request.setIntensity(existing.getIntensity());
        request.setNote(existing.getNote());

        model.addAttribute("moodEntryRequest", request);
        model.addAttribute("entryId", id);
        model.addAttribute("isEdit", true);
        return "form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                          @Valid @ModelAttribute("moodEntryRequest") MoodEntryRequest request,
                          BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("entryId", id);
            model.addAttribute("isEdit", true);
            return "form";
        }
        service.update(id, request);
        return "redirect:/entries";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id) {
        service.delete(id);
        return "redirect:/entries";
    }

    @GetMapping("/stats")
    public String stats(Model model) {
        // Nota: esto lanzara un error 500 hasta que completes
        // MoodEntryService.getMonthlyStats() (ver el TODO en esa clase).
        model.addAttribute("stats", service.getMonthlyStats());
        return "stats";
    }
}
