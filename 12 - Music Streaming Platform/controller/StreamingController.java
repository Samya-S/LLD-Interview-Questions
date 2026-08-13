package controller;

import service.StreamingService;

public class StreamingController {
    private StreamingService streamingService;

    public StreamingController(StreamingService streamingService) {
        this.streamingService = streamingService;
    }

    public byte[] stream(String songId, long start, long end, String userId) {
        // Validate user has access
        // TODO: Add subscription tier check
        return streamingService.getChunk(songId, start, end);
    }
}
