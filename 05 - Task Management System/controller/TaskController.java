package controller;

import domain.Task;
import domain.TaskSearchCriteria;
import service.TaskService;
import java.time.LocalDateTime;
import java.util.List;

public class TaskController {
    private TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    public Task createTask(String title, String description, LocalDateTime dueDate, 
                          String priority, int creatorId) {
        // TODO: Validate input parameters
        // TODO: Convert priority string to Priority enum
        
        Task task = new Task(0, title, description, dueDate, 
                           domain.Priority.MEDIUM, creatorId); // Default priority
        
        return taskService.createTask(task);
    }

    public Task updateTask(int taskId, String title, String description, 
                          LocalDateTime dueDate, String priority) {
        // TODO: Validate input parameters
        // TODO: Convert priority string to Priority enum
        
        Task updatedTask = new Task(taskId, title, description, dueDate, 
                                  domain.Priority.MEDIUM, 0); // Default values
        
        return taskService.updateTask(taskId, updatedTask);
    }

    public void deleteTask(int taskId) {
        taskService.deleteTask(taskId);
    }

    public List<Task> searchTasks(TaskSearchCriteria criteria) {
        return taskService.searchTasks(criteria);
    }

    public Task addSubtask(int parentTaskId, String title, String description, 
                          LocalDateTime dueDate, String priority, int creatorId) {
        // TODO: Validate input parameters
        
        Task subtask = new Task(0, title, description, dueDate, 
                              domain.Priority.MEDIUM, creatorId);
        
        return taskService.addSubtask(parentTaskId, subtask);
    }
}


