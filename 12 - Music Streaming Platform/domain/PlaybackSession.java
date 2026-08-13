package domain;

import java.util.ArrayList;
import java.util.List;

public class PlaybackSession {
    private String id;
    private String sessionId;
    private String userId;
    private String currentSongId;
    private long currentPosition; // in seconds
    private PlaybackSource playbackSource;
    private String sourceId;
    private List<String> queue;
    private boolean shuffleMode;
    private RepeatMode repeatMode;
    private PlaybackStatus status;
    private String deviceId;
    private long startedAt;
    private long lastUpdatedAt;

    public PlaybackSession() {
        this.queue = new ArrayList<>();
    }

    public PlaybackSession(String id, String sessionId, String userId, String currentSongId, 
                          long currentPosition, PlaybackSource playbackSource, String sourceId,
                          List<String> queue, boolean shuffleMode, RepeatMode repeatMode,
                          PlaybackStatus status, String deviceId, long startedAt, long lastUpdatedAt) {
        this.id = id;
        this.sessionId = sessionId;
        this.userId = userId;
        this.currentSongId = currentSongId;
        this.currentPosition = currentPosition;
        this.playbackSource = playbackSource;
        this.sourceId = sourceId;
        this.queue = queue != null ? new ArrayList<>(queue) : new ArrayList<>();
        this.shuffleMode = shuffleMode;
        this.repeatMode = repeatMode;
        this.status = status;
        this.deviceId = deviceId;
        this.startedAt = startedAt;
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCurrentSongId() {
        return currentSongId;
    }

    public void setCurrentSongId(String currentSongId) {
        this.currentSongId = currentSongId;
    }

    public long getCurrentPosition() {
        return currentPosition;
    }

    public void setCurrentPosition(long currentPosition) {
        this.currentPosition = currentPosition;
    }

    public PlaybackSource getPlaybackSource() {
        return playbackSource;
    }

    public void setPlaybackSource(PlaybackSource playbackSource) {
        this.playbackSource = playbackSource;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public List<String> getQueue() {
        return queue;
    }

    public void setQueue(List<String> queue) {
        this.queue = queue != null ? new ArrayList<>(queue) : new ArrayList<>();
    }

    public boolean isShuffleMode() {
        return shuffleMode;
    }

    public void setShuffleMode(boolean shuffleMode) {
        this.shuffleMode = shuffleMode;
    }

    public RepeatMode getRepeatMode() {
        return repeatMode;
    }

    public void setRepeatMode(RepeatMode repeatMode) {
        this.repeatMode = repeatMode;
    }

    public PlaybackStatus getStatus() {
        return status;
    }

    public void setStatus(PlaybackStatus status) {
        this.status = status;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public long getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(long startedAt) {
        this.startedAt = startedAt;
    }

    public long getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(long lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    @Override
    public String toString() {
        return "PlaybackSession{" +
                "sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                ", currentSongId='" + currentSongId + '\'' +
                ", status=" + status +
                '}';
    }
}
