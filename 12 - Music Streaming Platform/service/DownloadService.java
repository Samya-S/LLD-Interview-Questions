package service;

import domain.Download;
import domain.DownloadStatus;
import domain.SubscriptionTier;
import domain.User;
import repository.DownloadRepository;
import repository.UserRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class DownloadService {
    private DownloadRepository downloadRepository;
    private UserRepository userRepository;
    private static final int MAX_DEVICES = 5;
    private static final int MAX_DOWNLOADS = 10000;

    public DownloadService(DownloadRepository downloadRepository, UserRepository userRepository) {
        this.downloadRepository = downloadRepository;
        this.userRepository = userRepository;
    }

    public Download download(String userId, String songId, String deviceId) {
        // Validate premium subscription
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        User user = userOpt.get();
        if (user.getSubscriptionTier() != SubscriptionTier.PREMIUM) {
            throw new IllegalArgumentException("Premium subscription required for downloads");
        }

        // Validate device limit
        if (!validateDeviceLimit(userId, deviceId)) {
            throw new IllegalArgumentException("Device limit exceeded. Max " + MAX_DEVICES + " devices allowed");
        }

        // Validate download limit
        int downloadCount = downloadRepository.countByUserId(userId);
        if (downloadCount >= MAX_DOWNLOADS) {
            throw new IllegalArgumentException("Download limit exceeded. Max " + MAX_DOWNLOADS + " songs allowed");
        }

        String id = UUID.randomUUID().toString();
        String downloadId = "DL_" + id.substring(0, 8);
        long now = System.currentTimeMillis();

        Download download = new Download(id, downloadId, userId, songId, deviceId, 
                                        DownloadStatus.PENDING, null, 0, now);
        
        // TODO: Async download - trigger StreamingService.downloadFullSong()
        // For now, mark as completed immediately
        download.setDownloadStatus(DownloadStatus.COMPLETED);
        download.setLocalFilePath("/cache/" + deviceId + "/" + songId);
        download.setDownloadedAt(now);

        return downloadRepository.save(download);
    }

    public List<Download> getDownloads(String userId, String deviceId) {
        if (deviceId != null) {
            return downloadRepository.findByUserIdAndDeviceId(userId, deviceId);
        }
        return downloadRepository.findByUserId(userId);
    }

    public void deleteDownload(String downloadId, String userId) {
        // TODO: Find download and validate ownership
        downloadRepository.delete(downloadId);
    }

    private boolean validateDeviceLimit(String userId, String deviceId) {
        int deviceCount = downloadRepository.countDistinctDevicesByUserId(userId);
        
        // Check if this device already has downloads
        List<Download> deviceDownloads = downloadRepository.findByUserIdAndDeviceId(userId, deviceId);
        boolean isExistingDevice = !deviceDownloads.isEmpty();
        
        if (isExistingDevice) {
            return true; // Device already registered
        }
        
        return deviceCount < MAX_DEVICES; // New device, check limit
    }
}
