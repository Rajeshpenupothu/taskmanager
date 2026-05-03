package com.example.taskmanager.controller;

import com.example.taskmanager.learning.entity.LearningTopic;
import com.example.taskmanager.service.LearningTopicService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/topics")
@CrossOrigin
public class LearningTopicController {

    private final LearningTopicService service;

    public LearningTopicController(LearningTopicService service) {
        this.service = service;
    }

    // ➕ Add topic
    @PostMapping
    public LearningTopic add(@RequestBody LearningTopic topic) {
        return service.addTopic(topic);
    }

    // 📅 Today revision
    @GetMapping("/today")
    public List<LearningTopic> today() {
        return service.getTodayTopics();
    }

    // 📊 All topics
    @GetMapping
    public List<LearningTopic> all() {
        return service.getAll();
    }

    // 🔁 Revise
    @PutMapping("/revise/{id}")
    public LearningTopic revise(
            @PathVariable Long id,
            @RequestParam(defaultValue = "NORMAL") String mode
    ) {
        return service.reviseTopic(id, mode);
    }

    // 📊 Progress
    @GetMapping("/progress")
    public Map<String, Object> progress() {
        return service.getProgress();
    }

    // 📅 Calendar
    @GetMapping("/calendar")
    public Map<LocalDate, Long> calendar() {
        return service.getCalendarData();
    }
}