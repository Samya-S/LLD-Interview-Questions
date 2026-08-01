package domain.state;

import domain.TrafficLight;

public class GreenState implements TrafficLightState {
    
    @Override
    public void turnGreen(TrafficLight trafficLight) {
        // No change - already GREEN
        System.out.println("Traffic light " + trafficLight.getDirection() + " is already GREEN");
    }
    
    @Override
    public void turnYellow(TrafficLight trafficLight) {
        // Valid transition: GREEN → YELLOW
        trafficLight.setState(new YellowState());
        System.out.println("Traffic light " + trafficLight.getDirection() + " changed from GREEN to YELLOW");
    }
    
    @Override
    public void turnRed(TrafficLight trafficLight) {
        // Invalid transition: GREEN → RED
        throw new InvalidStateTransitionException("GREEN", "RED");
    }
    
    @Override
    public void turnOff(TrafficLight trafficLight) {
        // Valid transition: GREEN → OFF
        trafficLight.setState(new OffState());
        System.out.println("Traffic light " + trafficLight.getDirection() + " changed from GREEN to OFF");
    }
    
    @Override
    public String getStateName() {
        return "GREEN";
    }
    
    @Override
    public boolean canTransitionTo(TrafficLightState newState) {
        return newState instanceof YellowState || newState instanceof OffState;
    }
} 