package service;

import domain.*;
import repository.*;
import java.util.List;

public class TaskNotificationService {
    private TaskSubscriptionRepository taskSubscriptionRepository;
    private TaskChangeLogRepository taskChangeLogRepository;

    public TaskNotificationService(TaskSubscriptionRepository taskSubscriptionRepository, 
                                 TaskChangeLogRepository taskChangeLogRepository) {
        this.taskSubscriptionRepository = taskSubscriptionRepository;
        this.taskChangeLogRepository = taskChangeLogRepository;
    }

    public void subscribeToTask(int taskId, int userId) {
        TaskSubscription subscription = new TaskSubscription(0, userId, taskId);
        taskSubscriptionRepository.save(subscription);
    }

    public void unsubscribeFromTask(int taskId, int userId) {
        // TODO: Implement unsubscribe logic
        // This would typically mark the subscription as inactive
    }

    public void notifySubscribers(int taskId, ChangeType changeType, String oldValue, String newValue) {
        // TODO: Implement notification logic
        // This would get all active subscribers and notify them
    }

    public List<TaskChangeLog> getTaskHistory(int taskId) {
        return taskChangeLogRepository.findByTaskId(taskId);
    }
}