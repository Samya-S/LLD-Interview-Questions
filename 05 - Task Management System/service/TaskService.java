package service;

import domain.Task;
import domain.TaskSearchCriteria;
import domain.ChangeType;
import domain.strategy.TaskSortingContext;
import domain.strategy.PrioritySortingStrategy;
import domain.strategy.DueDateSortingStrategy;
import domain.strategy.CreatedDateSortingStrategy;
import repository.TaskRepository;
import java.time.LocalDateTime;
import java.util.List;

public class TaskService {
    private TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public Task createTask(Task task) {
        // TODO: Validate user permissions
        // TODO: Validate parent task hierarchy if parentTaskId exists
        
        Task savedTask = taskRepository.save(task);
        
        // Notify observers about task creation
        savedTask.notifySubscribers(ChangeType.CREATED, "", savedTask.getTitle());
        
        return savedTask;
    }

    public Task updateTask(int taskId, Task updatedTask) {
        Task existingTask = taskRepository.findById(taskId);
        if (existingTask == null) {
            throw new RuntimeException("Task not found");
        }

        // TODO: Validate user permissions
        // TODO: Check state transitions

        // TODO: Check if the old value is same as the task's current value 
        // Check for stale task.
        // If stale, throw an exception.
        // else update the task.
        
        // Store old values for notification
        String oldTitle = existingTask.getTitle();
        String oldPriority = existingTask.getPriority().toString();
        
        // Update task
        updatedTask.setId(taskId);
        updatedTask.setUpdatedAt(LocalDateTime.now());
        Task savedTask = taskRepository.save(updatedTask);
        
        // Notify observers about task update
        savedTask.notifySubscribers(ChangeType.UPDATED, oldTitle, savedTask.getTitle());
        
        // TODO: Update subtask priorities if needed
        savedTask.updateSubtaskPriorities();
        
        return savedTask;
    }

    public void deleteTask(int taskId) {
        Task task = taskRepository.findById(taskId);
        if (task == null) {
            throw new RuntimeException("Task not found");
        }

        // TODO: Validate user permissions
        // TODO: Handle subtasks (cascade delete or prevent deletion)
        
        taskRepository.delete(taskId);
    }

    public List<Task> searchTasks(TaskSearchCriteria criteria) {
        // Get tasks from repository
        List<Task> tasks = taskRepository.search(criteria);
        
        // Apply sorting strategy based on criteria
        TaskSortingContext sortingContext = new TaskSortingContext();
        
        String sortBy = criteria.getSortBy();
        if ("dueDate".equals(sortBy)) {
            sortingContext.setSortingStrategy(new DueDateSortingStrategy());
        } else if ("createdDate".equals(sortBy)) {
            sortingContext.setSortingStrategy(new CreatedDateSortingStrategy());
        } else {
            // Default to priority sorting
            sortingContext.setSortingStrategy(new PrioritySortingStrategy());
        }
        
        // Apply sorting
        List<Task> sortedTasks = sortingContext.sortTasks(tasks);
        
        // Reverse if descending order is requested
        if ("asc".equals(criteria.getSortOrder())) {
            java.util.Collections.reverse(sortedTasks);
        }
        
        return sortedTasks;
    }

    public Task addSubtask(int parentTaskId, Task subtask) {
        Task parentTask = taskRepository.findById(parentTaskId);
        if (parentTask == null) {
            throw new RuntimeException("Parent task not found");
        }

        // TODO: Validate user permissions
        // TODO: Validate subtask hierarchy
        
        subtask.setParentTaskId(parentTaskId);
        subtask.setCreatorId(parentTask.getCreatorId());
        
        Task savedSubtask = taskRepository.save(subtask);
        
        // Notify observers about subtask creation
        savedSubtask.notifySubscribers(ChangeType.CREATED, "", savedSubtask.getTitle());
        
        return savedSubtask;
    }
}



