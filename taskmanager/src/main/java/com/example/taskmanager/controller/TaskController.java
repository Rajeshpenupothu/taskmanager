package com.example.taskmanager.controller;
 
import com.example.taskmanager.entity.User;
import com.example.taskmanager.security.UserDetailsImpl;
import com.example.taskmanager.dto.TaskRequestDTO;
import com.example.taskmanager.dto.TaskResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.example.taskmanager.service.TaskService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import jakarta.validation.Valid;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
@RestController
@RequestMapping("/tasks")
@Tag(name = "Tasks", description = "Task management APIs")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {
	//TaskController → needs TaskService
	//{constructor based dependency injection}
	private final TaskService taskService;
	public TaskController(TaskService taskService) {
		this.taskService=taskService;
	}

	private User getCurrentUser(Authentication authentication) {
	    UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
	    return userDetails.getUser();
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get task by ID")
	public ResponseEntity<TaskResponseDTO> getTaskById(@PathVariable Long id, Authentication authentication) {
	    User user = getCurrentUser(authentication);
	    return ResponseEntity.ok(taskService.getTaskById(id, user));
	}
	@PostMapping
	@Operation(summary = "Create a new task")
	public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskRequestDTO taskRequest, Authentication authentication) {
	    User user = getCurrentUser(authentication);
	    return ResponseEntity.ok(taskService.saveTask(taskRequest, user));
	}
	@PutMapping("/{id}")
	@Operation(summary = "Update an existing task")
	public ResponseEntity<TaskResponseDTO> updateTask(@PathVariable Long id,@Valid @RequestBody TaskRequestDTO updateTask, Authentication authentication){
	    User user = getCurrentUser(authentication);
		return ResponseEntity.ok(taskService.updateTask(id, updateTask, user));
	}
	@DeleteMapping("/{id}")
	@Operation(summary = "Delete a task")
	public ResponseEntity<Void> deleteTask(@PathVariable Long id, Authentication authentication) {
	    User user = getCurrentUser(authentication);
	    taskService.deleteTask(id, user);
	    return ResponseEntity.noContent().build();
	}
	@GetMapping
	@Operation(summary = "Get all tasks for current user")
	public ResponseEntity<Page<TaskResponseDTO>> getAllTasks(Authentication authentication, Pageable pageable) {
	    User user = getCurrentUser(authentication);
	    return ResponseEntity.ok(taskService.getAllTasks(user, pageable));
	}
}
