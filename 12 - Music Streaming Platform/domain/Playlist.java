package domain;

import java.util.ArrayList;
import java.util.List;

public class Playlist {
    private String id;
    private String playlistId;
    private String name;
    private String userId;
    private boolean isPublic;
    private List<String> songIds;
    private long createdAt;
    private long updatedAt;

    public Playlist() {
        this.songIds = new ArrayList<>();
    }

    public Playlist(String id, String playlistId, String name, String userId, boolean isPublic, 
                    List<String> songIds, long createdAt, long updatedAt) {
        this.id = id;
        this.playlistId = playlistId;
        this.name = name;
        this.userId = userId;
        this.isPublic = isPublic;
        this.songIds = songIds != null ? new ArrayList<>(songIds) : new ArrayList<>();
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(String playlistId) {
        this.playlistId = playlistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public List<String> getSongIds() {
        return songIds;
    }

    public void setSongIds(List<String> songIds) {
        this.songIds = songIds != null ? new ArrayList<>(songIds) : new ArrayList<>();
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(long updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Playlist{" +
                "playlistId='" + playlistId + '\'' +
                ", name='" + name + '\'' +
                ", userId='" + userId + '\'' +
                ", songCount=" + songIds.size() +
                '}';
    }
}
