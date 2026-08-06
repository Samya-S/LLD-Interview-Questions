package service;

import domain.Task;
import domain.TaskStatus;
import domain.state.TaskState;
import domain.state.TodoState;
import domain.state.InProgressState;
import domain.state.ReviewState;
import domain.state.CompletedState;
import domain.state.CancelledState;
import domain.ChangeType;
import repository.TaskRepository;
import java.util.HashMap;
import java.util.Map;

public class TaskStateService {
    private TaskRepository taskRepository;
    private Map<TaskStatus, TaskState> stateMap;

    public TaskStateService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
        initializeStates();
    }

    private void initializeStates() {
        stateMap = new HashMap<>();
        stateMap.put(TaskStatus.TODO, new TodoState());
        stateMap.put(TaskStatus.IN_PROGRESS, new InProgressState());
        stateMap.put(TaskStatus.REVIEW, new ReviewState());
        stateMap.put(TaskStatus.COMPLETED, new CompletedState());
        stateMap.put(TaskStatus.CANCELLED, new CancelledState());
    }

    public void updateTaskStatus(int taskId, TaskStatus newStatus) {
        Task task = taskRepository.findById(taskId);
        if (task == null) {
            throw new RuntimeException("Task not found");
        }

        TaskState currentState = stateMap.get(task.getStatus());
        if (currentState == null) {
            throw new RuntimeException("Invalid current state");
        }

        if (currentState.canTransitionTo(newStatus)) {
            String oldStatus = task.getStatus().toString();
            
            // Perform state transition
            currentState.performTransition(task, newStatus);
            
            // Update task in repository
            taskRepository.save(task);
            
            // Notify observers about status change
            task.notifySubscribers(ChangeType.STATUS_CHANGED, oldStatus, newStatus.toString());
        } else {
            throw new RuntimeException("Invalid state transition from " + task.getStatus() + " to " + newStatus);
        }
    }

    public boolean isValidTransition(TaskStatus currentStatus, TaskStatus newStatus) {
        TaskState currentState = stateMap.get(currentStatus);
        if (currentState == null) {
            return false;
        }
        return currentState.canTransitionTo(newStatus);
    }
}



