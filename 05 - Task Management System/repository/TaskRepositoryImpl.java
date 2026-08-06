package repository;

import domain.Task;
import domain.TaskSearchCriteria;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class TaskRepositoryImpl implements TaskRepository {
    private Map<Integer, Task> tasks = new HashMap<>();
    private int nextId = 1;

    @Override
    public Task save(Task task) {
        if (task.getId() == 0) {
            task.setId(nextId++);
        }
        tasks.put(task.getId(), task);
        return task;
    }

    @Override
    public Task findById(int taskId) {
        return tasks.get(taskId);
    }

    @Override
    public List<Task> findByAssignee(int assigneeId) {
        return tasks.values().stream()
            .filter(task -> task.getAssigneeId() == assigneeId)
            .collect(Collectors.toList());
    }

    @Override
    public List<Task> findByParentTask(int parentTaskId) {
        return tasks.values().stream()
            .filter(task -> task.getParentTaskId() != null && task.getParentTaskId() == parentTaskId)
            .collect(Collectors.toList());
    }

    @Override
    public List<Task> search(TaskSearchCriteria criteria) {
        // TODO: Implement actual search logic
        return new ArrayList<>(tasks.values());
    }

    @Override
    public void delete(int taskId) {
        tasks.remove(taskId);
    }
}