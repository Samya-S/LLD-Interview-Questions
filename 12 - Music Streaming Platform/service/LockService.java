package service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LockService {
    // TODO: In production, use distributed lock (Redis, etc.)
    private Map<String, Boolean> locks = new ConcurrentHashMap<>();

    public boolean acquire(String key, long timeoutMs) {
        // Simple in-memory lock for interview
        // In production, use distributed locking with timeout
        if (locks.containsKey(key)) {
            return false; // Lock already held
        }
        locks.put(key, true);
        return true;
    }

    public void release(String key) {
        locks.remove(key);
    }
}
