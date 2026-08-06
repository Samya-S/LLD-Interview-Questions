package controller;

import service.TaskAssignmentService;

public class TaskAssignmentController {
    private TaskAssignmentService taskAssignmentService;

    public TaskAssignmentController(TaskAssignmentService taskAssignmentService) {
        this.taskAssignmentService = taskAssignmentService;
    }

    public void assignTask(int taskId, int assigneeId) {
        taskAssignmentService.assignTask(taskId, assigneeId);
    }
}