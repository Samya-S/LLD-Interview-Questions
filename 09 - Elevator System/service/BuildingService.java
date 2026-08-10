package service;

import domain.Building;
import domain.SystemState;
import repository.BuildingRepository;
import repository.impl.BuildingRepositoryImpl;

public class BuildingService {
    private final BuildingRepository buildingRepository;
    
    public BuildingService() {
        this.buildingRepository = new BuildingRepositoryImpl();
    }
    
    public Building createBuilding(String name, int minFloor, int maxFloor, int totalElevators) {
        Building building = new Building(name, minFloor, maxFloor, totalElevators);
        return buildingRepository.save(building);
    }
    
    public boolean isValidFloor(String buildingId, int floor) {
        return buildingRepository.findById(buildingId)
                .map(building -> building.isValidFloor(floor))
                .orElse(false);
    }
    
    public void setBuildingSystemState(String buildingId, SystemState state) {
        buildingRepository.findById(buildingId).ifPresent(building -> {
            building.setSystemState(state);
            buildingRepository.save(building);
        });
    }
    
    public boolean isSystemRunning(String buildingId) {
        return buildingRepository.findById(buildingId)
                .map(building -> building.getSystemState() == SystemState.RUNNING)
                .orElse(false);
    }
    
    public Building findById(String buildingId) {
        return buildingRepository.findById(buildingId).orElse(null);
    }
    
    public boolean buildingExists(String buildingId) {
        return buildingRepository.findById(buildingId).isPresent();
    }
}
