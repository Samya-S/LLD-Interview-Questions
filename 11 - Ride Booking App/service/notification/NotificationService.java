package service.notification;

import domain.NotificationMessage;

public class NotificationService {
    public void send(NotificationMessage message) {
        System.out.println("[Notification] to=" + message.getTo() + " subject=" + message.getSubject() + " body="
                + message.getBody());
    }
}