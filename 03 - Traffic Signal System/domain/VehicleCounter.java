package domain;

public class VehicleCounter {
    private Direction direction;
    private int count;
    private long lastUpdate;

    public VehicleCounter(Direction direction) {
        this.direction = direction;
        this.count = 0;
        this.lastUpdate = System.currentTimeMillis();
        System.out.println("Vehicle counter created for direction: " + direction);
    }

    public Direction getDirection() { return direction; }
    public int getCount() { return count; }
    public long getLastUpdate() { return lastUpdate; }

    public void setCount(int count) {
        this.count = count;
        this.lastUpdate = System.currentTimeMillis();
        System.out.println("Vehicle count updated for " + direction + ": " + count);
    }

    public void incrementCount() {
        this.count++;
        this.lastUpdate = System.currentTimeMillis();
        System.out.println("Vehicle count incremented for " + direction + ": " + count);
    }

    public void resetCount() {
        this.count = 0;
        this.lastUpdate = System.currentTimeMillis();
        System.out.println("Vehicle count reset for " + direction);
    }

    @Override
    public String toString() {
        return "VehicleCounter{" +
                "direction=" + direction +
                ", count=" + count +
                ", lastUpdate=" + lastUpdate +
                '}';
    }
} 