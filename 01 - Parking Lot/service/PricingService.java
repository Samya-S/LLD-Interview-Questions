package service;

import domain.PricingRule;
import domain.Ticket;
import domain.Vehicle;
import repository.PricingRuleRepository;
import java.util.Optional;

public class PricingService {
    private PricingRuleRepository pricingRuleRepository;

    public PricingService(PricingRuleRepository pricingRuleRepository) {
        this.pricingRuleRepository = pricingRuleRepository;
    }

    public double calculateFee(Ticket ticket) {
        System.out.println("[SERVICE] Calculating fee for ticket: " + ticket.getId());
        
        // For demo purposes, we'll use a dummy vehicle type
        // In a real system, you'd get the vehicle from the ticket
        Vehicle.VehicleType vehicleType = Vehicle.VehicleType.CAR; // Default
        
        Optional<PricingRule> rule = pricingRuleRepository.findByVehicleType(vehicleType);
        if (rule.isEmpty()) {
            throw new IllegalStateException("No pricing rule found for vehicle type: " + vehicleType);
        }

        PricingRule pricingRule = rule.get();
        
        // Calculate both flat and hourly fees
        double flatFee = pricingRule.getFlatRate();
        double hourlyFee = calculateHourlyFee(ticket, pricingRule.getRatePerHour());
        
        // Return the minimum of flat and hourly pricing
        double finalFee = Math.min(flatFee, hourlyFee);
        
        System.out.println("[SERVICE] Flat fee: " + flatFee + ", Hourly fee: " + hourlyFee + ", Final fee: " + finalFee + " for vehicle type: " + vehicleType);
        
        return finalFee;
    }
    
    private double calculateHourlyFee(Ticket ticket, double ratePerHour) {
        java.time.Duration duration = java.time.Duration.between(ticket.getEntryTime(), java.time.LocalDateTime.now());
        long hours = duration.toHours();
        
        // Minimum 1 hour charge
        if (hours < 1) {
            hours = 1;
        }
        
        return hours * ratePerHour;
    }

    public void addPricingRule(PricingRule rule) {
        pricingRuleRepository.save(rule);
    }

    public void updatePricingRule(PricingRule rule) {
        pricingRuleRepository.update(rule);
    }
} 