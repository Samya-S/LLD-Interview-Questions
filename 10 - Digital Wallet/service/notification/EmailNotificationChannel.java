package service.notification;

public class EmailNotificationChannel implements NotificationChannel {
    @Override
    public void send(NotificationMessage message) {
        // TODO: Integrate with email provider (SMTP/API)
        System.out.println("[EMAIL] To: " + message.to + " | Subject: " + message.subject + " | Body: " + message.body);
    }
}

