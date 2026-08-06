package domain.observer;

import domain.ChangeType;

public interface TaskSubject {
    void attach(TaskSubscriber subscriber);
    void detach(TaskSubscriber subscriber);
    void notifySubscribers(ChangeType changeType, String oldValue, String newValue);
}