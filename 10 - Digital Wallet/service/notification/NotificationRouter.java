package service.notification;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationRouter {
    private final Map<String, NotificationChannel> channels = new ConcurrentHashMap<>();

    public void register(String channelName, NotificationChannel channel) {
        channels.put(channelName, channel);
    }

    public void send(String channelName, NotificationMessage message) {
        // whatsapp
        // email
        // push
        // sms
        NotificationChannel channel = channels.get(channelName);
        if (channel == null) {
            throw new IllegalArgumentException("Notification channel not registered: " + channelName);
        }
        channel.send(message);
    }
}
