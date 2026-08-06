package domain.strategy;

import domain.Task;
import java.util.List;

public class TaskSortingContext {
    private TaskSortingStrategy strategy;

    public void setSortingStrategy(TaskSortingStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Task> sortTasks(List<Task> tasks) {
        if (strategy == null) {
            // Default to priority sorting
            strategy = new PrioritySortingStrategy();
        }
        return strategy.sort(tasks);
    }
}

