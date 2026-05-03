package com.example.taskmanager.learning.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class LearningTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;

    private int stage; // 1,2,4,8
    private LocalDate nextRevisionDate;
    private LocalDate lastReviewedDate;

    // 🔥 NEW
    private int streak;
    private LocalDate lastActiveDate;

    public LearningTopic() {}

    public LearningTopic(String title, String description) {
        this.title = title;
        this.description = description;
        this.stage = 1;
        this.nextRevisionDate = LocalDate.now().plusDays(1);
        this.streak = 0;
    }

    public Long getId() { return id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getStage() { return stage; }
    public void setStage(int stage) { this.stage = stage; }

    public LocalDate getNextRevisionDate() { return nextRevisionDate; }
    public void setNextRevisionDate(LocalDate nextRevisionDate) {
        this.nextRevisionDate = nextRevisionDate;
    }

    public LocalDate getLastReviewedDate() { return lastReviewedDate; }
    public void setLastReviewedDate(LocalDate lastReviewedDate) {
        this.lastReviewedDate = lastReviewedDate;
    }

    public int getStreak() { return streak; }
    public void setStreak(int streak) { this.streak = streak; }

    public LocalDate getLastActiveDate() { return lastActiveDate; }
    public void setLastActiveDate(LocalDate lastActiveDate) {
        this.lastActiveDate = lastActiveDate;
    }
}