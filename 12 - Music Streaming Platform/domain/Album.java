package domain;

public class Album {
    private String id;
    private String albumId;
    private String title;
    private String artistId;
    private String thumbnailUrl;
    private long createdAt;

    public Album() {
    }

    public Album(String id, String albumId, String title, String artistId, String thumbnailUrl, long createdAt) {
        this.id = id;
        this.albumId = albumId;
        this.title = title;
        this.artistId = artistId;
        this.thumbnailUrl = thumbnailUrl;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
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
        return "Album{" +
                "albumId='" + albumId + '\'' +
                ", title='" + title + '\'' +
                ", artistId='" + artistId + '\'' +
                '}';
    }
}
