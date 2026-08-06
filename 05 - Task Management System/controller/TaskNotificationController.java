package controller;

import service.TaskNotificationService;
import domain.TaskChangeLog;
import java.util.List;

public class TaskNotificationController {
    private TaskNotificationService taskNotificationService;

    public TaskNotificationController(TaskNotificationService taskNotificationService) {
        this.taskNotificationService = taskNotificationService;
    }

    public void subscribeToTask(int taskId, int userId) {
        taskNotificationService.subscribeToTask(taskId, userId);
    }

    public void unsubscribeFromTask(int taskId, int userId) {
        taskNotificationService.unsubscribeFromTask(taskId, userId);
    }

    public List<TaskChangeLog> getTaskHistory(int taskId) {
        return taskNotificationService.getTaskHistory(taskId);
    }
}