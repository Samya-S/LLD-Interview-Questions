package domain.state;

import domain.Task;
import domain.TaskStatus;

public class CompletedState implements TaskState {
    
    @Override
    public boolean canTransitionTo(TaskStatus newStatus) {
        // Can go to IN_PROGRESS (reopen)
        return newStatus == TaskStatus.IN_PROGRESS;
    }

    @Override
    public void performTransition(Task task, TaskStatus newStatus) {
        if (canTransitionTo(newStatus)) {
            task.setStatus(newStatus);
        } else {
            throw new InvalidStateTransitionException(
                "Cannot transition from COMPLETED to " + newStatus);
        }
    }

    @Override
    public String getStateName() {
        return "COMPLETED";
    }
}