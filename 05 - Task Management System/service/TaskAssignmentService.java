package service;

import domain.Task;
import domain.ChangeType;
import repository.TaskRepository;
import repository.UserRepository;

public class TaskAssignmentService {
    private TaskRepository taskRepository;
    private UserRepository userRepository;

    public TaskAssignmentService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public void assignTask(int taskId, int assigneeId) {
        // Validate assignee exists
        if (userRepository.findById(assigneeId) == null) {
            throw new IllegalArgumentException("Assignee with ID " + assigneeId + " not found");
        }

        // Get task and update assignment
        Task task = taskRepository.findById(taskId);
        if (task == null) {
            throw new IllegalArgumentException("Task with ID " + taskId + " not found");
        }

        int oldAssigneeId = task.getAssigneeId();
        task.setAssigneeId(assigneeId);
        
        // Save updated task
        taskRepository.save(task);
        
        // Notify subscribers of assignment change
        task.notifySubscribers(ChangeType.ASSIGNED, 
            String.valueOf(oldAssigneeId), String.valueOf(assigneeId));
    }
}