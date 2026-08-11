package service.notification;

public class NotificationMessage {
    public String to;
    public String subject;
    public String body;

    public NotificationMessage(String to, String subject, String body) {
        this.to = to;
        this.subject = subject;
        this.body = body;
    }
}

