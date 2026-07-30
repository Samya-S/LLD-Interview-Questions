package controller;

import domain.PricingRule;
import domain.Vehicle;
import service.AdminService;
import java.util.Map;

public class AdminController {
    private AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
        System.out.println("[CONTROLLER] AdminController initialized");
    }

    public void initializeParkingLot() {
        System.out.println("[CONTROLLER] Admin request to initialize parking lot");
        adminService.initializeParkingLot();
        System.out.println("[CONTROLLER] Parking lot initialized successfully by admin");
    }

    public void addFloor(int floorNumber) {
        System.out.println("[CONTROLLER] Admin request to add floor: " + floorNumber);
        adminService.addFloorPublic(floorNumber);
        System.out.println("[CONTROLLER] Floor added successfully by admin");
    }

    public void addSlotsToFloor(int floorNumber, Vehicle.VehicleType slotType, int count) {
        System.out.println("[CONTROLLER] Admin request to add " + count + " " + slotType + " slots to floor " + floorNumber);
        adminService.addSlotsToFloorPublic(floorNumber, slotType, count);
        System.out.println("[CONTROLLER] Slots added successfully by admin");
    }

    public void updatePricingRule(Vehicle.VehicleType vehicleType, double ratePerHour, double flatRate) {
        System.out.println("[CONTROLLER] Admin request to update pricing for " + vehicleType);
        adminService.updatePricingRule(vehicleType, ratePerHour, flatRate);
        System.out.println("[CONTROLLER] Pricing rule updated successfully by admin");
    }
    
    public void updateFlatPricing(Vehicle.VehicleType vehicleType, double flatRate) {
        System.out.println("[CONTROLLER] Admin request to update flat pricing for " + vehicleType + " to " + flatRate);
        adminService.updateFlatPricing(vehicleType, flatRate);
        System.out.println("[CONTROLLER] Flat pricing updated successfully by admin");
    }
    
    public void updateHourlyPricing(Vehicle.VehicleType vehicleType, double ratePerHour) {
        System.out.println("[CONTROLLER] Admin request to update hourly pricing for " + vehicleType + " to " + ratePerHour);
        adminService.updateHourlyPricing(vehicleType, ratePerHour);
        System.out.println("[CONTROLLER] Hourly pricing updated successfully by admin");
    }

    public void addPricingRule(PricingRule rule) {
        System.out.println("[CONTROLLER] Admin request to add new pricing rule for " + rule.getVehicleType());
        adminService.addPricingRule(rule);
        System.out.println("[CONTROLLER] Pricing rule added successfully by admin");
    }

    public Map<String, Object> getParkingStatus() {
        System.out.println("[CONTROLLER] Admin request to get parking status");
        Map<String, Object> status = adminService.getParkingStatus();
        System.out.println("[CONTROLLER] Parking status retrieved successfully by admin");
        return status;
    }
} 