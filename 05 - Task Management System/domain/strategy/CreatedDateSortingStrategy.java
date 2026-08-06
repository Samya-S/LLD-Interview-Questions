package domain.strategy;

import domain.Task;
import java.util.Comparator;
import java.util.List;

public class CreatedDateSortingStrategy implements TaskSortingStrategy {
    
    @Override
    public List<Task> sort(List<Task> tasks) {
        // Sort by creation date (newest first)
        List<Task> mutableTasks = new java.util.ArrayList<>(tasks);
        mutableTasks.sort(Comparator.comparing(Task::getCreatedAt, 
            Comparator.nullsLast(Comparator.reverseOrder())));
        return mutableTasks;
    }

    @Override
    public String getStrategyName() {
        return "CREATED_DATE";
    }
}