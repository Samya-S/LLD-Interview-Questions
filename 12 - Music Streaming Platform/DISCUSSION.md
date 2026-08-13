# MUSIC STREAMING PLATFORM (SPOTIFY) LLD DESIGN STEPS

## STEP-1: DISCUSS FUNCTIONAL REQUIREMENTS

### FUNCTIONAL REQUIREMENTS:

1. As a user, I can register and log in (free or premium tier).
2. As a user, I can search for songs by title, artist name, album, or genre.
3. As a user, I can play songs, albums, and playlists uniformly (same playback interface).
4. As a user, I can control playback: play, pause, resume, and skip to next/previous track.
5. As a user, I can create, update, and delete playlists, and add/remove songs from playlists.
6. As a user, I can start playing songs with minimal initial wait time.
7. As a user, I can listen to songs offline after downloading them.
8. As a premium user, I can download songs for offline playback with ad-free listening and unlimited skips.
9. As a free user, I have limited skips and standard-quality audio with ads.
10. As a user, I can receive song recommendations based on my listening preferences.

### EDGE CASES:

1. Device limit on premium users?
2. Which streaming protocol to use?
3. Quality selection? Adaptive or fixed according to network.
4. Allow sharing of playlists, and other users to deleting it?
5. What kind of formats are supported?
6. No API for upgrade to premium tier?
7. A song can only belong to one album/playlist?
8. On different devices, do we sync?

---

## STEP-2: IDENTIFY CORE ENTITIES

1. **Song** (Core Entity)
   - id: int [PK]
   - songId: String [UNIQUE]
   - title: String
   - artistId: int [FK to Artist]
   - albumId: int [FK to Album, NULLABLE]
   - duration: long (in seconds)
   - genre: String
   - audioUrl: String (URL to audio file on CDN/storage)
   - thumbnailUrl: String (URL to album art/thumbnail)
   - fileSize: long (in bytes)
   - quality: AudioQuality (Enum: STANDARD, HIGH, PREMIUM)
   - format: AudioFormat (Enum: MP3, AAC, WAV)
   - createdAt: LocalDateTime

2. **User**
   - id: int [PK]
   - username: String [UNIQUE]
   - email: String [UNIQUE]
   - name: String
   - subscriptionTier: SubscriptionTier (Enum: FREE, PREMIUM)
   - createdAt: LocalDateTime

3. **Playlist**
   - id: int [PK]
   - playlistId: String [UNIQUE]
   - name: String
   - userId: int [FK to User]
   - isPublic: boolean (false = private, true = public/shared)
   - songIds: List\<String\> (ordered list of song IDs)
   - createdAt: LocalDateTime
   - updatedAt: LocalDateTime

4. **Album**
   - id: int [PK]
   - albumId: String [UNIQUE]
   - title: String
   - artistId: int [FK to Artist]
   - releaseDate: LocalDate
   - thumbnailUrl: String
   - createdAt: LocalDateTime

5. **Artist**
   - id: int [PK]
   - artistId: String [UNIQUE]
   - name: String
   - thumbnailUrl: String
   - createdAt: LocalDateTime

6. **PlaybackSession** (Core Entity)
   - id: int [PK]
   - sessionId: String [UNIQUE]
   - userId: int [FK to User]
   - currentSongId: String [FK to Song]
   - currentPosition: long (playback position in seconds)
   - playbackSource: PlaybackSource (Enum: SONG, ALBUM, PLAYLIST)
   - sourceId: String (songId, albumId, or playlistId depending on source)
   - queue: List\<String\> (ordered list of song IDs to play)
   - shuffleMode: boolean
   - repeatMode: RepeatMode (Enum: OFF, ONE, ALL)
   - status: PlaybackStatus (Enum: PLAYING, PAUSED, STOPPED)
   - deviceId: String (device identifier for multi-device sync)
   - startedAt: LocalDateTime
   - lastUpdatedAt: LocalDateTime

7. **Download** (Core Entity - for offline playback)
   - id: int [PK]
   - downloadId: String [UNIQUE]
   - userId: int [FK to User]
   - songId: String [FK to Song]
   - deviceId: String (device where song is downloaded)
   - downloadStatus: DownloadStatus (Enum: PENDING, IN_PROGRESS, COMPLETED, FAILED)
   - localFilePath: String [NULLABLE] (path to cached file on device)
   - downloadedAt: LocalDateTime [NULLABLE]
   - createdAt: LocalDateTime

8. **ListeningHistory**
   - id: int [PK]
   - userId: int [FK to User]
   - songId: String [FK to Song]
   - playedAt: LocalDateTime
   - playDuration: long (seconds listened)
   - completed: boolean (true if entire song was played)

9. **SubscriptionTier** (Enum)
   - FREE, PREMIUM

10. **AudioQuality** (Enum)
    - STANDARD (128kbps), HIGH (256kbps), PREMIUM (320kbps)

11. **AudioFormat** (Enum)
    - MP3, AAC, WAV

12. **PlaybackSource** (Enum)
    - SONG, ALBUM, PLAYLIST

13. **RepeatMode** (Enum)
    - OFF, ONE, ALL

14. **PlaybackStatus** (Enum)
    - PLAYING, PAUSED, STOPPED

15. **DownloadStatus** (Enum)
    - PENDING, IN_PROGRESS, COMPLETED, FAILED

---

## STEP-3: VISUALISE INTERACTION FLOWS

1. **Search Songs:**
   - GET /api/search?query={query}&type={type} -\> SearchController.search(request) -\>
   - SearchService.search(query, type) -\>
   - SongRepository.findByTitle/Artist/Album(query) -\>
   - Build SearchResponse -\> Return

2. **Play Song/Album/Playlist:**
   - POST /api/playback/play -\> PlaybackController.play(request) -\>
   - PlaybackService.play(request) -\>
   - Validate user subscription tier -\>
   - Build queue from source (single song, album songs, or playlist songs) -\>
   - Create/Update PlaybackSession(status=PLAYING, currentSongId, queue) -\>
   - StreamingService.getStreamUrl(songId, quality) -\>
   - Return stream URL and PlaybackStateResponse

3. **Stream Audio (Chunk-based):**
   - GET /api/stream/{songId}?start={byteOffset}&end={byteEnd} -\>
   - StreamingController.stream(songId, start, end) -\>
   - StreamingService.getChunk(songId, start, end) -\>
   - Check CacheService.getChunk(songId, start, end) -\>
   - If cached: return from cache -\>
   - If not cached: fetch from CDN/storage -\>
   - CacheService.putChunk(songId, start, end, chunkData) -\>
   - Return audio chunk with HTTP 206 Partial Content

4. **Control Playback:**
   - POST /api/playback/pause -\> PlaybackController.pause(sessionId) -\>
   - PlaybackService.pause(sessionId) -\>
   - Update PlaybackSession(status=PAUSED, currentPosition) -\>
   - Return PlaybackStateResponse
   
   - POST /api/playback/resume -\> PlaybackController.resume(sessionId) -\>
   - PlaybackService.resume(sessionId) -\>
   - Update PlaybackSession(status=PLAYING) -\>
   - Return PlaybackStateResponse
   
   - POST /api/playback/skip-next -\> PlaybackController.skipNext(sessionId) -\>
   - PlaybackService.skipNext(sessionId) -\>
   - Get next song from queue (consider shuffle/repeat) -\>
   - Update PlaybackSession(currentSongId, currentPosition=0) -\>
   - Return PlaybackStateResponse

5. **Create Playlist:**
   - POST /api/playlists -\> PlaylistController.createPlaylist(request) -\>
   - PlaylistService.createPlaylist(userId, name, songIds) -\>
   - Validate songs exist -\>
   - Create Playlist(userId, name, songIds) -\>
   - Return Playlist

6. **Update Playlist:**
   - PUT /api/playlists/{playlistId} -\> PlaylistController.updatePlaylist(playlistId, request) -\>
   - PlaylistService.updatePlaylist(playlistId, userId, name, songIds) -\>
   - Acquire lock on playlist (lockKey="playlist_lock_{playlistId}") -\>
   - Validate user owns playlist -\>
   - Update Playlist(name, songIds, updatedAt=now) -\>
   - Release lock -\>
   - Return Playlist

7. **Download Song (Offline):**
   - POST /api/downloads -\> DownloadController.download(request) -\>
   - DownloadService.download(userId, songId, deviceId) -\>
   - Validate premium subscription -\>
   - Validate device limit (max 5 devices) -\>
   - Validate download limit (max 10,000 songs per user) -\>
   - Create Download(status=PENDING, deviceId) -\>
   - Async: StreamingService.downloadFullSong(songId) -\>
   - Store in device cache -\>
   - Update Download(status=COMPLETED, localFilePath) -\>
   - Return Download

8. **Get Recommendations:**
   - GET /api/recommendations -\> RecommendationController.getRecommendations(userId) -\>
   - RecommendationService.getRecommendations(userId) -\>
   - Get user listening history -\>
   - RecommendationStrategy.generate(userId, history) -\>
   - Return RecommendationResponse

9. **Get Playback State:**
   - GET /api/playback/state -\> PlaybackController.getState(sessionId) -\>
   - PlaybackService.getState(sessionId) -\>
   - Fetch PlaybackSession -\>
   - Build PlaybackStateResponse -\>
   - Return (client polls this for sync)

10. **Toggle Shuffle/Repeat:**
    - POST /api/playback/shuffle -\> PlaybackController.toggleShuffle(sessionId, enabled) -\>
    - PlaybackService.toggleShuffle(sessionId, enabled) -\>
    - Update PlaybackSession(shuffleMode=enabled) -\>
    - Return PlaybackStateResponse

11. **Update Playback Position (Periodic):**
    - POST /api/playback/position -\> PlaybackController.updatePosition(sessionId, position) -\>
    - PlaybackService.updatePosition(sessionId, position) -\>
    - Update PlaybackSession(currentPosition=position, lastUpdatedAt=now) -\>
    - Fetch current song from session -\>
    - Get song duration from SongRepository -\>
    - If position \>= duration * 0.9 (90% played): mark as completed -\>
    - Save/Update ListeningHistory(userId, songId, playDuration=position, completed, playedAt=now) -\>
    - Return success (client calls this every 30-60 seconds while playing)

---

## STEP-4: DEFINE CLASS STRUCTURES AND RELATIONSHIPS

### CONTROLLERS:

1. **PlaybackController**
   - PlaybackStateResponse play(PlayRequest request)
   - PlaybackStateResponse pause(String sessionId)
   - PlaybackStateResponse resume(String sessionId)
   - PlaybackStateResponse skipNext(String sessionId)
   - PlaybackStateResponse skipPrevious(String sessionId)
   - PlaybackStateResponse getState(String sessionId)
   - PlaybackStateResponse toggleShuffle(String sessionId, boolean enabled)
   - void setRepeatMode(String sessionId, RepeatMode mode)
   - void updatePosition(String sessionId, long position)

2. **SearchController**
   - SearchResponse search(SearchRequest request)

3. **PlaylistController**
   - Playlist createPlaylist(PlaylistRequest request)
   - Playlist updatePlaylist(String playlistId, PlaylistRequest request)
   - void deletePlaylist(String playlistId, int userId)
   - Playlist addSongs(String playlistId, List\<String\> songIds, int userId)
   - Playlist removeSongs(String playlistId, List\<String\> songIds, int userId)

4. **StreamingController**
   - ResponseEntity\<byte[]\> stream(String songId, long start, long end, int userId)

5. **DownloadController**
   - Download download(DownloadRequest request)
   - List\<Download\> getDownloads(int userId, String deviceId)
   - void deleteDownload(String downloadId, int userId)

6. **RecommendationController**
   - RecommendationResponse getRecommendations(int userId)

### SERVICES:

1. **PlaybackService**
   - PlaybackStateResponse play(PlayRequest request)
   - PlaybackStateResponse pause(String sessionId)
   - PlaybackStateResponse resume(String sessionId)
   - PlaybackStateResponse skipNext(String sessionId)
   - PlaybackStateResponse skipPrevious(String sessionId)
   - PlaybackStateResponse getState(String sessionId)
   - void updatePosition(String sessionId, long position)
   - void saveListeningHistory(int userId, String songId, long playDuration, boolean completed)

2. **StreamingService**
   - String getStreamUrl(String songId, AudioQuality quality)
   - byte[] getChunk(String songId, long start, long end)
   - void downloadFullSong(String songId, String deviceId)

3. **CacheService**
   - Optional\<byte[]\> getChunk(String songId, long start, long end)
   - void putChunk(String songId, long start, long end, byte[] chunk)
   - void evictChunk(String songId, long start, long end)
   - void evictSong(String songId)
   - void evictLRU(int maxSize) (evicts least recently used chunks when cache exceeds maxSize)

4. **PlaylistService**
   - Playlist createPlaylist(int userId, String name, List\<String\> songIds)
   - Playlist updatePlaylist(String playlistId, int userId, String name, List\<String\> songIds)
   - void deletePlaylist(String playlistId, int userId)
   - Playlist addSongs(String playlistId, int userId, List\<String\> songIds)
   - Playlist removeSongs(String playlistId, int userId, List\<String\> songIds)

5. **DownloadService**
   - Download download(int userId, String songId, String deviceId)
   - List\<Download\> getDownloads(int userId, String deviceId)
   - void deleteDownload(String downloadId, int userId)
   - boolean validateDeviceLimit(int userId, String deviceId)
   - boolean validateDownloadLimit(int userId)

6. **SearchService**
   - SearchResponse search(String query, String type)

7. **RecommendationService**
   - RecommendationResponse getRecommendations(int userId)

8. **LockService** (Distributed)
   - boolean acquire(String key, long timeoutMs)
   - void release(String key)

### RECOMMENDATION STRATEGY:
- RecommendationStrategy (Strategy Interface)
  + List\<Song\> generate(int userId, List\<ListeningHistory\> history)
- GenreBasedStrategy (Concrete - recommends songs from same genre as frequently listened)
- PopularityBasedStrategy (Concrete - recommends trending/popular songs)
- CollaborativeFilteringStrategy (Concrete - recommends based on similar users' preferences)
- RecommendationService (Context)
  - RecommendationStrategy strategy
  + void setStrategy(RecommendationStrategy strategy)
  + RecommendationResponse getRecommendations(int userId)

---

## STREAMING PROTOCOL EXPLANATION

Audio streaming requires delivering audio data to clients in a way that enables playback to start quickly while the file is still being downloaded.

1. HTTP Range Requests (Byte Range Requests)
2. HLS (HTTP Live Streaming)
3. DASH (Dynamic Adaptive Streaming over HTTP)

### 1. HTTP Range Requests (Byte-Range Requests)

Description:
- Standard HTTP feature that allows clients to request specific byte ranges of a file
- Server responds with HTTP 206 Partial Content status
- Client can request multiple ranges in parallel for faster buffering

Example Flow:
1. Client requests: GET /api/stream/song123?start=0&end=1048575
   Headers: Range: bytes=0-1048575
2. Server responds: HTTP 206 Partial Content
   Headers: Content-Range: bytes 0-1048575/5242880
   Body: First 1MB chunk of audio file
3. Client starts playing while requesting next chunk: Range: bytes=1048576-2097151
4. Process continues until entire file is streamed

Benefits:
- Simple to implement (standard HTTP)
- Works with any web server/CDN
- No special client libraries needed
- Supports seeking (jump to any position)
- Efficient for on-demand streaming

Limitations:
- Client must manage chunk requests and buffering
- No automatic quality adaptation
- Requires file to be available as single file

### 2. HLS (HTTP Live Streaming)

Description:
- Apple's protocol that breaks audio into small segments (typically 10 seconds)
- Creates a playlist file (.m3u8) listing all segments
- Client downloads segments sequentially and plays them

Example Flow:
1. Client requests: GET /api/stream/song123.m3u8
2. Server returns playlist file:
   #EXTM3U
   #EXTINF:10.0,segment1.ts
   #EXTINF:10.0,segment2.ts
   ...
3. Client downloads segments sequentially: segment1.ts, segment2.ts, etc.
4. Client plays segments as they arrive

Benefits:
- Automatic quality adaptation (multiple quality playlists)
- Built-in error recovery
- Works well for live and on-demand content
- Standardized protocol

Limitations:
- More complex to implement
- Requires segmenting files in advance
- Slight overhead from playlist files

---

## STEP-5: CORE USE CASES AND METHODS

---

## DESIGN PATTERNS AND OOP PRINCIPLES

### DESIGN PATTERNS USED:
1. Repository Pattern - data access abstraction for Song/User/Playlist/Album/Artist/PlaybackSession/Download
2. Service Layer - business logic separation (PlaybackService/StreamingService/CacheService/PlaylistService)
3. Strategy Pattern - RecommendationStrategy (GenreBased, PopularityBased, CollaborativeFiltering)
4. RESTful API Design - clean resource-oriented endpoints

### OOP PRINCIPLES APPLIED:
1. Single Responsibility - each service focuses on one concern (PlaybackService manages playback, StreamingService handles streaming, etc.)
2. Open/Closed - add new recommendation strategies without modifying core logic
3. Encapsulation - playback state transitions only through PlaybackService; cache operations only through CacheService
4. Dependency Inversion - services depend on interfaces (repositories, strategies, cache service)
5. Idempotency - playback control operations can be safely retried

### KEY RELATIONSHIPS:
- Association | uses: PlaybackSession references User via userId
- Association | uses: PlaybackSession references Song via currentSongId
- Association | uses: Playlist references User via userId
- Association | uses: Song references Artist via artistId
- Association | uses: Song references Album via albumId
- Association | uses: Download references User via userId and Song via songId
- Dependency | uses: Services depend on repositories, LockService, CacheService
