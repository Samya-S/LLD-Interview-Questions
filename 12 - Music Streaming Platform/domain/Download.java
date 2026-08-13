package domain;

public class Download {
    private String id;
    private String downloadId;
    private String userId;
    private String songId;
    private String deviceId;
    private DownloadStatus downloadStatus;
    private String localFilePath;
    private long downloadedAt;
    private long createdAt;

    public Download() {
    }

    public Download(String id, String downloadId, String userId, String songId, String deviceId,
                    DownloadStatus downloadStatus, String localFilePath, long downloadedAt, long createdAt) {
        this.id = id;
        this.downloadId = downloadId;
        this.userId = userId;
        this.songId = songId;
        this.deviceId = deviceId;
        this.downloadStatus = downloadStatus;
        this.localFilePath = localFilePath;
        this.downloadedAt = downloadedAt;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(String downloadId) {
        this.downloadId = downloadId;
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

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public DownloadStatus getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(DownloadStatus downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public void setLocalFilePath(String localFilePath) {
        this.localFilePath = localFilePath;
    }

    public long getDownloadedAt() {
        return downloadedAt;
    }

    public void setDownloadedAt(long downloadedAt) {
        this.downloadedAt = downloadedAt;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Download{" +
                "downloadId='" + downloadId + '\'' +
                ", userId='" + userId + '\'' +
                ", songId='" + songId + '\'' +
                ", status=" + downloadStatus +
                '}';
    }
}
