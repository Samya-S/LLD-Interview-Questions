# PUBSUB SYSTEM LLD DESIGN STEPS

## STEP-1: DISCUSS FUNCTIONAL REQUIREMENTS

### FUNCTIONAL REQUIREMENTS:

1. The system should support multiple topics where messages can be published
2. Publisher should be able to publish a message to a particular topic
3. Subscribers should be able to subscribe to a topic
4. Whenever a message is published to a topic, all subscribers subscribed to that topic should receive the message
5. The system should support topic management
6. The system should maintain notification channels (build scalable system)

### EDGE CASES:

1. Subscriber goes offline during message delivery or the user is offline?
2. How many types of notifications?
3. What happens when someone comes online after long time?

---

## STEP-2: IDENTIFY CORE ENTITIES

1. **Topic (Core Entity)**
   - id: String [PK]
   - name: String
   - isActive: boolean
   - createdAt: long

2. **Subscriber**
   - id: String [PK]
   - email: String
   - realtimeConnectionId: String (nullable)
   - isOnline: boolean
   - createdAt: long
   - lastHeartbeat: long

3. **Subscription**
   - id: String [PK]
   - topicId: String [FK]
   - subscriberId: String [FK]
   - isActive: boolean
   - createdAt: long

4. **Message**
   - id: String [PK]
   - topicId: String [FK]
   - content: String
   - timestamp: long

5. **MessageDelivery**
   - id: String [PK]
   - messageId: String [FK]
   - subscriberId: String [FK]
   - channel: DeliveryChannel (Enum: EMAIL, REALTIME)
   - status: DeliveryStatus (Enum: PENDING, DELIVERED, ACKNOWLEDGED)
   - createdAt: long
   - acknowledgedAt: long (nullable)

6. **DeliveryChannel (Enum)**
   - EMAIL, REALTIME

7. **DeliveryStatus (Enum)**
   - PENDING, DELIVERED, ACKNOWLEDGED

---

## STEP-3: INTERACTION FLOWS

1. **Topic Management:**
   - Create topic
   - List topics
   - Deactivate topic

2. **Message Publishing:**
   - Publish message
   - Acknowledge message

3. **Subscriber Management:**
   - Register subscriber
   - Go online
   - Go offline

4. **Subscription Management:**
   - Subscribe to topic
   - Unsubscribe from topic

---

## STEP-4: DEFINE CLASS STRUCTURES AND RELATIONSHIPS

### CONTROLLERS:

1. **TopicController** (Topic Management)
   - Topic createTopic(String name)
   - List\<Topic\> getAllTopics()
   - void deactivateTopic(String topicId)

2. **PublisherController** (Message Publishing)
   - Message publishMessage(String topicId, String content)

3. **SubscriberController** (Subscriber Management)
   - Subscriber registerSubscriber(String email)
   - void goOnline(String subscriberId, String connectionId)
   - void goOffline(String subscriberId)

4. **SubscriptionController** (Subscription Management)
   - Subscription subscribeToTopic(String topicId, String subscriberId)
   - void unsubscribeFromTopic(String topicId, String subscriberId)

5. **MessageController** (Message Consumption)
   - void acknowledgeMessage(String messageId, String subscriberId)

### SERVICES:

1. **TopicService** (Topic Management)
   - Topic createTopic(String name)
   - List\<Topic\> getAllTopics()
   - void deactivateTopic(String topicId)

2. **PublisherService** (Message Publishing)
   - Message publishMessage(String topicId, String content)
   - void processMessageDeliveryAsync(Message message, Topic topic)

3. **SubscriberService** (Subscriber Management)
   - Subscriber registerSubscriber(String email)
   - void goOnline(String subscriberId, String connectionId)
   - void goOffline(String subscriberId)
   - void pushPendingDeliveries(String subscriberId)

4. **SubscriptionService** (Subscription Management)
   - Subscription subscribeToTopic(String topicId, String subscriberId)
   - void unsubscribeFromTopic(String topicId, String subscriberId)

5. **MessageService** (Message Consumption)
   - void acknowledgeMessage(String messageId, String subscriberId)

### REPOSITORIES:

1. **TopicRepository**
   - Topic save(Topic topic)
   - List\<Topic\> findAll()
   - Optional\<Topic\> findById(String topicId)
   - void deleteById(String topicId)

2. **SubscriberRepository**
   - Subscriber save(Subscriber subscriber)
   - Optional\<Subscriber\> findById(String subscriberId)
   - List\<Subscriber\> findAll()
   - void updateOnlineStatus(String subscriberId, boolean isOnline, String connectionId)
   - void deleteById(String subscriberId)

3. **SubscriptionRepository**
   - Subscription save(Subscription subscription)
   - List\<Subscription\> findByTopic(String topicId)
   - List\<Subscription\> findBySubscriber(String subscriberId)
   - void deactivateSubscription(String topicId, String subscriberId)
   - void deleteById(String subscriptionId)

4. **MessageRepository**
   - Message save(Message message)
   - Optional\<Message\> findById(String messageId)
   - void deleteById(String messageId)

5. **MessageDeliveryRepository**
   - MessageDelivery save(MessageDelivery delivery)
   - List\<MessageDelivery\> findPendingBySubscriber(String subscriberId)
   - void updateDeliveryStatus(String deliveryId, DeliveryStatus status)
   - void deleteById(String deliveryId)

### OBSERVER PATTERN IMPLEMENTATION:

1. **SubscriberObserver** (Observer Interface)
   - void update(Message message)

2. **EmailSubscriber** (Concrete Observer)
   - email: String
   - void update(Message message) // Send email notification

3. **RealtimeSubscriber** (Concrete Observer)
   - connectionId: String
   - subscriberId: String
   - void update(Message message) // Send realtime notification

4. **MessageSubject** (Subject - Observer Pattern Implementation)
   - emailSubscribers: List\<SubscriberObserver\>
   - realtimeSubscribers: List\<SubscriberObserver\>
   - void addEmailSubscriber(SubscriberObserver subscriber)
   - void removeEmailSubscriber(SubscriberObserver subscriber)
   - void addRealtimeSubscriber(SubscriberObserver subscriber)
   - void removeRealtimeSubscriber(SubscriberObserver subscriber)
   - void notify(Message message) // Calls both email and realtime notifications
   - void notifyEmailSubscribers(Message message)
   - void notifyRealtimeSubscribers(Message message)
   - List\<SubscriberObserver\> getEmailSubscribers()
   - List\<SubscriberObserver\> getRealtimeSubscribers()

---

## STEP-5: CORE USE CASES AND METHODS

---

## STEP-6: OOPS PRINCIPLES AND DESIGN PATTERNS USED

### DESIGN PATTERNS USED:

1. **Repository Pattern** - for data access abstraction
2. **Service Layer Pattern** - for business logic separation
3. **Observer Pattern** - for message notification system
4. **RESTful API Design** - for clean HTTP endpoints
5. **Controller Separation Pattern** - for better separation of concerns

### OOP PRINCIPLES APPLIED:

1. **Single Responsibility** - each controller handles one specific concern
2. **Open/Closed** - easy to extend with new notification types or delivery mechanisms
3. **Encapsulation** - domain objects encapsulate their data and behavior
4. **Dependency Inversion** - services depend on repositories, not concrete implementations
5. **Polymorphism** - different subscriber types can be handled uniformly through observer interface

---

## STEP-7: HANDLE EDGE CASES

1. **Subscriber goes offline during message delivery or the user is offline?**
   - Email notifications: Always sent regardless of online status (fire-and-forget)
   - Realtime notifications: Only sent to online subscribers
   - Offline subscribers: No realtime delivery, but email still works
   - Pending realtime deliveries: Stored in database for later delivery when subscriber comes online

2. **How many types of notifications?**
   - Two notification channels: Email and Realtime
   - Email: Always active, auto-acknowledged, works for all subscribers
   - Realtime: Only for online subscribers, requires explicit acknowledgment
   - Each topic maintains separate subscriber lists for each channel

3. **What happens when someone comes online after long time?**
   - Update subscriber status to online
   - Add subscriber to realtime lists for all their active topic subscriptions
   - Scan database for pending realtime deliveries for this subscriber
   - Push all pending messages to the subscriber
   - Subscriber can acknowledge messages as they receive them
   - Email notifications continue working regardless of online status
