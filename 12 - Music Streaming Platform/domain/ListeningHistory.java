package domain;

public class ListeningHistory {
    private String id;
    private String userId;
    private String songId;
    private long playedAt;
    private long playDuration; // seconds listened
    private boolean completed;

    public ListeningHistory() {
    }

    public ListeningHistory(String id, String userId, String songId, long playedAt, long playDuration, boolean completed) {
        this.id = id;
        this.userId = userId;
        this.songId = songId;
        this.playedAt = playedAt;
        this.playDuration = playDuration;
        this.completed = completed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
    }

    public long getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(long playedAt) {
        this.playedAt = playedAt;
    }

    public long getPlayDuration() {
        return playDuration;
    }

    public void setPlayDuration(long playDuration) {
        this.playDuration = playDuration;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    @Override
    public String toString() {
        return "ListeningHistory{" +
                "userId='" + userId + '\'' +
                ", songId='" + songId + '\'' +
                ", playDuration=" + playDuration +
                ", completed=" + completed +
                '}';
    }
}
