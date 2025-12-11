package org.delcom.app.entities;

import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WatchListTest {

    @Test
    void testNoArgsConstructorAndSetters() {
        // 1. Test No-Args Constructor
        WatchList watchList = new WatchList();
        assertNotNull(watchList);

        // Prepare Data
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String title = "Interstellar";
        String type = "Movie";
        String genre = "Sci-Fi";
        Integer rating = 10;
        Integer releaseYear = 2014;
        String status = "Watched";
        String notes = "Masterpiece";
        String cover = "cover.jpg";

        // 2. Test Setters
        watchList.setId(id);
        watchList.setUserId(userId);
        watchList.setTitle(title);
        watchList.setType(type);
        watchList.setGenre(genre);
        watchList.setRating(rating);
        watchList.setReleaseYear(releaseYear);
        watchList.setStatus(status);
        watchList.setNotes(notes);
        watchList.setCover(cover);

        // 3. Test Getters (Assert values match)
        assertEquals(id, watchList.getId());
        assertEquals(userId, watchList.getUserId());
        assertEquals(title, watchList.getTitle());
        assertEquals(type, watchList.getType());
        assertEquals(genre, watchList.getGenre());
        assertEquals(rating, watchList.getRating());
        assertEquals(releaseYear, watchList.getReleaseYear());
        assertEquals(status, watchList.getStatus());
        assertEquals(notes, watchList.getNotes());
        assertEquals(cover, watchList.getCover());
    }

    @Test
    void testParameterizedConstructor() {
        // Prepare Data
        UUID userId = UUID.randomUUID();
        String title = "Breaking Bad";
        String type = "Series";
        String genre = "Crime";
        Integer rating = 9;
        Integer releaseYear = 2008;
        String status = "Watching";
        String notes = "Intense";

        // 1. Test Parameterized Constructor
        WatchList watchList = new WatchList(userId, title, type, genre, rating, releaseYear, status, notes);

        // 2. Assert values are set correctly
        assertEquals(userId, watchList.getUserId());
        assertEquals(title, watchList.getTitle());
        assertEquals(type, watchList.getType());
        assertEquals(genre, watchList.getGenre());
        assertEquals(rating, watchList.getRating());
        assertEquals(releaseYear, watchList.getReleaseYear());
        assertEquals(status, watchList.getStatus());
        assertEquals(notes, watchList.getNotes());
        
        // Assert fields not in constructor are null
        assertNull(watchList.getId());
        assertNull(watchList.getCover());
    }

    @Test
    void testLifecycleMethods() throws InterruptedException {
        WatchList watchList = new WatchList();

        // Ensure dates are null initially
        assertNull(watchList.getCreatedAt());
        assertNull(watchList.getUpdatedAt());

        // 1. Test onCreate (@PrePersist)
        // Karena method protected, kita bisa akses jika test ada di package yang sama.
        watchList.onCreate(); 
        
        LocalDateTime createdAt = watchList.getCreatedAt();
        LocalDateTime updatedAt = watchList.getUpdatedAt();

        assertNotNull(createdAt);
        assertNotNull(updatedAt);
        // Pada saat create, updated dan created biasanya sama (atau sangat dekat)
        assertEquals(createdAt, updatedAt); 

        // 2. Test onUpdate (@PreUpdate)
        // Beri jeda sedikit agar waktu berubah (opsional, tapi bagus untuk memastikan perubahan)
        Thread.sleep(10); 
        
        watchList.onUpdate();
        
        LocalDateTime newUpdatedAt = watchList.getUpdatedAt();
        
        // CreatedAt tidak boleh berubah
        assertEquals(createdAt, watchList.getCreatedAt());
        // UpdatedAt harus berubah (waktu maju)
        assertTrue(newUpdatedAt.isAfter(updatedAt));
    }
}