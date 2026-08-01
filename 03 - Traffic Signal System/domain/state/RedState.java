package domain.state;

import domain.TrafficLight;

public class RedState implements TrafficLightState {
    
    @Override
    public void turnGreen(TrafficLight trafficLight) {
        // Valid transition: RED → GREEN
        trafficLight.setState(new GreenState());
        System.out.println("Traffic light " + trafficLight.getDirection() + " changed from RED to GREEN");
    }
    
    @Override
    public void turnYellow(TrafficLight trafficLight) {
        // Invalid transition: RED → YELLOW
        throw new InvalidStateTransitionException("RED", "YELLOW");
    }
    
    @Override
    public void turnRed(TrafficLight trafficLight) {
        // No change - already RED
        System.out.println("Traffic light " + trafficLight.getDirection() + " is already RED");
    }
    
    @Override
    public void turnOff(TrafficLight trafficLight) {
        // Valid transition: RED → OFF
        trafficLight.setState(new OffState());
        System.out.println("Traffic light " + trafficLight.getDirection() + " changed from RED to OFF");
    }
    
    @Override
    public String getStateName() {
        return "RED";
    }
    
    @Override
    public boolean canTransitionTo(TrafficLightState newState) {
        return newState instanceof GreenState || newState instanceof OffState;
    }
} 