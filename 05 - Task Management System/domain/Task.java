package domain;

import domain.observer.EmailSubscriber;
import domain.observer.TaskSubscriber;
import domain.observer.TaskSubject;
import domain.state.TaskState;
import domain.state.TodoState;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Task implements TaskSubject {
    private int id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private Priority priority;
    private TaskStatus status;
    private int assigneeId;
    private int creatorId;
    private Integer parentTaskId;
    private List<String> tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Observer Pattern - List of subscribers
    private List<TaskSubscriber> subscribers;
    
    // State Pattern - Current state
    private TaskState currentState;

    public Task(int id, String title, String description, LocalDateTime dueDate, 
                Priority priority, int creatorId) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.priority = priority;
        this.creatorId = creatorId;
        this.status = TaskStatus.TODO;
        this.tags = new ArrayList<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.subscribers = new ArrayList<>();
        this.currentState = new TodoState();
        subscribers.add(new EmailSubscriber("emailService"));
    }

    // Observer Pattern Methods
    public void attach(TaskSubscriber subscriber) {
        if (!subscribers.contains(subscriber)) {
            subscribers.add(subscriber);
        }
    }

    public void detach(TaskSubscriber subscriber) {
        subscribers.remove(subscriber);
    }

    public void notifySubscribers(ChangeType changeType, String oldValue, String newValue) {
        for (TaskSubscriber subscriber : subscribers) {
            subscriber.update(this.id, changeType, oldValue, newValue);
        }
    }

    // State Pattern Methods
    public void setState(TaskState newState) {
        this.currentState = newState;
    }

    public TaskState getCurrentState() {
        return currentState;
    }

    // Recursive Subtask Methods
    public List<Task> getSubtasks() {
        // TODO: Implement database query to get immediate subtasks
        return new ArrayList<>();
    }

    public List<Task> getAllSubtasks() {
        // TODO: Implement recursive query to get all nested subtasks
        return new ArrayList<>();
    }

    public boolean hasSubtasks() {
        // TODO: Implement check for subtasks
        return false;
    }

    public int getSubtaskCount() {
        // TODO: Implement count of subtasks
        return 0;
    }

    public void updateSubtaskPriorities() {
        // TODO: Implement recursive priority update logic
    }

    // Getters
    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getDueDate() { return dueDate; }
    public Priority getPriority() { return priority; }
    public TaskStatus getStatus() { return status; }
    public int getAssigneeId() { return assigneeId; }
    public int getCreatorId() { return creatorId; }
    public Integer getParentTaskId() { return parentTaskId; }
    public List<String> getTags() { return tags; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
    public void setPriority(Priority priority) { this.priority = priority; }
    public void setStatus(TaskStatus status) { this.status = status; }
    public void setAssigneeId(int assigneeId) { this.assigneeId = assigneeId; }
    public void setCreatorId(int creatorId) { this.creatorId = creatorId; }
    public void setParentTaskId(Integer parentTaskId) { this.parentTaskId = parentTaskId; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}

