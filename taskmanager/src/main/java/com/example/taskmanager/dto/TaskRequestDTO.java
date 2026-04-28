package com.example.taskmanager.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDTO {

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @Size(min = 5, message = "Description must be at least 5 characters")
    private String description;

    @NotBlank(message = "Status is required")
    private String status;
}