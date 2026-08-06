package domain.observer;

import domain.ChangeType;

public class EmailSubscriber implements TaskSubscriber {
    private String emailService; // TODO: Replace with actual EmailService

    public EmailSubscriber(String emailService) {
        this.emailService = emailService;
    }

    @Override
    public void update(int taskId, ChangeType changeType, String oldValue, String newValue) {
        // TODO: Implement email notification logic
        // Call the repo to get all the mail id's
        // 
        System.out.println("Email notification sent for task " + taskId + 
                          ": " + changeType + " - " + oldValue + " -> " + newValue);
    }
}
