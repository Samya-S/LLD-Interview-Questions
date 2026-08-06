package domain;

public class TaskSubscription {
    private int id;
    private int userId;
    private int taskId;
    private boolean isActive;

    public TaskSubscription(int id, int userId, int taskId) {
        this.id = id;
        this.userId = userId;
        this.taskId = taskId;
        this.isActive = true;
    }

    // Getters
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public int getTaskId() { return taskId; }
    public boolean isActive() { return isActive; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setTaskId(int taskId) { this.taskId = taskId; }
    public void setActive(boolean active) { isActive = active; }
}