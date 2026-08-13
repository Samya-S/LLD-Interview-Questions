package main;

import controller.*;
import domain.*;
import repository.*;
import repository.impl.*;
import service.*;
import service.strategy.GenreBasedStrategy;
import service.strategy.RecommendationStrategy;

public class MusicStreamingSimulation {
    public static void main(String[] args) {
        // Initialize repositories
        UserRepository userRepository = new UserRepositoryImpl();
        SongRepository songRepository = new SongRepositoryImpl();
        ArtistRepository artistRepository = new ArtistRepositoryImpl();
        AlbumRepository albumRepository = new AlbumRepositoryImpl();
        PlaylistRepository playlistRepository = new PlaylistRepositoryImpl();
        PlaybackSessionRepository sessionRepository = new PlaybackSessionRepositoryImpl();
        DownloadRepository downloadRepository = new DownloadRepositoryImpl();
        ListeningHistoryRepository historyRepository = new ListeningHistoryRepositoryImpl();

        // Initialize services
        LockService lockService = new LockService();
        CacheService cacheService = new CacheService();
        StreamingService streamingService = new StreamingService(songRepository, userRepository, cacheService);
        PlaybackService playbackService = new PlaybackService(sessionRepository, songRepository,
                albumRepository, playlistRepository, userRepository, historyRepository, streamingService);
        SearchService searchService = new SearchService(songRepository, artistRepository, albumRepository);
        PlaylistService playlistService = new PlaylistService(playlistRepository, songRepository, lockService);
        DownloadService downloadService = new DownloadService(downloadRepository, userRepository);
        RecommendationStrategy recommendationStrategy = new GenreBasedStrategy(songRepository);
        RecommendationService recommendationService = new RecommendationService(historyRepository, recommendationStrategy);

        // Initialize controllers
        PlaybackController playbackController = new PlaybackController(playbackService);
        SearchController searchController = new SearchController(searchService);
        PlaylistController playlistController = new PlaylistController(playlistService);
        StreamingController streamingController = new StreamingController(streamingService);
        DownloadController downloadController = new DownloadController(downloadService);
        RecommendationController recommendationController = new RecommendationController(recommendationService);

        // Setup test data
        setupTestData(userRepository, songRepository, artistRepository, albumRepository);

        // Simulate flows
        System.out.println("=== Music Streaming App Simulation ===\n");

        // Flow 1: User Registration
        System.out.println("1. User Registration:");
        User user1 = createUser(userRepository, "user1", "user1@example.com", "John Doe", SubscriptionTier.FREE);
        User user2 = createUser(userRepository, "user2", "user2@example.com", "Jane Smith", SubscriptionTier.PREMIUM);
        System.out.println("Created users: " + user1.getUsername() + " (FREE), " + user2.getUsername() + " (PREMIUM)\n");

        // Flow 2: Search
        System.out.println("2. Search Songs:");
        SearchService.SearchResponse searchResult = searchController.search("Love", "SONG");
        System.out.println("Found " + searchResult.getSongs().size() + " songs\n");

        // Flow 3: Play Song
        System.out.println("3. Play Song:");
        if (!searchResult.getSongs().isEmpty()) {
            String songId = searchResult.getSongs().get(0).getSongId();
            PlaybackService.PlaybackStateResponse playbackState = playbackController.play(
                    user1.getId(), PlaybackSource.SONG, songId, null);
            System.out.println("Playing: " + playbackState.getCurrentSong().getTitle());
            System.out.println("Stream URL: " + playbackState.getStreamUrl() + "\n");
        }

        // Flow 4: Create Playlist
        System.out.println("4. Create Playlist:");
        if (!searchResult.getSongs().isEmpty()) {
            String songId1 = searchResult.getSongs().get(0).getSongId();
            String songId2 = searchResult.getSongs().size() > 1 ? searchResult.getSongs().get(1).getSongId() : songId1;
            Playlist playlist = playlistController.createPlaylist(user1.getId(), "My Favorites", 
                    java.util.Arrays.asList(songId1, songId2));
            System.out.println("Created playlist: " + playlist.getName() + " with " + 
                    playlist.getSongIds().size() + " songs\n");
        }

        // Flow 5: Download (Premium only)
        System.out.println("5. Download Song (Premium):");
        if (!searchResult.getSongs().isEmpty()) {
            try {
                String songId = searchResult.getSongs().get(0).getSongId();
                Download download = downloadController.download(user2.getId(), songId, "device1");
                System.out.println("Downloaded: " + download.getDownloadId() + " (Status: " + 
                        download.getDownloadStatus() + ")\n");
            } catch (Exception e) {
                System.out.println("Download failed: " + e.getMessage() + "\n");
            }
        }

        // Flow 6: Position Update
        System.out.println("6. Update Playback Position:");
        if (!searchResult.getSongs().isEmpty()) {
            String songId = searchResult.getSongs().get(0).getSongId();
            PlaybackService.PlaybackStateResponse state = playbackController.play(
                    user1.getId(), PlaybackSource.SONG, songId, null);
            playbackController.updatePosition(state.getSessionId(), 30);
            System.out.println("Updated position to 30 seconds\n");
        }

        // Flow 7: Recommendations
        System.out.println("7. Get Recommendations:");
        java.util.List<Song> recommendations = recommendationController.getRecommendations(user1.getId());
        System.out.println("Got " + recommendations.size() + " recommendations\n");

        System.out.println("=== Simulation Complete ===");
    }

    private static User createUser(UserRepository userRepository, String username, String email, 
                                  String name, SubscriptionTier tier) {
        String id = java.util.UUID.randomUUID().toString();
        long now = System.currentTimeMillis();
        User user = new User(id, username, email, name, tier, now);
        return userRepository.save(user);
    }

    private static void setupTestData(UserRepository userRepository, SongRepository songRepository,
                                     ArtistRepository artistRepository, AlbumRepository albumRepository) {
        // Create artist
        String artistId = java.util.UUID.randomUUID().toString();
        String artistUniqueId = "ART_" + artistId.substring(0, 8);
        Artist artist = new Artist(artistId, artistUniqueId, "The Beatles", 
                "https://example.com/beatles.jpg", System.currentTimeMillis());
        artistRepository.save(artist);

        // Create album
        String albumId = java.util.UUID.randomUUID().toString();
        String albumUniqueId = "ALB_" + albumId.substring(0, 8);
        Album album = new Album(albumId, albumUniqueId, "Abbey Road", artistUniqueId,
                "https://example.com/abbeyroad.jpg", System.currentTimeMillis());
        albumRepository.save(album);

        // Create songs
        for (int i = 1; i <= 5; i++) {
            String songId = java.util.UUID.randomUUID().toString();
            String songUniqueId = "SONG_" + songId.substring(0, 8);
            Song song = new Song(songId, songUniqueId, "Love Song " + i, artistUniqueId, albumUniqueId,
                    180 + i * 10, "Rock", "https://example.com/audio/" + songUniqueId + ".mp3",
                    "https://example.com/thumb/" + songUniqueId + ".jpg", 5000000L,
                    AudioQuality.HIGH, AudioFormat.MP3, System.currentTimeMillis());
            songRepository.save(song);
        }
    }
}
