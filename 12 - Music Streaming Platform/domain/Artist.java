package domain;

public class Artist {
    private String id;
    private String artistId;
    private String name;
    private String thumbnailUrl;
    private long createdAt;

    public Artist() {
    }

    public Artist(String id, String artistId, String name, String thumbnailUrl, long createdAt) {
        this.id = id;
        this.artistId = artistId;
        this.name = name;
        this.thumbnailUrl = thumbnailUrl;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Artist{" +
                "artistId='" + artistId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }
}
