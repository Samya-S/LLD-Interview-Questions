package domain;

public class IntersectionCycle {
    private int intersectionId;
    private int currentPhase; // 0=NORTH, 1=EAST, 2=SOUTH, 3=WEST
    private boolean isPaused;
    private int pausedAtPhase;
    private long phaseStartTime;
    private long pauseStartTime; // NEW: Track when pause started
    private long totalPauseTime; // NEW: Track total pause duration

    public IntersectionCycle(int intersectionId) {
        this.intersectionId = intersectionId;
        this.currentPhase = 0;
        this.isPaused = false;
        this.pausedAtPhase = 0;
        this.phaseStartTime = System.currentTimeMillis();
        this.pauseStartTime = 0;
        this.totalPauseTime = 0;
        System.out.println("Intersection cycle created for intersection: " + intersectionId);
    }

    public int getIntersectionId() { return intersectionId; }
    public int getCurrentPhase() { return currentPhase; }
    public boolean isPaused() { return isPaused; }
    public int getPausedAtPhase() { return pausedAtPhase; }
    public long getPhaseStartTime() { return phaseStartTime; }
    
    // NEW: Get elapsed time in current phase (excluding pause time)
    public long getPhaseElapsedTime() {
        if (isPaused) {
            return pauseStartTime - phaseStartTime;
        } else {
            return System.currentTimeMillis() - phaseStartTime - totalPauseTime;
        }
    }
    
    // NEW: Get remaining time for current phase
    public long getPhaseRemainingTime(int phaseDurationSeconds) {
        long elapsed = getPhaseElapsedTime();
        long remaining = (phaseDurationSeconds * 1000) - elapsed;
        return Math.max(0, remaining);
    }
    
    // NEW: Get total pause time
    public long getTotalPauseTime() {
        if (isPaused) {
            return totalPauseTime + (System.currentTimeMillis() - pauseStartTime);
        }
        return totalPauseTime;
    }

    public void setCurrentPhase(int currentPhase) {
        this.currentPhase = currentPhase;
        this.phaseStartTime = System.currentTimeMillis();
        this.totalPauseTime = 0; // Reset pause time for new phase
        System.out.println("Phase changed to: " + currentPhase + " for intersection " + intersectionId);
    }

    public void setPaused(boolean paused) {
        this.isPaused = paused;
        if (paused) {
            this.pausedAtPhase = this.currentPhase;
            this.pauseStartTime = System.currentTimeMillis();
            System.out.println("Cycle paused at phase: " + this.currentPhase + 
                             " (elapsed: " + getPhaseElapsedTime()/1000 + "s) for intersection " + intersectionId);
        } else {
            // Calculate total pause time when resuming
            this.totalPauseTime += (System.currentTimeMillis() - pauseStartTime);
            System.out.println("Cycle resumed from phase: " + this.pausedAtPhase + 
                             " (remaining: " + getPhaseRemainingTime(30)/1000 + "s) for intersection " + intersectionId);
        }
    }

    public void setPausedAtPhase(int pausedAtPhase) {
        this.pausedAtPhase = pausedAtPhase;
        System.out.println("Paused phase set to: " + pausedAtPhase + " for intersection " + intersectionId);
    }

    public void nextPhase() {
        this.currentPhase = (this.currentPhase + 1) % 4;
        this.phaseStartTime = System.currentTimeMillis();
        this.totalPauseTime = 0; // Reset for new phase
        System.out.println("Advanced to next phase: " + this.currentPhase + " for intersection " + intersectionId);
    }
    
    // NEW: Check if current phase is complete
    public boolean isPhaseComplete(int phaseDurationSeconds) {
        return getPhaseElapsedTime() >= (phaseDurationSeconds * 1000);
    }

    @Override
    public String toString() {
        return "IntersectionCycle{" +
                "intersectionId=" + intersectionId +
                ", currentPhase=" + currentPhase +
                ", isPaused=" + isPaused +
                ", pausedAtPhase=" + pausedAtPhase +
                ", phaseElapsed=" + getPhaseElapsedTime()/1000 + "s" +
                ", totalPauseTime=" + getTotalPauseTime()/1000 + "s" +
                '}';
    }
} 