package domain.state;

import domain.TrafficLight;

public class YellowState implements TrafficLightState {
    
    @Override
    public void turnGreen(TrafficLight trafficLight) {
        // Invalid transition: YELLOW → GREEN
        throw new InvalidStateTransitionException("YELLOW", "GREEN");
    }
    
    @Override
    public void turnYellow(TrafficLight trafficLight) {
        // No change - already YELLOW
        System.out.println("Traffic light " + trafficLight.getDirection() + " is already YELLOW");
    }
    
    @Override
    public void turnRed(TrafficLight trafficLight) {
        // Valid transition: YELLOW → RED
        trafficLight.setState(new RedState());
        System.out.println("Traffic light " + trafficLight.getDirection() + " changed from YELLOW to RED");
    }
    
    @Override
    public void turnOff(TrafficLight trafficLight) {
        // Valid transition: YELLOW → OFF
        trafficLight.setState(new OffState());
        System.out.println("Traffic light " + trafficLight.getDirection() + " changed from YELLOW to OFF");
    }
    
    @Override
    public String getStateName() {
        return "YELLOW";
    }
    
    @Override
    public boolean canTransitionTo(TrafficLightState newState) {
        return newState instanceof RedState || newState instanceof OffState;
    }
} 