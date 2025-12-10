package org.delcom.app.entities;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WatchListTests {
    
    @Test
    @DisplayName("Membuat instance dari kelas WatchList")
    void testMembuatInstanceWatchList() throws Exception {
        UUID userId = UUID.randomUUID();

        // 1. WatchList tipe Movie
        {
            WatchList watchList = new WatchList(
                userId, 
                "Inception", 
                "Movie", 
                "Sci-Fi", 
                9, 
                2010,
                "Watched", // [FIX] String Status
                "Film yang sangat membingungkan tapi seru"
            );

            assert (watchList.getUserId().equals(userId));
            assert (watchList.getTitle().equals("Inception"));
            assert (watchList.getType().equals("Movie"));
            assert (watchList.getGenre().equals("Sci-Fi"));
            assert (watchList.getRating().equals(9));
            assert (watchList.getReleaseYear().equals(2010));
            // [FIX] getStatus
            assert (watchList.getStatus().equals("Watched"));
            assert (watchList.getNotes().equals("Film yang sangat membingungkan tapi seru"));
        }

        // 2. WatchList tipe Series
        {
            WatchList watchList = new WatchList(
                userId, 
                "Breaking Bad", 
                "Series", 
                "Crime", 
                10, 
                2008,
                "Plan to Watch", // [FIX] String Status
                "Best series ever"
            );

            assert (watchList.getTitle().equals("Breaking Bad"));
            assert (watchList.getType().equals("Series"));
            assert (watchList.getStatus().equals("Plan to Watch"));
        }

        // 3. WatchList Default
        {
            WatchList watchList = new WatchList();
            assert (watchList.getId() == null);
            assert (watchList.getStatus() == null); // [FIX]
        }

        // 4. WatchList Setter
        {
            WatchList watchList = new WatchList();
            UUID generatedId = UUID.randomUUID();
            
            watchList.setId(generatedId);
            watchList.setUserId(userId);
            watchList.setTitle("Interstellar");
            watchList.setType("Movie");
            watchList.setGenre("Sci-Fi");
            watchList.setRating(10);
            watchList.setReleaseYear(2014);
            // [FIX] setStatus
            watchList.setStatus("Watching");
            watchList.setNotes("Belum nonton");
            watchList.setCover("/images/interstellar.jpg");
            
            watchList.onCreate();
            watchList.onUpdate();

            assert (watchList.getTitle().equals("Interstellar"));
            // [FIX]
            assert (watchList.getStatus().equals("Watching"));
            assert (watchList.getCreatedAt() != null);
        }
    }
}