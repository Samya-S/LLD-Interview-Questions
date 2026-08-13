package service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CacheService {
    private Map<String, CacheEntry> cache = new ConcurrentHashMap<>();
    private int maxSize = 1000; // Max number of chunks

    private static class CacheEntry {
        byte[] data;
        long lastAccessed;
        
        CacheEntry(byte[] data) {
            this.data = data;
            this.lastAccessed = System.currentTimeMillis();
        }
    }

    public Optional<byte[]> getChunk(String songId, long start, long end) {
        String key = buildKey(songId, start, end);
        CacheEntry entry = cache.get(key);
        if (entry != null) {
            entry.lastAccessed = System.currentTimeMillis(); // Update LRU
            return Optional.of(entry.data);
        }
        return Optional.empty();
    }

    public void putChunk(String songId, long start, long end, byte[] chunk) {
        if (cache.size() >= maxSize) {
            evictLRU();
        }
        String key = buildKey(songId, start, end);
        cache.put(key, new CacheEntry(chunk));
    }

    public void evictChunk(String songId, long start, long end) {
        String key = buildKey(songId, start, end);
        cache.remove(key);
    }

    public void evictSong(String songId) {
        // Remove all chunks for a song
        cache.entrySet().removeIf(entry -> entry.getKey().startsWith(songId + "_"));
    }

    private void evictLRU() {
        // Find least recently used entry
        Optional<Map.Entry<String, CacheEntry>> lruEntry = cache.entrySet().stream()
                .min(Comparator.comparingLong(e -> e.getValue().lastAccessed));
        
        lruEntry.ifPresent(entry -> cache.remove(entry.getKey()));
    }

    private String buildKey(String songId, long start, long end) {
        return songId + "_" + start + "_" + end;
    }
}
