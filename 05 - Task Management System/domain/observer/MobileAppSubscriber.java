package domain.observer;

import domain.ChangeType;

public class MobileAppSubscriber implements TaskSubscriber {
    private String pushNotificationService; // TODO: Replace with actual PushNotificationService

    public MobileAppSubscriber(String pushNotificationService) {
        this.pushNotificationService = pushNotificationService;
    }

    @Override
    public void update(int taskId, ChangeType changeType, String oldValue, String newValue) {
        // TODO: Implement push notification logic
        // Find out who subscribed to it, and send them.. 
        System.out.println("Push notification sent for task " + taskId + 
                          ": " + changeType + " - " + oldValue + " -> " + newValue);
    }
}