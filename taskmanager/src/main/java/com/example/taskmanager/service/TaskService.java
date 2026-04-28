package com.example.taskmanager.service;
import com.example.taskmanager.entity.Task;
import org.springframework.stereotype.Service;
import com.example.taskmanager.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.entity.User;
import com.example.taskmanager.dto.TaskRequestDTO;
import com.example.taskmanager.dto.TaskResponseDTO;
import com.example.taskmanager.dto.UserDTO;
@Service
public class TaskService {
	//TaskService → needs TaskRepository
	private final TaskRepository taskRepository;
	public TaskService(TaskRepository taskRepository) {
		this.taskRepository = taskRepository;
		}
	public TaskResponseDTO saveTask(TaskRequestDTO taskRequest, User user) {
        Task task = new Task();
        task.setTitle(taskRequest.getTitle());
        task.setDescription(taskRequest.getDescription());
        task.setStatus(taskRequest.getStatus());
        task.setUser(user);

        Task saved = taskRepository.save(task);
        return convertToDTO(saved);
    }
	public TaskResponseDTO getTaskById(Long id, User user) {
	    Task task = taskRepository.findById(id)
	        .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + id));

	    if (!task.getUser().getId().equals(user.getId())) {
	        throw new ResourceNotFoundException("Task not found");
	    }

	    return convertToDTO(task);
	}
	public TaskResponseDTO updateTask(Long id, TaskRequestDTO updatedTask, User user) {
	    Task existing = taskRepository.findById(id)
	            .orElseThrow(() -> new ResourceNotFoundException("Task not found with id "+id));

	    if (!existing.getUser().getId().equals(user.getId())) {
	        throw new ResourceNotFoundException("Task not found");
	    }

	    existing.setTitle(updatedTask.getTitle());
	    existing.setDescription(updatedTask.getDescription());
	    existing.setStatus(updatedTask.getStatus());

	    Task saved = taskRepository.save(existing);
	    return convertToDTO(saved);
	}
	public void deleteTask(Long id, User user) {
	    Task task = taskRepository.findById(id)
	        .orElseThrow(() -> new ResourceNotFoundException("Task not found with id: "+id));

	    if (!task.getUser().getId().equals(user.getId())) {
	        throw new ResourceNotFoundException("Task not found");
	    }

	    taskRepository.delete(task);
	}
	public Page<TaskResponseDTO> getAllTasks(User user, Pageable pageable) {
	    Page<Task> tasks = taskRepository.findByUser(user, pageable);
	    return tasks.map(this::convertToDTO);
	}

	private TaskResponseDTO convertToDTO(Task task) {
	    UserDTO userDTO = new UserDTO(task.getUser().getId(), task.getUser().getUsername(), task.getUser().getEmail());
	    return new TaskResponseDTO(task.getId(), task.getTitle(), task.getDescription(), task.getStatus(), userDTO);
	}
}
