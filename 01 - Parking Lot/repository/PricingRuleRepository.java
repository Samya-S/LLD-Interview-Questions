package repository;

import domain.PricingRule;
import domain.Vehicle;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PricingRuleRepository {
    private Map<UUID, PricingRule> rules = new ConcurrentHashMap<>();
    private Map<Vehicle.VehicleType, UUID> vehicleTypeToRule = new ConcurrentHashMap<>();

    public PricingRule save(PricingRule rule) {
        rules.put(rule.getId(), rule);
        vehicleTypeToRule.put(rule.getVehicleType(), rule.getId());
        return rule;
    }

    public Optional<PricingRule> findById(UUID ruleId) {
        return Optional.ofNullable(rules.get(ruleId));
    }

    public Optional<PricingRule> findByVehicleType(Vehicle.VehicleType vehicleType) {
        UUID ruleId = vehicleTypeToRule.get(vehicleType);
        return ruleId != null ? Optional.ofNullable(rules.get(ruleId)) : Optional.empty();
    }

    public List<PricingRule> findAll() {
        return new ArrayList<>(rules.values());
    }

    public void update(PricingRule rule) {
        if (rules.containsKey(rule.getId())) {
            rules.put(rule.getId(), rule);
            vehicleTypeToRule.put(rule.getVehicleType(), rule.getId());
        }
    }

    public void delete(UUID ruleId) {
        PricingRule rule = rules.remove(ruleId);
        if (rule != null) {
            vehicleTypeToRule.remove(rule.getVehicleType());
        }
    }

    public void clear() {
        rules.clear();
        vehicleTypeToRule.clear();
    }
} 