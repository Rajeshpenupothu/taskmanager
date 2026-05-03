package com.example.taskmanager.learning.repository;

import com.example.taskmanager.learning.entity.LearningTopic;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface LearningTopicRepository extends JpaRepository<LearningTopic, Long> {

    List<LearningTopic> findByNextRevisionDate(LocalDate date);
}