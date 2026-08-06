package repository;

import domain.Recovery;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class RecoveryRepository {
    private Map<Integer, Recovery> recoveries;
    private int nextRecoveryId;

    public RecoveryRepository() {
        this.recoveries = new HashMap<>();
        this.nextRecoveryId = 1;
        System.out.println("RecoveryRepository initialized");
    }

    public void saveRecovery(Recovery recovery) {
        if (recovery.getId() == 0) {
            recovery.setId(nextRecoveryId++);
        }
        recoveries.put(recovery.getId(), recovery);
        System.out.println("Repository: Saved recovery with ID: " + recovery.getId());
    }

    public Recovery findById(int recoveryId) {
        Recovery recovery = recoveries.get(recoveryId);
        if (recovery == null) {
            System.out.println("Repository: Recovery not found with ID: " + recoveryId);
        } else {
            System.out.println("Repository: Found recovery with ID: " + recoveryId);
        }
        return recovery;
    }

    public List<Recovery> findPendingRecoveries(int machineId) {
        List<Recovery> pendingRecoveries = recoveries.values().stream()
            .filter(r -> r.getVendingMachineId() == machineId && r.getStatus().toString().equals("PENDING"))
            .collect(Collectors.toList());
        System.out.println("Repository: Found " + pendingRecoveries.size() + " pending recoveries for machine " + machineId);
        return pendingRecoveries;
    }

    public void markComplete(int recoveryId) {
        Recovery recovery = findById(recoveryId);
        if (recovery != null) {
            recovery.markComplete();
            System.out.println("Repository: Marked recovery " + recoveryId + " as complete");
        }
    }

    public List<Recovery> findAll() {
        return new ArrayList<>(recoveries.values());
    }

    public int getTotalRecoveries() {
        return recoveries.size();
    }
}
