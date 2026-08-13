package repository;

import domain.Download;
import java.util.List;
import java.util.Optional;

public interface DownloadRepository {
    Download save(Download download);
    Optional<Download> findById(String id);
    List<Download> findByUserId(String userId);
    List<Download> findByUserIdAndDeviceId(String userId, String deviceId);
    int countByUserId(String userId);
    void delete(String downloadId);
    // TODO: Implement countDistinctDevicesByUserId for device limit validation
    int countDistinctDevicesByUserId(String userId);
}
