package controller;

import domain.RecoveryStatus;
import domain.state.VendingMachineState;
import service.RecoveryService;
import java.util.List;

public class RecoveryController {
    private RecoveryService recoveryService;

    public RecoveryController(RecoveryService recoveryService) {
        this.recoveryService = recoveryService;
        System.out.println("RecoveryController initialized");
    }

    public void checkAndRecover(int machineId) {
        System.out.println("Controller: Checking and performing recovery for machine " + machineId);
        recoveryService.performRecovery(machineId);
    }

    public RecoveryStatus getRecoveryStatus(int machineId) {
        System.out.println("Controller: Getting recovery status for machine " + machineId);
        return recoveryService.getRecoveryStatus(machineId);
    }

    public void markRecoveryComplete(int machineId, int recoveryId) {
        System.out.println("Controller: Marking recovery " + recoveryId + " as complete for machine " + machineId);
        recoveryService.markRecoveryComplete(machineId, recoveryId);
    }

    public void createRecoveryEntry(int machineId, int transactionId, VendingMachineState state) {
        System.out.println("Controller: Creating recovery entry for machine " + machineId +
                ", transaction " + transactionId + ", state " + state.getStateName());
        recoveryService.createRecoveryEntry(machineId, transactionId, state);
    }

    public List<domain.Recovery> getPendingRecoveries(int machineId) {
        System.out.println("Controller: Getting pending recoveries for machine " + machineId);
        return recoveryService.getPendingRecoveries(machineId);
    }

    /**
     * System startup recovery
     */
    public void checkAndRecover() {
        System.out.println("Controller: Checking and recovering during system startup");
        recoveryService.checkAndRecover();
    }
}
