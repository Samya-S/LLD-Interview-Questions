package controller;

import domain.PlaybackSource;
import domain.RepeatMode;
import service.PlaybackService;
import service.PlaybackService.PlaybackStateResponse;

public class PlaybackController {
    private PlaybackService playbackService;

    public PlaybackController(PlaybackService playbackService) {
        this.playbackService = playbackService;
    }

    public PlaybackStateResponse play(String userId, PlaybackSource sourceType, String sourceId, Long startPosition) {
        return playbackService.play(userId, sourceType, sourceId, startPosition);
    }

    public PlaybackStateResponse pause(String sessionId) {
        return playbackService.pause(sessionId);
    }

    public PlaybackStateResponse resume(String sessionId) {
        return playbackService.resume(sessionId);
    }

    public PlaybackStateResponse skipNext(String sessionId) {
        return playbackService.skipNext(sessionId);
    }

    public PlaybackStateResponse skipPrevious(String sessionId) {
        return playbackService.skipPrevious(sessionId);
    }

    public PlaybackStateResponse getState(String sessionId) {
        return playbackService.getState(sessionId);
    }

    public PlaybackStateResponse toggleShuffle(String sessionId, boolean enabled) {
        return playbackService.toggleShuffle(sessionId, enabled);
    }

    public PlaybackStateResponse setRepeatMode(String sessionId, RepeatMode mode) {
        return playbackService.setRepeatMode(sessionId, mode);
    }

    // Called in constant intervals. 
    public void updatePosition(String sessionId, long position) {
        playbackService.updatePosition(sessionId, position);
    }
}
