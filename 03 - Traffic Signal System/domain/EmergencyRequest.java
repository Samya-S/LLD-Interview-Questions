package domain;

public class EmergencyRequest {
    private int id;
    private int intersectionId;
    private Direction direction;
    private int duration;
    private boolean isActive;
    private long requestTime;

    public EmergencyRequest(int id, int intersectionId, Direction direction, int duration) {
        this.id = id;
        this.intersectionId = intersectionId;
        this.direction = direction;
        this.duration = duration;
        this.isActive = true;
        this.requestTime = System.currentTimeMillis();
        System.out.println("Emergency request created: ID=" + id + ", Intersection=" + intersectionId + 
                          ", Direction=" + direction + ", Duration=" + duration + "s");
    }

    public int getId() { return id; }
    public int getIntersectionId() { return intersectionId; }
    public Direction getDirection() { return direction; }
    public int getDuration() { return duration; }
    public boolean isActive() { return isActive; }
    public long getRequestTime() { return requestTime; }

    public void setActive(boolean active) {
        this.isActive = active;
        System.out.println("Emergency request " + id + " " + (active ? "activated" : "deactivated"));
    }

    public void setDuration(int duration) {
        this.duration = duration;
        System.out.println("Emergency request " + id + " duration updated to " + duration + "s");
    }

    public boolean isExpired() {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - requestTime) / 1000; // Convert to seconds
        return elapsedTime >= duration;
    }

    @Override
    public String toString() {
        return "EmergencyRequest{" +
                "id=" + id +
                ", intersectionId=" + intersectionId +
                ", direction=" + direction +
                ", duration=" + duration +
                ", isActive=" + isActive +
                ", requestTime=" + requestTime +
                '}';
    }
} 