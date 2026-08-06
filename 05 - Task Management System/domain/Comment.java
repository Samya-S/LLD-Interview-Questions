package domain;

import java.time.LocalDateTime;

public class Comment {
    private int id;
    private int taskId;
    private int userId;
    private String content;
    private LocalDateTime createdAt;

    public Comment(int id, int taskId, int userId, String content) {
        this.id = id;
        this.taskId = taskId;
        this.userId = userId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public int getId() { return id; }
    public int getTaskId() { return taskId; }
    public int getUserId() { return userId; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setTaskId(int taskId) { this.taskId = taskId; }
    public void setUserId(int userId) { this.userId = userId; }
    public void setContent(String content) { this.content = content; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
