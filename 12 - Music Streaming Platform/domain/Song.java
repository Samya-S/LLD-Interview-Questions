package domain;

public class Song {
    private String id;
    private String songId;
    private String title;
    private String artistId;
    private String albumId;
    private long duration; // in seconds
    private String genre;
    private String audioUrl;
    private String thumbnailUrl;
    private long fileSize; // in bytes
    private AudioQuality quality;
    private AudioFormat format;
    private long createdAt;

    public Song() {
    }

    public Song(String id, String songId, String title, String artistId, String albumId, 
                long duration, String genre, String audioUrl, String thumbnailUrl, 
                long fileSize, AudioQuality quality, AudioFormat format, long createdAt) {
        this.id = id;
        this.songId = songId;
        this.title = title;
        this.artistId = artistId;
        this.albumId = albumId;
        this.duration = duration;
        this.genre = genre;
        this.audioUrl = audioUrl;
        this.thumbnailUrl = thumbnailUrl;
        this.fileSize = fileSize;
        this.quality = quality;
        this.format = format;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSongId() {
        return songId;
    }

    public void setSongId(String songId) {
        this.songId = songId;
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

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public AudioQuality getQuality() {
        return quality;
    }

    public void setQuality(AudioQuality quality) {
        this.quality = quality;
    }

    public AudioFormat getFormat() {
        return format;
    }

    public void setFormat(AudioFormat format) {
        this.format = format;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Song{" +
                "songId='" + songId + '\'' +
                ", title='" + title + '\'' +
                ", artistId='" + artistId + '\'' +
                ", duration=" + duration +
                '}';
    }
}
