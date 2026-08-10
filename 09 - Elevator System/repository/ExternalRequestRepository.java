package repository;

import domain.ExternalRequest;
import domain.RequestStatus;
import java.util.List;
import java.util.Optional;

public interface ExternalRequestRepository {
    ExternalRequest save(ExternalRequest request);
    List<ExternalRequest> findPendingRequests(String buildingId);
    List<ExternalRequest> findQueuedRequests(String buildingId);
    void updateRequestStatus(String requestId, RequestStatus status);
    Optional<ExternalRequest> findById(String requestId);
    List<ExternalRequest> findAll();
    void deleteById(String requestId);
}
