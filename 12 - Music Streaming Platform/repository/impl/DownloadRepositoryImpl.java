package repository.impl;

import domain.Download;
import repository.DownloadRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DownloadRepositoryImpl implements DownloadRepository {
    private Map<String, Download> downloadsById = new ConcurrentHashMap<>();
    private Map<String, String> downloadIdByUniqueId = new ConcurrentHashMap<>();

    @Override
    public Download save(Download download) {
        downloadsById.put(download.getId(), download);
        downloadIdByUniqueId.put(download.getDownloadId(), download.getId());
        return download;
    }

    @Override
    public Optional<Download> findById(String id) {
        return Optional.ofNullable(downloadsById.get(id));
    }

    @Override
    public List<Download> findByUserId(String userId) {
        return downloadsById.values().stream()
                .filter(download -> download.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Download> findByUserIdAndDeviceId(String userId, String deviceId) {
        return downloadsById.values().stream()
                .filter(download -> download.getUserId().equals(userId) && download.getDeviceId().equals(deviceId))
                .collect(Collectors.toList());
    }

    @Override
    public int countByUserId(String userId) {
        return (int) downloadsById.values().stream()
                .filter(download -> download.getUserId().equals(userId))
                .count();
    }

    @Override
    public void delete(String downloadId) {
        String id = downloadIdByUniqueId.remove(downloadId);
        if (id != null) {
            downloadsById.remove(id);
        }
    }

    @Override
    public int countDistinctDevicesByUserId(String userId) {
        // TODO: Implement efficient distinct device count
        Set<String> devices = downloadsById.values().stream()
                .filter(download -> download.getUserId().equals(userId))
                .map(Download::getDeviceId)
                .collect(Collectors.toSet());
        return devices.size();
    }
}
