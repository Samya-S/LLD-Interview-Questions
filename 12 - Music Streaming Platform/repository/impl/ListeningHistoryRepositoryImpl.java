package repository.impl;

import domain.ListeningHistory;
import repository.ListeningHistoryRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ListeningHistoryRepositoryImpl implements ListeningHistoryRepository {
    private Map<String, ListeningHistory> historyById = new ConcurrentHashMap<>();
    private List<ListeningHistory> historyList = new ArrayList<>(); // For ordering

    @Override
    public void save(ListeningHistory history) {
        historyById.put(history.getId(), history);
        historyList.add(history);
    }

    @Override
    public List<ListeningHistory> findByUserId(String userId) {
        return historyList.stream()
                .filter(history -> history.getUserId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<ListeningHistory> findByUserIdOrderByPlayedAtDesc(String userId, int limit) {
        return historyList.stream()
                .filter(history -> history.getUserId().equals(userId))
                .sorted((a, b) -> Long.compare(b.getPlayedAt(), a.getPlayedAt()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public ListeningHistory findByUserIdAndSongIdAndDate(String userId, String songId, long date) {
        // TODO: Implement date-based lookup (same day)
        long dayStart = date - (date % (24 * 60 * 60 * 1000)); // Start of day
        long dayEnd = dayStart + (24 * 60 * 60 * 1000); // End of day
        
        return historyList.stream()
                .filter(history -> history.getUserId().equals(userId) 
                        && history.getSongId().equals(songId)
                        && history.getPlayedAt() >= dayStart 
                        && history.getPlayedAt() < dayEnd)
                .findFirst()
                .orElse(null);
    }
}
