package service.notification;

public class SmsNotificationChannel implements NotificationChannel {
    @Override
    public void send(NotificationMessage message) {
        // TODO: Integrate with SMS provider (e.g., Twilio)
        System.out.println("[SMS] To: " + message.to + " | Body: " + message.body);
    }
}

