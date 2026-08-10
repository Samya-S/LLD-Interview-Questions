# HOTEL MANAGEMENT SYSTEM (MAKEMYTRIP HOTELS) LLD DESIGN STEPS

## STEP-1: DISCUSS FUNCTIONAL REQUIREMENTS

### FUNCTIONAL REQUIREMENTS:

1. The system should allow users to search and browse hotels/rooms by location (city/geo), date range (check-in, check-out), room type (capacity/bed type/amenities), and price range
2. The system should provide real-time availability check per night, per hotel, per room type
3. The system should support booking cancellations with policies (non-refundable, partial refund with cutoff)
4. The system should handle payment workflow with states (Pending -> Completed -> Refunded/Failed)
5. The system should support dynamic daily/seasonal pricing where price can vary per day
6. The system should support multiple roles: Customer, Admin
7. The system should provide user dashboard to view past and upcoming bookings
8. The system should allow users to reserve a room (single room per booking)
9. The system should provide Admin panel to add/remove/update hotel/room/pricing/policies
10. The system should have a background scheduler to process expired HELD bookings and restore inventory

### EDGE CASES:

1. Cancellation policy on hotel or room?
2. Overbooking allowed? If yes, how much?
3. How to show price for multiple days if it includes a surge day?
4. Do we maintain check in and check out for the booking made?
5. What if the user checks out before check out date?
6. Any coupon code management?

---

## STEP-2: IDENTIFY CORE ENTITIES

1. **Hotel (Core Entity)**
   - id: String [PK]
   - name: String
   - address: String
   - city: String
   - country: String
   - latitude: double
   - longitude: double
   - rating: double
   - isActive: boolean
   - defaultOverbookPercent: int (e.g., 10)
   - cancellationPolicyId: String [FK]
   - createdAt: long

2. **RoomType**
   - id: String [PK]
   - hotelId: String [FK]
   - name: String (e.g., Deluxe King)
   - capacity: int
   - bedType: String (e.g., KING, QUEEN, TWIN)
   - basePrice: long (in minor units)
   - amenities: List\<String\>
   - totalRooms: int (physical rooms for this type)
   - isActive: boolean
   - createdAt: long

3. **Room**
   - id: String [PK]
   - hotelId: String [FK]
   - roomTypeId: String [FK]
   - roomNumber: String
   - isActive: boolean
   - createdAt: long

4. **Booking**
   - id: String [PK]
   - userId: String [FK]
   - hotelId: String [FK]
   - roomTypeId: String [FK]
   - checkInDateUtc: long
   - checkOutDateUtc: long // exclusive
   - nightlyPrices: List\<NightlyPrice\> // (dateUtc, priceMinor)
   - totalAmountMinor: long
   - bookingStatus: BookingStatus (CREATED, HELD, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED)
   - paymentStatus: TransactionStatus (PENDING, COMPLETED, REFUNDED, FAILED)
   - allocatedRoomId: String (nullable; assign at check-in)
   - checkInTimeUtc: long (nullable; set by admin at check-in)
   - checkOutTimeUtc: long (nullable; set by admin at check-out or early check-out)
   - holdExpiresAt: long (UTC)
   - createdAt: long

6. **Transaction**
   - id: String [PK]
   - bookingId: String [FK]
   - amountMinor: long
   - currency: String
   - status: TransactionStatus (PENDING, COMPLETED, REFUNDED, FAILED)
   - providerRef: String
   - createdAt: long
   - completedAt: long (nullable)
   - refundedAt: long (nullable)

7. **SeasonalPrice (Dynamic Daily Price)**
   - id: String [PK]
   - hotelId: String [FK]
   - roomTypeId: String [FK]
   - dateUtc: long
   - priceMinor: long
   - createdAt: long

8. **CancellationPolicy**
   - id: String [PK]
   - name: String (NON_REFUNDABLE, PARTIAL, FLEX)
   - refundPercent: int (0..100)
   - cutoffHoursBeforeCheckIn: int
   - createdAt: long

9. **User**
   - id: String [PK]
   - name: String
   - email: String
   - role: UserRole (CUSTOMER, ADMIN)
   - createdAt: long

10. **SearchFilter (Request DTO)**

11. **RoomTypeAvailability (Response DTO)**
    - roomTypeId: String
    - roomTypeName: String
    - capacity: int
    - bedType: String
    - amenities: List\<String\>
    - available: boolean
    - totalPrice: long
    - averagePricePerNight: double
    - nightlyPrices: List\<NightlyPrice\>

---

## STEP-3: VISUALIZE INTERACTION FLOWS

1. **Hotel Search & Browse:**  
   - User enters filters -> System queries search index -> Returns hotels/room types with total price and average price per night

2. **Real-time Availability:**  
   - User selects hotel + date range -> System queries all room types for hotel -> For each room type: checks availability (query CONFIRMED and HELD bookings, CREATED bookings don't count) -> Calculates pricing -> Returns List\<RoomTypeAvailability\> with only available room types + their details and pricing

3. **Booking (Two-Phase):**  
   **Phase 1 - Create Booking:**  
   - Validate request -> Precheck availability (query CONFIRMED + HELD bookings) -> Fetch current prices from DB (SeasonalPrice table, fallback to basePrice) -> Validate prices match expected -> Price the stay -> Create booking (CREATED, paymentStatus=PENDING) with locked prices -> NO inventory reduction (CREATED bookings don't count in availability)

   **Phase 2 - Initiate Payment:**  
   - User initiates payment -> TransactionService.initiateTransaction(bookingId) -> Validate booking status is CREATED -> Update booking (CREATED -> HELD) -> Reduce inventory (booking now counts as HELD) -> On payment success callback: confirm booking (status=HELD -> CONFIRMED);
   on payment failure/timeout callback: restore inventory (mark booking CANCELLED)

4. **Cancellation:**  
   - User requests cancel -> Validate booking can be cancelled (not CHECKED_IN/CHECKED_OUT) -> Apply policy by time window -> Update booking status to CANCELLED -> Trigger refund if applicable -> Inventory automatically restored (CANCELLED bookings don't count in availability)

5. **Check-in:**  
   - Admin checks in guest -> Assign room -> Update booking status to CHECKED_IN -> Set checkInTimeUtc and allocatedRoomId

6. **Check-out:**  
   - Admin checks out guest -> Update booking status to CHECKED_OUT -> Set checkOutTimeUtc -> If early check-out (before checkOutDateUtc), release inventory for remaining dates

7. **Admin:**  
   - CRUD hotel/room/roomType -> Adjust inventory/overbooking percent -> Manage seasonal prices and policies

8. **User Dashboard:**  
   - Fetch bookings partitioned into past vs upcoming with statuses and receipts

9. **Background Scheduler:**
   - Periodically (every 1-5 minutes) check for expired HELD bookings
   - Mark expired bookings as CANCELLED to restore inventory

---

## STEP-4: DEFINE CLASS STRUCTURES AND RELATIONSHIPS

### CONTROLLERS:

1. **SearchController** (Search & Availability)
   - List\<Hotel\> searchHotels(SearchFilter filter)
   - List\<RoomTypeAvailability\> getAvailability(String hotelId, DateRange range)

2. **BookingController** (Reservation)
   - Booking createBooking(String userId, String hotelId, String roomTypeId, DateRange range, long expectedTotalPrice)
   - void cancelBooking(String bookingId, String userId)

3. **TransactionController** (Transactions)
   - Transaction initiateTransaction(String bookingId)
   - void handleTransactionCallback(String providerRef, TransactionStatus status)

4. **AdminController** (Admin)
   - Hotel createOrUpdateHotel(Hotel hotel)
   - RoomType createOrUpdateRoomType(RoomType roomType)
   - void updateOverbookingPercent(String hotelId, String roomTypeId, int percent)
   - SeasonalPrice setSeasonalPrice(String hotelId, String roomTypeId, long dateUtc, long priceMinor)
   - CancellationPolicy createOrUpdatePolicy(CancellationPolicy policy)
   - Booking checkIn(String bookingId, String roomId, long checkInTimeUtc)
   - Booking checkOut(String bookingId, long checkOutTimeUtc) // handles early check-out

5. **DashboardController** (User)
   - List\<Booking\> listUserBookings(String userId)

### SERVICES:

1. **SearchService**
   - List\<Hotel\> searchHotels(SearchFilter filter)
   - List\<RoomTypeAvailability\> getAvailability(String hotelId, DateRange range)

2. **InventoryService**
   - int getConfirmedBookingsCount(String hotelId, String roomTypeId, long dateUtc)
   - int getHeldBookingsCount(String hotelId, String roomTypeId, long dateUtc)
   - int getCheckedInBookingsCount(String hotelId, String roomTypeId, long dateUtc)
   - boolean checkAvailability(String hotelId, String roomTypeId, DateRange range, int qty)

3. **PricingService**
   - List\<NightlyPrice\> rateStay(String hotelId, String roomTypeId, DateRange range)
   - long computeTotal(List\<NightlyPrice\> nightly)
   - double computeAveragePricePerNight(List\<NightlyPrice\> nightly, int numberOfNights)

4. **BookingService**
   - Booking createBooking(String userId, String hotelId, String roomTypeId, DateRange range, long expectedTotalPrice)
   - void cancelBooking(String bookingId, String userId)
   - Booking checkIn(String bookingId, String roomId, long checkInTimeUtc)
   - Booking checkOut(String bookingId, long checkOutTimeUtc)

5. **TransactionService**
   - Transaction initiateTransaction(String bookingId)
   - void handleCallback(String providerRef, TransactionStatus status)
   - void issueRefund(String bookingId, long amountMinor)

6. **BookingStateHandler** (State Pattern Implementation)
   - boolean canTransition(BookingStatus current, BookingStatus newStatus)
   - void transition(Booking booking, BookingStatus newStatus)
   - void requireStatus(Booking booking, BookingStatus expectedStatus)
   - void requireAnyStatus(Booking booking, BookingStatus... allowedStatuses)
   - boolean canCancel(Booking booking)
   - boolean canCheckIn(Booking booking)
   - boolean canCheckOut(Booking booking)
   - boolean canInitiateTransaction(Booking booking)
   - boolean countsInInventory(Booking booking)

7. **PolicyService**
   - RefundDecision evaluateCancellation(Booking booking, CancellationPolicy policy, long nowUtc)

8. **UserService**
   - List\<Booking\> listUserBookings(String userId)

9. **SchedulerService** (Background Jobs)
   - void processExpiredHolds()
   - List\<Booking\> findExpiredHeldBookings(long nowUtc)

---

## STEP-5: CORE USE CASES AND METHODS

---

## STEP-6: OOPS PRINCIPLES AND DESIGN PATTERNS USED

### DESIGN PATTERNS USED:

1. **Repository Pattern** - for data access abstraction
2. **Service Layer Pattern** - for business logic separation
3. **State Pattern** - BookingStateHandler manages all BookingStatus transitions and validations
   - Encapsulates state transition logic (no manual if/else checks)
   - Validates transitions: CREATED-\>HELD, HELD-\>CONFIRMED, HELD-\>CANCELLED, CONFIRMED-\>CHECKED_IN, CHECKED_IN-\>CHECKED_OUT, etc.
   - Provides helper methods: canCancel(), canCheckIn(), canCheckOut(), canInitiateTransaction(), countsInInventory()
   - All services use BookingStateHandler.transition() and BookingStateHandler.requireStatus() instead of manual checks
4. **Controller Separation** - clear responsibilities by domain
5. **Domain Events** - decouple payment, booking, inventory, notifications

### OOP PRINCIPLES APPLIED:

1. **Single Responsibility** - each class handles one concern
2. **Open/Closed** - easy to extend with new features without modifying existing code
3. **Encapsulation** - domain objects control invariants (date range, totals)
4. **Dependency Inversion** - services depend on repositories/interfaces
5. **Consistency Boundaries** - inventory operations are the concurrency boundary

