package controller;

import domain.TaskStatus;
import service.TaskStateService;

public class TaskStateController {
    private TaskStateService taskStateService;

    public TaskStateController(TaskStateService taskStateService) {
        this.taskStateService = taskStateService;
    }

    public void updateTaskStatus(int taskId, TaskStatus newStatus) {
        taskStateService.updateTaskStatus(taskId, newStatus);
    }
}