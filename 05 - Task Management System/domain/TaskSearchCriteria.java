package domain;

import java.time.LocalDateTime;
import java.util.List;

public class TaskSearchCriteria {
    private Integer assigneeId;
    private Integer creatorId;
    private Priority priority;
    private TaskStatus status;
    private DateRange dueDateRange;
    private List<String> tags;
    private Boolean hasSubtasks;
    private String sortBy; // "priority", "dueDate", "createdDate"
    private String sortOrder; // "asc" or "desc"

    public TaskSearchCriteria() {
        // Default constructor for flexible criteria building
        this.sortBy = "priority"; // Default sorting
        this.sortOrder = "desc"; // Default order
    }

    // Builder pattern methods
    public TaskSearchCriteria assigneeId(Integer assigneeId) {
        this.assigneeId = assigneeId;
        return this;
    }

    public TaskSearchCriteria creatorId(Integer creatorId) {
        this.creatorId = creatorId;
        return this;
    }

    public TaskSearchCriteria priority(Priority priority) {
        this.priority = priority;
        return this;
    }

    public TaskSearchCriteria status(TaskStatus status) {
        this.status = status;
        return this;
    }

    public TaskSearchCriteria dueDateRange(DateRange dueDateRange) {
        this.dueDateRange = dueDateRange;
        return this;
    }

    public TaskSearchCriteria tags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public TaskSearchCriteria hasSubtasks(Boolean hasSubtasks) {
        this.hasSubtasks = hasSubtasks;
        return this;
    }

    public TaskSearchCriteria sortBy(String sortBy) {
        this.sortBy = sortBy;
        return this;
    }

    public TaskSearchCriteria sortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
        return this;
    }

    // Getters
    public Integer getAssigneeId() { return assigneeId; }
    public Integer getCreatorId() { return creatorId; }
    public Priority getPriority() { return priority; }
    public TaskStatus getStatus() { return status; }
    public DateRange getDueDateRange() { return dueDateRange; }
    public List<String> getTags() { return tags; }
    public Boolean getHasSubtasks() { return hasSubtasks; }
    public String getSortBy() { return sortBy; }
    public String getSortOrder() { return sortOrder; }
}