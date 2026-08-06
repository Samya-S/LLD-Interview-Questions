package domain.state;

public class InvalidStateTransitionException extends RuntimeException {
    public InvalidStateTransitionException(String message) {
        super(message);
    }
    
    public InvalidStateTransitionException(String currentState, String attemptedTransition) {
        super("Invalid transition from " + currentState + " to " + attemptedTransition);
    }
}
