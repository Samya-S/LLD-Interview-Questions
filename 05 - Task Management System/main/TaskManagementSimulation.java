package main;

import domain.*;
import domain.observer.*;
import domain.strategy.*;
import service.*;
import controller.*;
import repository.*;
import java.time.LocalDateTime;
import java.util.List;

public class TaskManagementSimulation {
    
    public static void main(String[] args) {
        System.out.println("🚀 Starting Task Management System Simulation...\n");
        
        // Initialize repositories
        TaskRepository taskRepository = new TaskRepositoryImpl();
        UserRepository userRepository = new UserRepositoryImpl();
        TaskChangeLogRepository changeLogRepository = new TaskChangeLogRepository();
        TaskSubscriptionRepository subscriptionRepository = new TaskSubscriptionRepository();
        CommentRepository commentRepository = new CommentRepository() {
            @Override
            public Comment save(Comment comment) {
                return comment;
            }
            
            @Override
            public List<Comment> findByTaskId(int taskId) {
                return new java.util.ArrayList<>();
            }
        };
        
        // Initialize services with correct constructors
        TaskService taskService = new TaskService(taskRepository);
        TaskAssignmentService assignmentService = new TaskAssignmentService(taskRepository, userRepository);
        TaskStateService stateService = new TaskStateService(taskRepository);
        TaskNotificationService notificationService = new TaskNotificationService(
            subscriptionRepository, changeLogRepository);
        
        // Initialize controllers (Frontend will interact with these)
        TaskController taskController = new TaskController(taskService);
        TaskAssignmentController assignmentController = new TaskAssignmentController(assignmentService);
        TaskStateController stateController = new TaskStateController(stateService);
        TaskNotificationController notificationController = new TaskNotificationController(notificationService);
        
        // Simulate frontend user interactions
        
        // 1. User Login and Dashboard View
        System.out.println("=== 📱 FRONTEND: User Dashboard ===");
        simulateUserDashboard(taskController, notificationController);
        
        // 2. Task Creation Workflow
        System.out.println("\n=== ✨ FRONTEND: Task Creation ===");
        simulateTaskCreation(taskController, assignmentController);
        
        // 3. Task Management Workflow
        System.out.println("\n=== 🔄 FRONTEND: Task Management ===");
        simulateTaskManagement(taskController, stateController, assignmentController);
        
        // 4. Task Collaboration Workflow
        System.out.println("\n=== 👥 FRONTEND: Task Collaboration ===");
        simulateTaskCollaboration(taskController, notificationController, commentRepository);
        
        // 5. Task Search and Filtering
        System.out.println("\n=== 🔍 FRONTEND: Task Search ===");
        simulateTaskSearch(taskController);
        
        // 6. Notification and Subscription Management
        System.out.println("\n=== 🔔 FRONTEND: Notifications ===");
        simulateNotificationManagement(notificationController, taskController);
        
        System.out.println("\n✅ Simulation completed successfully!");
    }
    
    private static void simulateUserDashboard(TaskController taskController, TaskNotificationController notificationController) {
        System.out.println("User opens dashboard...");
        
        // Simulate getting user's tasks using builder pattern
        TaskSearchCriteria userTasksCriteria = new TaskSearchCriteria()
            .assigneeId(1)
            .status(TaskStatus.TODO);
        
        List<Task> userTasks = taskController.searchTasks(userTasksCriteria);
        System.out.println("📋 User has " + userTasks.size() + " TODO tasks");
        
        // Simulate getting recent notifications
        if (!userTasks.isEmpty()) {
            List<TaskChangeLog> recentChanges = notificationController.getTaskHistory(userTasks.get(0).getId());
            System.out.println("📢 Recent changes for first task: " + recentChanges.size() + " updates");
        }
    }
    
    private static void simulateTaskCreation(TaskController taskController, TaskAssignmentController assignmentController) {
        System.out.println("User clicks 'Create New Task' button...");
        
        // Create a new task
        LocalDateTime dueDate = LocalDateTime.now().plusDays(7);
        Task newTask = taskController.createTask(
            "Implement User Authentication", 
            "Create login, registration, and password reset functionality", 
            dueDate, 
            "HIGH", 
            1
        );
        System.out.println("✅ Task created: " + newTask.getTitle() + " (ID: " + newTask.getId() + ")");
        
        // Assign the task
        System.out.println("User assigns task to team member...");
        assignmentController.assignTask(newTask.getId(), 2);
        System.out.println("👤 Task assigned to user ID: 2");
        
        // Create a subtask
        System.out.println("User adds subtask...");
        Task subtask = taskController.addSubtask(
            newTask.getId(),
            "Design Database Schema",
            "Create user table and authentication tables",
            dueDate.minusDays(2),
            "MEDIUM",
            1
        );
        System.out.println("📝 Subtask added: " + subtask.getTitle());
    }
    
    private static void simulateTaskManagement(TaskController taskController, TaskStateController stateController, 
                                             TaskAssignmentController assignmentController) {
        System.out.println("User opens task management panel...");
        
        // Search for tasks to manage using builder pattern
        TaskSearchCriteria criteria = new TaskSearchCriteria()
            .status(TaskStatus.TODO);
        List<Task> todoTasks = taskController.searchTasks(criteria);
        
        if (!todoTasks.isEmpty()) {
            Task taskToManage = todoTasks.get(0);
            System.out.println("📋 Managing task: " + taskToManage.getTitle());
            
            // Update task details
            System.out.println("User edits task details...");
            Task updatedTask = taskController.updateTask(
                taskToManage.getId(),
                taskToManage.getTitle() + " (Updated)",
                taskToManage.getDescription() + " - Additional requirements added",
                taskToManage.getDueDate().plusDays(1),
                "HIGH"
            );
            System.out.println("✏️ Task updated: " + updatedTask.getTitle());
            
            // Change task status
            System.out.println("User changes task status to In Progress...");
            stateController.updateTaskStatus(taskToManage.getId(), TaskStatus.IN_PROGRESS);
            System.out.println("🔄 Task status changed to IN_PROGRESS");
            
            // Reassign task
            System.out.println("User reassigns task...");
            assignmentController.assignTask(taskToManage.getId(), 2);
            System.out.println("👤 Task reassigned to user ID: 2");
        }
    }
    
    private static void simulateTaskCollaboration(TaskController taskController, TaskNotificationController notificationController,
                                                CommentRepository commentRepository) {
        System.out.println("User opens task collaboration panel...");
        
        // Subscribe to task notifications (only if tasks exist)
        System.out.println("User subscribes to task notifications...");
        // We'll subscribe to the first available task
        List<Task> availableTasks = taskController.searchTasks(new TaskSearchCriteria());
        if (!availableTasks.isEmpty()) {
            int taskId = availableTasks.get(0).getId();
            notificationController.subscribeToTask(taskId, 2);
            System.out.println("🔔 User 2 subscribed to task " + taskId);
            
            // Get task history
            List<TaskChangeLog> taskHistory = notificationController.getTaskHistory(taskId);
            System.out.println("📚 Task history retrieved: " + taskHistory.size() + " changes");
        } else {
            System.out.println("⚠️ No tasks available for subscription");
        }
        
        // Simulate adding comments using correct constructor
        System.out.println("User adds comment to task...");
        Comment comment = new Comment(1, 1, 2, "Great progress! Let's review the implementation.");
        commentRepository.save(comment);
        System.out.println("💬 Comment added: " + comment.getContent());
    }
    
    private static void simulateTaskSearch(TaskController taskController) {
        System.out.println("User searches for tasks...");
        
        // Search by different criteria using builder pattern
        TaskSearchCriteria highPriorityCriteria = new TaskSearchCriteria()
            .priority(Priority.HIGH);
        List<Task> highPriorityTasks = taskController.searchTasks(highPriorityCriteria);
        System.out.println("🔍 High priority tasks found: " + highPriorityTasks.size());
        
        TaskSearchCriteria overdueCriteria = new TaskSearchCriteria()
            .status(TaskStatus.IN_PROGRESS);
        List<Task> inProgressTasks = taskController.searchTasks(overdueCriteria);
        System.out.println("🔍 In-progress tasks found: " + inProgressTasks.size());
        
        // Search with multiple criteria using builder pattern
        TaskSearchCriteria complexCriteria = new TaskSearchCriteria()
            .assigneeId(1)
            .status(TaskStatus.TODO)
            .priority(Priority.MEDIUM);
        List<Task> complexSearchResults = taskController.searchTasks(complexCriteria);
        System.out.println("🔍 Complex search results: " + complexSearchResults.size() + " tasks");
        
        // Demonstrate Strategy Pattern for sorting
        System.out.println("\n=== 🎯 STRATEGY PATTERN: Sorting Demonstrations ===");
        
        // Sort by Priority (default strategy)
        System.out.println("📊 Sorting by Priority (HIGH → MEDIUM → LOW):");
        TaskSearchCriteria prioritySortCriteria = new TaskSearchCriteria()
            .sortBy("priority")
            .sortOrder("desc");
        List<Task> prioritySortedTasks = taskController.searchTasks(prioritySortCriteria);
        prioritySortedTasks.forEach(task -> 
            System.out.println("  - " + task.getTitle() + " (Priority: " + task.getPriority() + ")"));
        
        // Sort by Due Date
        System.out.println("\n📅 Sorting by Due Date (earliest first):");
        TaskSearchCriteria dueDateSortCriteria = new TaskSearchCriteria()
            .sortBy("dueDate")
            .sortOrder("asc");
        List<Task> dueDateSortedTasks = taskController.searchTasks(dueDateSortCriteria);
        dueDateSortedTasks.forEach(task -> 
            System.out.println("  - " + task.getTitle() + " (Due: " + task.getDueDate() + ")"));
        
        // Sort by Created Date
        System.out.println("\n🕒 Sorting by Created Date (newest first):");
        TaskSearchCriteria createdDateSortCriteria = new TaskSearchCriteria()
            .sortBy("createdDate")
            .sortOrder("desc");
        List<Task> createdDateSortedTasks = taskController.searchTasks(createdDateSortCriteria);
        createdDateSortedTasks.forEach(task -> 
            System.out.println("  - " + task.getTitle() + " (Created: " + task.getCreatedAt() + ")"));
        
        System.out.println("\n✨ Strategy Pattern allows easy swapping of sorting algorithms at runtime!");
    }
    
    private static void simulateNotificationManagement(TaskNotificationController notificationController, 
                                                     TaskController taskController) {
        System.out.println("User manages notification preferences...");
        
        // Subscribe to multiple tasks
        System.out.println("User subscribes to multiple tasks...");
        for (int i = 1; i <= 2; i++) {
            notificationController.subscribeToTask(i, 1);
            System.out.println("🔔 Subscribed to task " + i);
        }
        
        // Unsubscribe from a task
        System.out.println("User unsubscribes from a task...");
        notificationController.unsubscribeFromTask(2, 1);
        System.out.println("🔕 Unsubscribed from task 2");
        
        // Get notification history for a task (only if tasks exist)
        List<Task> availableTasks = taskController.searchTasks(new TaskSearchCriteria());
        if (!availableTasks.isEmpty()) {
            int taskId = availableTasks.get(0).getId();
            List<TaskChangeLog> notifications = notificationController.getTaskHistory(taskId);
            System.out.println("📢 Notifications for task " + taskId + ": " + notifications.size() + " updates");
        } else {
            System.out.println("⚠️ No tasks available for notification history");
        }
        
        // Simulate real-time notification (would be through WebSocket in real system)
        System.out.println("📱 Real-time notification received: Task status changed!");
    }
}
