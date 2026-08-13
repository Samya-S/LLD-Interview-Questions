package repository;

import domain.PlaybackSession;
import java.util.Optional;

public interface PlaybackSessionRepository {
    PlaybackSession save(PlaybackSession session);
    Optional<PlaybackSession> findBySessionId(String sessionId);
    Optional<PlaybackSession> findByUserId(String userId);
}
