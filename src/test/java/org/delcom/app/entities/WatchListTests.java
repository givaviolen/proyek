package org.delcom.app.entities;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class WatchListTests {
    
    @Test
    @DisplayName("Membuat instance dari kelas WatchList")
    void testMembuatInstanceWatchList() throws Exception {
        UUID userId = UUID.randomUUID();

        // 1. WatchList tipe Movie (Constructor Parameter)
        {
            WatchList watchList = new WatchList(
                userId, 
                "Inception", 
                "Movie", 
                "Sci-Fi", 
                9, 
                2010,
                true,
                "Film yang sangat membingungkan tapi seru" // notes
            );

            assert (watchList.getUserId().equals(userId));
            assert (watchList.getTitle().equals("Inception"));
            assert (watchList.getType().equals("Movie"));
            assert (watchList.getGenre().equals("Sci-Fi"));
            assert (watchList.getRating().equals(9));
            assert (watchList.getReleaseYear().equals(2010));
            assert (watchList.getIsWatched().equals(true));
            assert (watchList.getNotes().equals("Film yang sangat membingungkan tapi seru"));
        }

        // 2. WatchList tipe Series (Constructor Parameter)
        {
            WatchList watchList = new WatchList(
                userId, 
                "Breaking Bad", 
                "Series", 
                "Crime", 
                10, 
                2008,
                true,
                "Best series ever"
            );

            assert (watchList.getUserId().equals(userId));
            assert (watchList.getTitle().equals("Breaking Bad"));
            assert (watchList.getType().equals("Series"));
            assert (watchList.getGenre().equals("Crime"));
            assert (watchList.getRating().equals(10));
            assert (watchList.getReleaseYear().equals(2008));
            assert (watchList.getIsWatched().equals(true));
            assert (watchList.getNotes().equals("Best series ever"));
        }

        // 3. WatchList dengan nilai default (Constructor Kosong)
        {
            WatchList watchList = new WatchList();

            assert (watchList.getId() == null);
            assert (watchList.getUserId() == null);
            assert (watchList.getTitle() == null);
            assert (watchList.getType() == null);
            assert (watchList.getGenre() == null);
            assert (watchList.getRating() == null);
            assert (watchList.getReleaseYear() == null);
            assert (watchList.getIsWatched() == null);
            assert (watchList.getNotes() == null);
            assert (watchList.getCover() == null);
        }

        // 4. WatchList dengan Setter dan Lifecycle methods
        {
            WatchList watchList = new WatchList();
            UUID generatedId = UUID.randomUUID();
            
            // Set properties
            watchList.setId(generatedId);
            watchList.setUserId(userId);
            watchList.setTitle("Interstellar");
            watchList.setType("Movie");
            watchList.setGenre("Sci-Fi");
            watchList.setRating(10);
            watchList.setReleaseYear(2014);
            watchList.setIsWatched(false);
            watchList.setNotes("Belum nonton, tapi ratingnya bagus");
            watchList.setCover("/images/interstellar.jpg");
            
            // Trigger lifecycle methods (accessible because test is in same package)
            watchList.onCreate();
            watchList.onUpdate();

            // Assertions
            assert (watchList.getId().equals(generatedId));
            assert (watchList.getUserId().equals(userId));
            assert (watchList.getTitle().equals("Interstellar"));
            assert (watchList.getType().equals("Movie"));
            assert (watchList.getGenre().equals("Sci-Fi"));
            assert (watchList.getRating().equals(10));
            assert (watchList.getReleaseYear().equals(2014));
            assert (watchList.getIsWatched().equals(false));
            assert (watchList.getNotes().equals("Belum nonton, tapi ratingnya bagus"));
            assert (watchList.getCover().equals("/images/interstellar.jpg"));
            
            // Assert dates are generated
            assert (watchList.getCreatedAt() != null);
            assert (watchList.getUpdatedAt() != null);
        }
    }
}