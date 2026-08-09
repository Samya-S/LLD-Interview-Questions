package service;

import domain.Booking;
import domain.BookingStatus;
import java.util.Set;
import java.util.HashSet;

/**
 * State Handler for BookingStatus transitions following the State Pattern.
 * Encapsulates all state transition logic and validations.
 */
public class BookingStateHandler {

    // Define valid state transitions
    private static final Set<StateTransition> VALID_TRANSITIONS = new HashSet<>();

    static {
        // CREATED -> HELD (when transaction is initiated)
        VALID_TRANSITIONS.add(new StateTransition(BookingStatus.CREATED, BookingStatus.HELD));

        // HELD -> CONFIRMED (when payment succeeds)
        VALID_TRANSITIONS.add(new StateTransition(BookingStatus.HELD, BookingStatus.CONFIRMED));

        // HELD -> CANCELLED (when payment fails or expires)
        VALID_TRANSITIONS.add(new StateTransition(BookingStatus.HELD, BookingStatus.CANCELLED));

        // CONFIRMED -> CHECKED_IN (when admin checks in guest)
        VALID_TRANSITIONS.add(new StateTransition(BookingStatus.CONFIRMED, BookingStatus.CHECKED_IN));

        // CHECKED_IN -> CHECKED_OUT (when admin checks out guest)
        VALID_TRANSITIONS.add(new StateTransition(BookingStatus.CHECKED_IN, BookingStatus.CHECKED_OUT));

        // CREATED -> CANCELLED (when user cancels before payment)
        VALID_TRANSITIONS.add(new StateTransition(BookingStatus.CREATED, BookingStatus.CANCELLED));

        // CONFIRMED -> CANCELLED (when user cancels after payment)
        VALID_TRANSITIONS.add(new StateTransition(BookingStatus.CONFIRMED, BookingStatus.CANCELLED));
    }

    /**
     * Validates if a state transition is allowed.
     * 
     * @param currentStatus Current booking status
     * @param newStatus     Desired new status
     * @return true if transition is valid, false otherwise
     */
    public static boolean canTransition(BookingStatus currentStatus, BookingStatus newStatus) {
        if (currentStatus == newStatus) {
            return true; // No transition needed
        }
        return VALID_TRANSITIONS.contains(new StateTransition(currentStatus, newStatus));
    }

    /**
     * Validates and performs a state transition on a booking.
     * 
     * @param booking   The booking to transition
     * @param newStatus The desired new status
     * @throws IllegalStateException if transition is not allowed
     */
    public static void transition(Booking booking, BookingStatus newStatus) {
        BookingStatus currentStatus = booking.getBookingStatus();

        if (!canTransition(currentStatus, newStatus)) {
            throw new IllegalStateException(
                    String.format("Invalid state transition: Cannot transition from %s to %s",
                            currentStatus, newStatus));
        }

        booking.setBookingStatus(newStatus);
    }

    /**
     * Validates if a booking is in a specific status.
     * 
     * @param booking        The booking to check
     * @param expectedStatus The expected status
     * @throws IllegalStateException if booking is not in expected status
     */
    public static void requireStatus(Booking booking, BookingStatus expectedStatus) {
        if (booking.getBookingStatus() != expectedStatus) {
            throw new IllegalStateException(
                    String.format("Booking must be in %s status. Current status: %s",
                            expectedStatus, booking.getBookingStatus()));
        }
    }

    /**
     * Validates if a booking is in one of the allowed statuses.
     * 
     * @param booking         The booking to check
     * @param allowedStatuses Set of allowed statuses
     * @throws IllegalStateException if booking is not in any of the allowed
     *                               statuses
     */
    public static void requireAnyStatus(Booking booking, BookingStatus... allowedStatuses) {
        BookingStatus currentStatus = booking.getBookingStatus();
        for (BookingStatus allowed : allowedStatuses) {
            if (currentStatus == allowed) {
                return; // Status is allowed
            }
        }
        throw new IllegalStateException(
                String.format("Booking must be in one of %s. Current status: %s",
                        java.util.Arrays.toString(allowedStatuses), currentStatus));
    }

    /**
     * Checks if a booking can be cancelled.
     * 
     * @param booking The booking to check
     * @return true if booking can be cancelled, false otherwise
     */
    public static boolean canCancel(Booking booking) {
        BookingStatus status = booking.getBookingStatus();
        return status == BookingStatus.CREATED ||
                status == BookingStatus.HELD ||
                status == BookingStatus.CONFIRMED;
    }

    /**
     * Checks if a booking can be checked in.
     * 
     * @param booking The booking to check
     * @return true if booking can be checked in, false otherwise
     */
    public static boolean canCheckIn(Booking booking) {
        return booking.getBookingStatus() == BookingStatus.CONFIRMED;
    }

    /**
     * Checks if a booking can be checked out.
     * 
     * @param booking The booking to check
     * @return true if booking can be checked out, false otherwise
     */
    public static boolean canCheckOut(Booking booking) {
        return booking.getBookingStatus() == BookingStatus.CHECKED_IN;
    }

    /**
     * Checks if a booking can initiate transaction (must be CREATED).
     * 
     * @param booking The booking to check
     * @return true if booking can initiate transaction, false otherwise
     */
    public static boolean canInitiateTransaction(Booking booking) {
        return booking.getBookingStatus() == BookingStatus.CREATED;
    }

    /**
     * Checks if a booking counts toward inventory (HELD, CONFIRMED, or CHECKED_IN).
     * 
     * @param booking The booking to check
     * @return true if booking counts in inventory, false otherwise
     */
    public static boolean countsInInventory(Booking booking) {
        BookingStatus status = booking.getBookingStatus();
        return status == BookingStatus.HELD ||
                status == BookingStatus.CONFIRMED ||
                status == BookingStatus.CHECKED_IN;
    }

    /**
     * Internal class to represent a state transition.
     */
    private static class StateTransition {
        private final BookingStatus from;
        private final BookingStatus to;

        public StateTransition(BookingStatus from, BookingStatus to) {
            this.from = from;
            this.to = to;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            StateTransition that = (StateTransition) o;
            return from == that.from && to == that.to;
        }

        @Override
        public int hashCode() {
            return from.hashCode() * 31 + to.hashCode();
        }
    }
}
