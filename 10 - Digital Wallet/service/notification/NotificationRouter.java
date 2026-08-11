package service.notification;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NotificationRouter {
    private final Map<String, NotificationChannel> channels = new ConcurrentHashMap<>();

    public void register(String channelName, NotificationChannel channel) {
        channels.put(channelName, channel);
    }

    public void send(String type, NotificationMessage message) {
        // whatsapp 
        // email 
        // push 
        // sms
    }
}

