package service;

import domain.*;
import repository.*;
import java.util.*;
import java.util.UUID;

public class PlaybackService {
    private PlaybackSessionRepository sessionRepository;
    private SongRepository songRepository;
    private AlbumRepository albumRepository;
    private PlaylistRepository playlistRepository;
    private UserRepository userRepository;
    private ListeningHistoryRepository listeningHistoryRepository;
    private StreamingService streamingService;
    private static final double COMPLETION_THRESHOLD = 0.9; // 90%

    public PlaybackService(PlaybackSessionRepository sessionRepository, SongRepository songRepository,
                          AlbumRepository albumRepository, PlaylistRepository playlistRepository,
                          UserRepository userRepository, ListeningHistoryRepository listeningHistoryRepository,
                          StreamingService streamingService) {
        this.sessionRepository = sessionRepository;
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.playlistRepository = playlistRepository;
        this.userRepository = userRepository;
        this.listeningHistoryRepository = listeningHistoryRepository;
        this.streamingService = streamingService;
    }

    public PlaybackStateResponse play(String userId, PlaybackSource sourceType, String sourceId, Long startPosition) {
        // Validate user
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found: " + userId);
        }

        // Build queue based on source type
        List<String> queue = buildQueue(sourceType, sourceId);
        if (queue.isEmpty()) {
            throw new IllegalArgumentException("No songs found for source: " + sourceId);
        }

        // Apply shuffle if needed (simplified - would randomize in production)
        // TODO: Implement proper shuffle algorithm like Fisher-Yates

        String currentSongId = queue.get(0);
        long position = startPosition != null ? startPosition : 0;

        // Create or update session
        // Currently we are having session per user and not per device
        Optional<PlaybackSession> existingSession = sessionRepository.findByUserId(userId);
        PlaybackSession session;
        
        if (existingSession.isPresent()) {
            session = existingSession.get();
            session.setCurrentSongId(currentSongId);
            session.setCurrentPosition(position);
            session.setQueue(queue);
            session.setPlaybackSource(sourceType);
            session.setSourceId(sourceId);
            session.setStatus(PlaybackStatus.PLAYING);
            session.setLastUpdatedAt(System.currentTimeMillis());
        } else {
            String id = UUID.randomUUID().toString();
            String sessionId = "SESS_" + id.substring(0, 8);
            long now = System.currentTimeMillis();
            
            session = new PlaybackSession(id, sessionId, userId, currentSongId, position,
                    sourceType, sourceId, queue, false, RepeatMode.OFF, 
                    PlaybackStatus.PLAYING, "device_" + userId, now, now);
        }

        session = sessionRepository.save(session);

        // Get stream URL
        String streamUrl = streamingService.getStreamUrl(currentSongId, userId);

        return buildPlaybackStateResponse(session, streamUrl);
    }

    public PlaybackStateResponse pause(String sessionId) {
        PlaybackSession session = getSession(sessionId);
        // State Pattern can check for transition 
        // User 
        session.setStatus(PlaybackStatus.PAUSED);
        session.setLastUpdatedAt(System.currentTimeMillis());
        session = sessionRepository.save(session);
        return buildPlaybackStateResponse(session, null);
    }

    public PlaybackStateResponse resume(String sessionId) {
        PlaybackSession session = getSession(sessionId);
        session.setStatus(PlaybackStatus.PLAYING);
        session.setLastUpdatedAt(System.currentTimeMillis());
        session = sessionRepository.save(session);
        return buildPlaybackStateResponse(session, null);
    }

    public PlaybackStateResponse skipNext(String sessionId) {
        PlaybackSession session = getSession(sessionId);
        
        // Save listening history for current song for recommendation algorithms
        // Do it in the BG to unblock request handling thread
        saveListeningHistory(session.getUserId(), session.getCurrentSongId(), 
                           session.getCurrentPosition(), false);

        // Get next song
        String nextSongId = getNextSong(session);
        if (nextSongId == null) {
            session.setStatus(PlaybackStatus.STOPPED);
        } else {
            session.setCurrentSongId(nextSongId);
            session.setCurrentPosition(0);
        }
        session.setLastUpdatedAt(System.currentTimeMillis());
        session = sessionRepository.save(session);

        String streamUrl = nextSongId != null ? streamingService.getStreamUrl(nextSongId, session.getUserId()) : null;
        return buildPlaybackStateResponse(session, streamUrl);
    }

    public PlaybackStateResponse skipPrevious(String sessionId) {
        PlaybackSession session = getSession(sessionId);
        
        // Get previous song (simplified - would need to track history)
        // TODO: Implement proper previous song logic in future iterations 
        String prevSongId = getPreviousSong(session);
        if (prevSongId != null) {
            session.setCurrentSongId(prevSongId);
            session.setCurrentPosition(0);
        }
        session.setLastUpdatedAt(System.currentTimeMillis());
        session = sessionRepository.save(session);

        String streamUrl = prevSongId != null ? streamingService.getStreamUrl(prevSongId, session.getUserId()) : null;
        return buildPlaybackStateResponse(session, streamUrl);
    }

    public PlaybackStateResponse getState(String sessionId) {
        PlaybackSession session = getSession(sessionId);
        return buildPlaybackStateResponse(session, null);
    }

    public void updatePosition(String sessionId, long position) {
        PlaybackSession session = getSession(sessionId);
        session.setCurrentPosition(position);
        session.setLastUpdatedAt(System.currentTimeMillis());
        sessionRepository.save(session);

        // Save/update listening history
        Optional<Song> songOpt = songRepository.findBySongId(session.getCurrentSongId());
        if (songOpt.isPresent()) {
            Song song = songOpt.get();
            boolean completed = position >= (song.getDuration() * COMPLETION_THRESHOLD);
            saveListeningHistory(session.getUserId(), session.getCurrentSongId(), position, completed);
        }
    }

    public PlaybackStateResponse toggleShuffle(String sessionId, boolean enabled) {
        // Enabled is true 
        // Shuffle the queue you have in the session 
        PlaybackSession session = getSession(sessionId);
        session.setShuffleMode(enabled);
        session.setLastUpdatedAt(System.currentTimeMillis());
        session = sessionRepository.save(session);
        return buildPlaybackStateResponse(session, null);
    }

    public PlaybackStateResponse setRepeatMode(String sessionId, RepeatMode mode) {
        PlaybackSession session = getSession(sessionId);
        session.setRepeatMode(mode);
        session.setLastUpdatedAt(System.currentTimeMillis());
        session = sessionRepository.save(session);
        return buildPlaybackStateResponse(session, null);
    }

    private List<String> buildQueue(PlaybackSource sourceType, String sourceId) {
        switch (sourceType) {
            case SONG:
                return Collections.singletonList(sourceId);
            case ALBUM:
                return buildAlbumQueue(sourceId);
            case PLAYLIST:
                return buildPlaylistQueue(sourceId);
            default:
                return Collections.emptyList();
        }
    }

    private List<String> buildAlbumQueue(String albumId) {
        Optional<Album> albumOpt = albumRepository.findByAlbumId(albumId);
        if (albumOpt.isEmpty()) {
            return Collections.emptyList();
        }
        List<Song> songs = songRepository.findByAlbumId(albumId);
        return songs.stream().map(Song::getSongId).collect(java.util.stream.Collectors.toList());
    }

    private List<String> buildPlaylistQueue(String playlistId) {
        Optional<Playlist> playlistOpt = playlistRepository.findByPlaylistId(playlistId);
        if (playlistOpt.isEmpty()) {
            return Collections.emptyList();
        }
        return new ArrayList<>(playlistOpt.get().getSongIds());
    }

    private String getNextSong(PlaybackSession session) {
        List<String> queue = session.getQueue();
        if (queue.isEmpty()) return null;

        int currentIndex = queue.indexOf(session.getCurrentSongId());
        if (currentIndex == -1) return queue.get(0);

        switch (session.getRepeatMode()) {
            case ONE:
                return session.getCurrentSongId();
            case OFF:
            default:
                if (currentIndex + 1 < queue.size()) {
                    return queue.get(currentIndex + 1);
                }
                return null;
        }
    }

    // Ask the interviewer if they want to have the repeat mode considered. 
    // If not we just find the previous and go to it
    private String getPreviousSong(PlaybackSession session) {
        List<String> queue = session.getQueue();
        if (queue.isEmpty()) return null;

        int currentIndex = queue.indexOf(session.getCurrentSongId());
        if (currentIndex <= 0) return null;

        return queue.get(currentIndex - 1);
    }

    private void saveListeningHistory(String userId, String songId, long playDuration, boolean completed) {
        long now = System.currentTimeMillis();
        
        // Check if history exists for today
        ListeningHistory existing = listeningHistoryRepository.findByUserIdAndSongIdAndDate(userId, songId, now);
        
        if (existing != null) {
            // Update existing
            existing.setPlayDuration(playDuration);
            existing.setCompleted(completed);
            existing.setPlayedAt(now);
            listeningHistoryRepository.save(existing);
        } else {
            // Create new
            String id = UUID.randomUUID().toString();
            ListeningHistory history = new ListeningHistory(id, userId, songId, now, playDuration, completed);
            listeningHistoryRepository.save(history);
        }
    }

    private PlaybackSession getSession(String sessionId) {
        Optional<PlaybackSession> sessionOpt = sessionRepository.findBySessionId(sessionId);
        if (sessionOpt.isEmpty()) {
            throw new IllegalArgumentException("Session not found: " + sessionId);
        }
        return sessionOpt.get();
    }

    private PlaybackStateResponse buildPlaybackStateResponse(PlaybackSession session, String streamUrl) {
        PlaybackStateResponse response = new PlaybackStateResponse();
        response.setSessionId(session.getSessionId());
        
        Optional<Song> songOpt = songRepository.findBySongId(session.getCurrentSongId());
        if (songOpt.isPresent()) {
            response.setCurrentSong(songOpt.get());
        }
        
        response.setCurrentPosition(session.getCurrentPosition());
        response.setQueue(session.getQueue().stream()
                .map(songId -> songRepository.findBySongId(songId).orElse(null))
                .filter(Objects::nonNull)
                .collect(java.util.stream.Collectors.toList()));
        response.setShuffleMode(session.isShuffleMode());
        response.setRepeatMode(session.getRepeatMode());
        response.setStatus(session.getStatus());
        response.setStreamUrl(streamUrl);
        
        return response;
    }

    public static class PlaybackStateResponse {
        private String sessionId;
        private Song currentSong;
        private long currentPosition;
        private List<Song> queue;
        private boolean shuffleMode;
        private RepeatMode repeatMode;
        private PlaybackStatus status;
        private String streamUrl;

        // Getters and setters
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public Song getCurrentSong() { return currentSong; }
        public void setCurrentSong(Song currentSong) { this.currentSong = currentSong; }
        public long getCurrentPosition() { return currentPosition; }
        public void setCurrentPosition(long currentPosition) { this.currentPosition = currentPosition; }
        public List<Song> getQueue() { return queue; }
        public void setQueue(List<Song> queue) { this.queue = queue; }
        public boolean isShuffleMode() { return shuffleMode; }
        public void setShuffleMode(boolean shuffleMode) { this.shuffleMode = shuffleMode; }
        public RepeatMode getRepeatMode() { return repeatMode; }
        public void setRepeatMode(RepeatMode repeatMode) { this.repeatMode = repeatMode; }
        public PlaybackStatus getStatus() { return status; }
        public void setStatus(PlaybackStatus status) { this.status = status; }
        public String getStreamUrl() { return streamUrl; }
        public void setStreamUrl(String streamUrl) { this.streamUrl = streamUrl; }
    }
}
