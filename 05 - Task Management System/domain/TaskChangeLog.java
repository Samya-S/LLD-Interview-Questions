package domain;

import java.time.LocalDateTime;

public class TaskChangeLog {
    private int id;
    private int taskId;
    private int userId;
    private ChangeType changeType;
    private String oldValue;
    private String newValue;
    private LocalDateTime timestamp;

    public TaskChangeLog(int id, int taskId, int userId, ChangeType changeType, 
                        String oldValue, String newValue) {
        this.id = id;
        this.taskId = taskId;
        this.userId = userId;
        this.changeType = changeType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public int getId() { return id; }
    public int getTaskId() { return taskId; }
    public int getUserId() { return userId; }
    public ChangeType getChangeType() { return changeType; }
    public String getOldValue() { return oldValue; }
    public String getNewValue() { return newValue; }
    public LocalDateTime getTimestamp() { return timestamp; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTaskId(int taskId) { this.taskId = taskId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setChangeType(ChangeType changeType) { this.changeType = changeType; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}