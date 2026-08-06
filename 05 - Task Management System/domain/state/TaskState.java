package domain.state;

import domain.Task;
import domain.TaskStatus;

public interface TaskState {
    boolean canTransitionTo(TaskStatus newStatus);
    void performTransition(Task task, TaskStatus newStatus);
    String getStateName();
}