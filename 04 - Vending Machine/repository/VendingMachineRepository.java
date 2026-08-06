package repository;

import domain.VendingMachine;
import java.util.HashMap;
import java.util.Map;

public class VendingMachineRepository {
    private Map<Integer, VendingMachine> machines;
    private int nextMachineId;

    public VendingMachineRepository() {
        this.machines = new HashMap<>();
        this.nextMachineId = 1;
        System.out.println("VendingMachineRepository initialized");
    }

    public VendingMachine findById(int machineId) {
        VendingMachine machine = machines.get(machineId);
        if (machine == null) {
            System.out.println("Repository: Machine not found with ID: " + machineId);
        } else {
            System.out.println("Repository: Found machine with ID: " + machineId);
        }
        return machine;
    }

    public VendingMachine save(VendingMachine machine) {
        if (machine.getId() == 0) {
            machine.setId(nextMachineId++);
        }
        machines.put(machine.getId(), machine);
        System.out.println("Repository: Saved machine with ID: " + machine.getId());
        return machine;
    }

    public void updateInventory(int machineId, int productId, int quantity) {
        VendingMachine machine = findById(machineId);
        if (machine != null) {
            // This would typically update the inventory in the machine
            System.out.println("Repository: Updated inventory for machine " + machineId + 
                             ", product " + productId + ", quantity " + quantity);
        }
    }

    public void updateMachineState(int machineId, String newState) {
        VendingMachine machine = findById(machineId);
        if (machine != null) {
            // This would typically update the machine state
            System.out.println("Repository: Updated machine " + machineId + " state to " + newState);
        }
    }

    public boolean exists(int machineId) {
        return machines.containsKey(machineId);
    }

    public int getTotalMachines() {
        return machines.size();
    }
    
    public java.util.List<VendingMachine> findAll() {
        return new java.util.ArrayList<>(machines.values());
    }
}
