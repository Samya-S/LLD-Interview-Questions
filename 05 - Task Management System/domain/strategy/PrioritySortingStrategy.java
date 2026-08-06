package domain.strategy;

import domain.Priority;
import domain.Task;
import java.util.Comparator;
import java.util.List;

public class PrioritySortingStrategy implements TaskSortingStrategy {
    
    @Override
    public List<Task> sort(List<Task> tasks) {
        // TODO: Implement priority-based sorting (URGENT -> HIGH -> MEDIUM -> LOW)
        List<Task> mutableTasks = new java.util.ArrayList<>(tasks);
        mutableTasks.sort(Comparator.comparing(Task::getPriority, 
            (p1, p2) -> p2.ordinal() - p1.ordinal())); // Higher priority first
        return mutableTasks;
    }

    @Override
    public String getStrategyName() {
        return "PRIORITY";
    }
}

