package controller;

import domain.Download;
import service.DownloadService;
import java.util.List;

public class DownloadController {
    private DownloadService downloadService;

    public DownloadController(DownloadService downloadService) {
        this.downloadService = downloadService;
    }

    public Download download(String userId, String songId, String deviceId) {
        return downloadService.download(userId, songId, deviceId);
    }

    public List<Download> getDownloads(String userId, String deviceId) {
        return downloadService.getDownloads(userId, deviceId);
    }

    public void deleteDownload(String downloadId, String userId) {
        downloadService.deleteDownload(downloadId, userId);
    }
}
