package domain;

import domain.state.TrafficLightState;
import domain.state.RedState;

public class TrafficLight {
    private Direction direction;
    private TrafficLightState currentState;

    public TrafficLight(Direction direction) {
        this.direction = direction;
        this.currentState = new RedState(); // Start with RED state
        System.out.println("Traffic light created for direction: " + direction + " in RED state");
    }

    public Direction getDirection() { 
        return direction; 
    }
    
    public TrafficLightState getCurrentState() { 
        return currentState; 
    }

    public void setState(TrafficLightState newState) {
        this.currentState = newState;
    }
    
    public void turnGreen() {
        currentState.turnGreen(this);
    }
    
    public void turnYellow() {
        currentState.turnYellow(this);
    }
    
    public void turnRed() {
        currentState.turnRed(this);
    }
    
    public void turnOff() {
        currentState.turnOff(this);
    }
    
    public String getCurrentStateName() {
        return currentState.getStateName();
    }
    
    public boolean canTransitionTo(TrafficLightState newState) {
        return currentState.canTransitionTo(newState);
    }

    @Override
    public String toString() {
        return "TrafficLight{" +
                "direction=" + direction +
                ", state=" + currentState.getStateName() +
                '}';
    }
} 