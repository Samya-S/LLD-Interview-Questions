# TASK MANAGEMENT SYSTEM LLD DESIGN STEPS

## STEP-1: DISCUSS FUNCTIONAL REQUIREMENTS

### FUNCTIONAL REQUIREMENTS:

1. The system should allow users to create, update, and delete tasks with title, description, due date, priority, and status.
2. The system should support composite tasks where a task can contain multiple subtasks (recursive structure).
3. Users should be able to assign tasks to other users and update task details including title, description, due date, and priority.
4. The system should implement a state machine for task workflow with state-specific behavior.
5. Users should be able to view tasks assigned to them, tasks they have created, and check delayed tasks.
6. The system should support searching and filtering tasks with flexible sorting strategies (Strategy Pattern).
7. The system should automatically log all task changes and notify observers of status updates (Observer Pattern).
8. Users should be able to categorize tasks using tags and add comments for collaboration.
9. The system should handle concurrent access to tasks and ensure data consistency.
10. Users should be able to view task distribution by priority, status, and tags.

### EDGE CASES:

1. TASK DELETION WITH SUBTASKS
2. CONCURRENT TASK UPDATES
3. TASK PRIORITY CONFLICTS
4. STATE TRANSITION RULES

---

## STEP-2: IDENTIFY CORE ENTITIES

1. **Task (Core Entity)**
   - id: int [PK]
   - title: String
   - description: String
   - dueDate: LocalDateTime
   - priority: Priority (Enum: LOW, MEDIUM, HIGH, URGENT)
   - status: TaskStatus (Enum: TODO, IN_PROGRESS, REVIEW, COMPLETED, CANCELLED)
   - assigneeId: int [FK to User]
   - creatorId: int [FK to User]
   - parentTaskId: int [FK to Task - for subtasks]
   - tags: List\<String\>
   - createdAt: LocalDateTime
   - updatedAt: LocalDateTime

2. **User**
   - id: int [PK]
   - username: String
   - email: String
   - role: UserRole (Enum: USER, ADMIN)

3. **TaskStatus (Enum)**
   - TODO, IN_PROGRESS, REVIEW, COMPLETED, CANCELLED

4. **Priority (Enum)**
   - LOW, MEDIUM, HIGH, URGENT

5. **Comment**
   - id: int [PK]
   - taskId: int [FK to Task]
   - userId: int [FK to User]
   - content: String
   - createdAt: LocalDateTime

6. **TaskChangeLog (Audit Trail)**
   - id: int [PK]
   - taskId: int [FK to Task]
   - userId: int [FK to User]
   - changeType: ChangeType (Enum: CREATED, UPDATED, STATUS_CHANGED, ASSIGNED)
   - oldValue: String
   - newValue: String
   - timestamp: LocalDateTime

7. **ChangeType (Enum)**
   - CREATED, UPDATED, STATUS_CHANGED, ASSIGNED, PRIORITY_CHANGED

8. **TaskSubscription (Observer Pattern)**
   - id: int [PK]
   - userId: int [FK to User]
   - taskId: int [FK to Task]
   - isActive: boolean

10. **TaskSearchCriteria**
    - assigneeId: int
    - creatorId: int
    - priority: Priority
    - status: TaskStatus
    - dueDateRange: DateRange
    - tags: List\<String\>
    - hasSubtasks: boolean

11. **DateRange**
    - startDate: LocalDateTime
    - endDate: LocalDateTime

---

## STEP-3: VISUALIZE INTERACTION FLOWS

1. **Task Creation Flow:**  
   POST /api/tasks -> TaskController.createTask() ->
   TaskService.createTask() -> Validate user permissions -> Create task ->
   If parent task exists, validate hierarchy -> Return task

2. **Task Update Flow:**  
   PUT /api/tasks/{id} -> TaskController.updateTask() ->
   TaskService.updateTask() -> Validate permissions -> Update task ->
   Update subtask priorities if needed -> Return updated task

3. **Task Status Change Flow:**  
   PUT /api/tasks/{id}/status -> TaskStateController.updateTaskStatus() ->
   TaskStateService.updateTaskStatus() -> Validate state transition -> Update status -> Return success

4. **Task Search & Sort Flow:**  
   GET /api/tasks/search -> TaskController.searchTasks() ->
   TaskService.searchTasks() -> Apply search criteria -> Apply sorting strategy ->
   Return results with recursive subtask structure

5. **Subtask Management Flow:**  
   POST /api/tasks/{id}/subtasks -> TaskController.addSubtask() ->
   TaskService.addSubtask() -> Validate parent task -> Create subtask -> Return subtask

---

## STEP-4: DEFINE CLASS STRUCTURES AND RELATIONSHIPS

### CONTROLLERS:

1. **TaskController** (Core Task Operations)
   - Task createTask(CreateTaskRequest request)
   - Task updateTask(int taskId, UpdateTaskRequest request)
   - void deleteTask(int taskId)
   - List\<Task\> searchTasks(TaskSearchCriteria criteria)
   - Task addSubtask(int parentTaskId, CreateTaskRequest request)

2. **TaskStateController** (State Management)
   - void updateTaskStatus(int taskId, TaskStatus newStatus)

3. **TaskAssignmentController** (Assignment Operations)
   - void assignTask(int taskId, int assigneeId)

4. **TaskNotificationController** (Observer Pattern)
   - void subscribeToTask(int taskId, int userId)
   - void unsubscribeFromTask(int taskId, int userId)
   - List\<TaskChangeLog\> getTaskHistory(int taskId)

### SERVICES:

1. **TaskService** (Core Business Logic)
   - Task createTask(CreateTaskRequest request)
   - Task updateTask(int taskId, UpdateTaskRequest request)
   - void deleteTask(int taskId)
   - List\<Task\> searchTasks(TaskSearchCriteria criteria)
   - Task addSubtask(int parentTaskId, CreateTaskRequest request)

2. **TaskStateService** (State Management)
   - void updateTaskStatus(int taskId, TaskStatus newStatus)
   - boolean isValidTransition(TaskStatus currentStatus, TaskStatus newStatus)

3. **TaskAssignmentService** (Assignment Logic)
   - void assignTask(int taskId, int assigneeId)

4. **TaskNotificationService** (Observer Pattern)
   - void subscribeToTask(int taskId, int userId)
   - void unsubscribeFromTask(int taskId, int userId)
   - void notifySubscribers(int taskId, ChangeType changeType, String oldValue, String newValue)
   - List\<TaskChangeLog\> getTaskHistory(int taskId)

### REPOSITORIES:

1. **TaskRepository**
   - Task save(Task task)
   - Task findById(int taskId)
   - List\<Task\> findByAssignee(int assigneeId)
   - List\<Task\> findByParentTask(int parentTaskId)
   - List\<Task\> search(TaskSearchCriteria criteria)

2. **UserRepository**
   - User findById(int userId)

3. **CommentRepository**
   - Comment save(Comment comment)
   - List\<Comment\> findByTaskId(int taskId)

4. **TaskChangeLogRepository**
   - TaskChangeLog save(TaskChangeLog log)
   - List\<TaskChangeLog\> findByTaskId(int taskId)

5. **TaskSubscriptionRepository**
   - TaskSubscription save(TaskSubscription subscription)
   - List\<TaskSubscription\> findByTaskId(int taskId)
   - List\<TaskSubscription\> findByUserId(int userId)

### STATE PATTERN IMPLEMENTATION:

1. **TaskState** (Interface)
   - boolean canTransitionTo(TaskStatus newStatus)
   - void performTransition(Task task, TaskStatus newStatus)
   - String getStateName()

2. **TodoState** (Concrete State)
   - boolean canTransitionTo(TaskStatus newStatus) // Can go to IN_PROGRESS, CANCELLED
   - void performTransition(Task task, TaskStatus newStatus) // Update status
   - String getStateName() // Returns "TODO"

3. **InProgressState** (Concrete State)
   - boolean canTransitionTo(TaskStatus newStatus) // Can go to REVIEW, CANCELLED
   - void performTransition(Task task, TaskStatus newStatus) // Update status
   - String getStateName() // Returns "IN_PROGRESS"

4. **ReviewState** (Concrete State)
   - boolean canTransitionTo(TaskStatus newStatus) // Can go to COMPLETED, IN_PROGRESS
   - void performTransition(Task task, TaskStatus newStatus) // Update status
   - String getStateName() // Returns "REVIEW"

5. **CompletedState** (Concrete State)
   - boolean canTransitionTo(TaskStatus newStatus) // Can go to IN_PROGRESS (reopen)
   - void performTransition(Task task, TaskStatus newStatus) // Update status
   - String getStateName() // Returns "COMPLETED"

6. **CancelledState** (Concrete State)
   - boolean canTransitionTo(TaskStatus newStatus) // Can go to TODO (reactivate)
   - void performTransition(Task task, TaskStatus newStatus) // Update status
   - String getStateName() // Returns "CANCELLED"

### STRATEGY PATTERN IMPLEMENTATION:

1. **TaskSortingStrategy** (Interface)
   - List\<Task\> sort(List\<Task\> tasks)
   - String getStrategyName()

2. **PrioritySortingStrategy** (Concrete Strategy) - IMPLEMENTED
   - List\<Task\> sort(List\<Task\> tasks) // Sort by priority (URGENT -> HIGH -> MEDIUM -> LOW)
   - String getStrategyName() // Returns "PRIORITY"

3. **DueDateSortingStrategy** (Concrete Strategy) - IMPLEMENTED
   - List\<Task\> sort(List\<Task\> tasks) // Sort by due date (earliest first)
   - String getStrategyName() // Returns "DUE_DATE"

4. **CreatedDateSortingStrategy** (Concrete Strategy) - IMPLEMENTED
   - List\<Task\> sort(List\<Task\> tasks) // Sort by creation date (newest first)
   - String getStrategyName() // Returns "CREATED_DATE"

5. **TaskSortingContext** (Context) - IMPLEMENTED
   - TaskSortingStrategy strategy
   - void setSortingStrategy(TaskSortingStrategy strategy)
   - List\<Task\> sortTasks(List\<Task\> tasks)

6. **TaskSearchCriteria** (Enhanced with Sorting) - IMPLEMENTED
   - sortBy: String ("priority", "dueDate", "createdDate")
   - sortOrder: String ("asc", "desc")
   - Builder pattern methods for sorting configuration

7. **TaskService Integration** - IMPLEMENTED
   - searchTasks() method automatically applies appropriate sorting strategy
   - Runtime strategy selection based on search criteria
   - Support for ascending/descending order

### OBSERVER PATTERN IMPLEMENTATION:

1. **TaskSubject** (Subject Interface) - IMPLEMENTED
   - void attach(TaskSubscriber subscriber)
   - void detach(TaskSubscriber subscriber)
   - void notifySubscribers(ChangeType changeType, String oldValue, String newValue)

2. **Task** (Enhanced with Observer Pattern) - IMPLEMENTED
   - subscribers: List\<TaskSubscriber\>
   - void attach(TaskSubscriber subscriber)
   - void detach(TaskSubscriber subscriber)
   - void notifySubscribers(ChangeType changeType, String oldValue, String newValue)
   - Implements TaskSubject interface

3. **TaskSubscriber** (Observer Interface)
   - void update(int taskId, ChangeType changeType, String oldValue, String newValue)

4. **EmailSubscriber** (Concrete Observer)
   - emailService: EmailService
   - void update(int taskId, ChangeType changeType, String oldValue, String newValue)

5. **MobileAppSubscriber** (Concrete Observer)
   - pushNotificationService: PushNotificationService
   - void update(int taskId, ChangeType changeType, String oldValue, String newValue)

---

## STEP-5: CORE USE CASES AND METHODS

1. **Task Creation Use Case:**  
   POST /api/tasks -> TaskController.createTask() ->
   TaskService.createTask() -> Validate user permissions -> Create task ->
   If parent task exists, validate hierarchy -> Notify subscribers -> Return task

2. **Task Status Update Use Case:**  
   PUT /api/tasks/{id}/status -> TaskStateController.updateTaskStatus() ->
   TaskStateService.updateTaskStatus() -> Validate state transition -> Update status -> Notify subscribers -> Return success

3. **Task Search & Sort Use Case - FULLY IMPLEMENTED:**  
   GET /api/tasks/search -> TaskController.searchTasks() ->
   TaskService.searchTasks() -> Apply search criteria -> Apply sorting strategy ->
   Return results with recursive subtask structure

4. **Subtask Management Use Case:**  
   POST /api/tasks/{id}/subtasks -> TaskController.addSubtask() ->
   TaskService.addSubtask() -> Validate parent task -> Create subtask -> Notify subscribers -> Return subtask

5. **Task Assignment Use Case:**  
   PUT /api/tasks/{id}/assign -> TaskAssignmentController.assignTask() ->
   TaskAssignmentService.assignTask() -> Validate assignee -> Update assignment -> Notify subscribers -> Return success

6. **Task Subscription Use Case:**  
   POST /api/tasks/{id}/subscribe -> TaskNotificationController.subscribeToTask() ->
   TaskNotificationService.subscribeToTask() -> Create subscription -> Return success

---

## STEP-6: OOPS AND DESIGN PRINCIPLES USED

### DESIGN PATTERNS USED:

1. **Repository Pattern** - for data access abstraction
2. **Service Layer Pattern** - for business logic separation
3. **State Pattern** - for task status management
4. **Strategy Pattern** - for flexible sorting algorithms
5. **Observer Pattern** - for task change notifications
6. **Composite Pattern** - for task/subtask hierarchy
7. **RESTful API Design** - for clean HTTP endpoints

### OOP PRINCIPLES APPLIED:

1. **Single Responsibility** - each service has one clear purpose
2. **Open/Closed** - easy to extend with new sorting strategies and states (DEMONSTRATED)
3. **Encapsulation** - domain objects encapsulate their data and behavior
4. **Dependency Inversion** - services depend on abstractions, not concrete implementations
5. **Polymorphism** - different states and sorting strategies can be swapped at runtime (DEMONSTRATED)

---

## STEP-7: DISCUSS EDGE CASE SOLUTIONS

1. **Task Deletion with Subtasks** - Cascade delete all subtasks recursively
2. **Concurrent Task Updates** - Use optimistic locking with version field
3. **Task Priority Conflicts** - Auto-adjust parent task priority upward if child has higher priority
4. **State Transition Rules** - Strict state machine with predefined valid transitions
