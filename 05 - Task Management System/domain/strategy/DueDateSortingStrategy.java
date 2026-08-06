package domain.strategy;

import domain.Task;
import java.util.Comparator;
import java.util.List;

public class DueDateSortingStrategy implements TaskSortingStrategy {
    
    @Override
    public List<Task> sort(List<Task> tasks) {
        // Sort by due date (earliest first)
        List<Task> mutableTasks = new java.util.ArrayList<>(tasks);
        mutableTasks.sort(Comparator.comparing(Task::getDueDate, 
            Comparator.nullsLast(Comparator.naturalOrder())));
        return mutableTasks;
    }

    @Override
    public String getStrategyName() {
        return "DUE_DATE";
    }
}