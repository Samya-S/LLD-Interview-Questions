package domain.observer;

import domain.ChangeType;

public interface TaskSubscriber {
    void update(int taskId, ChangeType changeType, String oldValue, String newValue);
}

