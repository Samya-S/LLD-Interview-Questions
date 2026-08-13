package repository.impl;

import domain.PlaybackSession;
import repository.PlaybackSessionRepository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class PlaybackSessionRepositoryImpl implements PlaybackSessionRepository {
    private Map<String, PlaybackSession> sessionsBySessionId = new ConcurrentHashMap<>();
    private Map<String, String> sessionIdByUserId = new ConcurrentHashMap<>();

    @Override
    public PlaybackSession save(PlaybackSession session) {
        sessionsBySessionId.put(session.getSessionId(), session);
        sessionIdByUserId.put(session.getUserId(), session.getSessionId());
        return session;
    }

    @Override
    public Optional<PlaybackSession> findBySessionId(String sessionId) {
        return Optional.ofNullable(sessionsBySessionId.get(sessionId));
    }

    @Override
    public Optional<PlaybackSession> findByUserId(String userId) {
        String sessionId = sessionIdByUserId.get(userId);
        if (sessionId == null) return Optional.empty();
        return Optional.ofNullable(sessionsBySessionId.get(sessionId));
    }
}
