package repository.impl;

import domain.ExternalRequest;
import domain.RequestStatus;
import repository.ExternalRequestRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ExternalRequestRepositoryImpl implements ExternalRequestRepository {
    private final Map<String, ExternalRequest> requests = new ConcurrentHashMap<>();
    
    @Override
    public ExternalRequest save(ExternalRequest request) {
        requests.put(request.getId(), request);
        return request;
    }
    
    @Override
    public List<ExternalRequest> findPendingRequests(String buildingId) {
        return requests.values().stream()
                .filter(r -> r.getBuildingId().equals(buildingId))
                .filter(r -> r.getStatus() == RequestStatus.PENDING)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<ExternalRequest> findQueuedRequests(String buildingId) {
        return requests.values().stream()
                .filter(r -> r.getBuildingId().equals(buildingId))
                .filter(r -> r.getStatus() == RequestStatus.QUEUED)
                .collect(Collectors.toList());
    }
    
    @Override
    public void updateRequestStatus(String requestId, RequestStatus status) {
        ExternalRequest request = requests.get(requestId);
        if (request != null) {
            request.setStatus(status);
        }
    }
    
    @Override
    public Optional<ExternalRequest> findById(String requestId) {
        return Optional.ofNullable(requests.get(requestId));
    }
    
    @Override
    public List<ExternalRequest> findAll() {
        return new ArrayList<>(requests.values());
    }
    
    @Override
    public void deleteById(String requestId) {
        requests.remove(requestId);
    }
}
