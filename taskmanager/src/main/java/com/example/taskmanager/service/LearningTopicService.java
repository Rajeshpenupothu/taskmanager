package com.example.taskmanager.service;

import com.example.taskmanager.learning.entity.LearningTopic;
import com.example.taskmanager.learning.repository.LearningTopicRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LearningTopicService {

    private final LearningTopicRepository repo;

    public LearningTopicService(LearningTopicRepository repo) {
        this.repo = repo;
    }

    // ➕ Add topic
    public LearningTopic addTopic(LearningTopic topic) {
        topic.setStage(1);
        topic.setNextRevisionDate(LocalDate.now().plusDays(1));
        return repo.save(topic);
    }

    // 📅 Today revision
    public List<LearningTopic> getTodayTopics() {
        return repo.findByNextRevisionDate(LocalDate.now());
    }

    // 📊 All topics
    public List<LearningTopic> getAll() {
        return repo.findAll();
    }

    // 🔁 REVISION LOGIC (HARD / NORMAL / EASY)
    public LearningTopic reviseTopic(Long id, String mode) {
        LearningTopic topic = repo.findById(id).orElseThrow();

        int stage = topic.getStage();

        switch (mode) {

            case "HARD":
                topic.setNextRevisionDate(LocalDate.now().plusDays(1));
                break;

            case "EASY":
                if (stage == 1) stage = 4;
                else if (stage == 2) stage = 8;
                else if (stage == 4) stage = 8;

                topic.setStage(stage);
                topic.setNextRevisionDate(LocalDate.now().plusDays(stage));
                break;

            default: // NORMAL
                if (stage == 1) stage = 2;
                else if (stage == 2) stage = 4;
                else if (stage == 4) stage = 8;

                topic.setStage(stage);
                topic.setNextRevisionDate(LocalDate.now().plusDays(stage));
        }

        // 📅 update review date
        topic.setLastReviewedDate(LocalDate.now());

        // 🔥 streak logic
        LocalDate today = LocalDate.now();

        if (topic.getLastActiveDate() == null ||
            topic.getLastActiveDate().isEqual(today.minusDays(1))) {

            topic.setStreak(topic.getStreak() + 1);

        } else if (!topic.getLastActiveDate().isEqual(today)) {
            topic.setStreak(1);
        }

        topic.setLastActiveDate(today);

        return repo.save(topic);
    }

    // 📊 PROGRESS
    public Map<String, Object> getProgress() {
        List<LearningTopic> all = repo.findAll();

        long total = all.size();
        long completed = all.stream().filter(t -> t.getStage() == 8).count();
        long inProgress = total - completed;

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("completed", completed);
        result.put("inProgress", inProgress);

        return result;
    }

    // 📅 CALENDAR DATA
    public Map<LocalDate, Long> getCalendarData() {
        List<LearningTopic> all = repo.findAll();

        return all.stream()
                .filter(t -> t.getLastReviewedDate() != null)
                .collect(Collectors.groupingBy(
                        LearningTopic::getLastReviewedDate,
                        Collectors.counting()
                ));
    }
}