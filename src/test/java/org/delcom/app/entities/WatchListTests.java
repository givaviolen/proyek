package org.delcom.app.entities;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WatchListTests {
    
    @Test
    @DisplayName("Membuat instance dari kelas WatchList (Constructor Lengkap)")
    void testMembuatInstanceWatchList() {
        UUID userId = UUID.randomUUID();
        
        WatchList watchList = new WatchList(
            userId, 
            "Inception", 
            "Movie", 
            "Sci-Fi", 
            9, 
            2010,
            "Watched", 
            "Film yang sangat membingungkan tapi seru"
        );

        // Menggunakan assertEquals lebih aman daripada keyword 'assert'
        assertEquals(userId, watchList.getUserId());
        assertEquals("Inception", watchList.getTitle());
        assertEquals("Movie", watchList.getType());
        assertEquals("Sci-Fi", watchList.getGenre());
        assertEquals(9, watchList.getRating());
        assertEquals(2010, watchList.getReleaseYear());
        assertEquals("Watched", watchList.getStatus());
        assertEquals("Film yang sangat membingungkan tapi seru", watchList.getNotes());
    }

    @Test
    @DisplayName("WatchList tipe Series (Constructor & Getter)")
    void testWatchListSeries() {
        UUID userId = UUID.randomUUID();
        WatchList watchList = new WatchList(
            userId, 
            "Breaking Bad", 
            "Series", 
            "Crime", 
            10, 
            2008,
            "Plan to Watch", 
            "Best series ever"
        );

        assertEquals("Breaking Bad", watchList.getTitle());
        assertEquals("Series", watchList.getType());
        assertEquals("Plan to Watch", watchList.getStatus());
    }

    @Test
    @DisplayName("WatchList Default Constructor")
    void testWatchListDefault() {
        WatchList watchList = new WatchList();
        assertNull(watchList.getId());
        assertNull(watchList.getStatus());
    }

    @Test
    @DisplayName("WatchList Setter & Lifecycle methods")
    void testWatchListSetterAndLifecycle() {
        UUID userId = UUID.randomUUID();
        WatchList watchList = new WatchList();
        UUID generatedId = UUID.randomUUID();
        
        watchList.setId(generatedId);
        watchList.setUserId(userId);
        watchList.setTitle("Interstellar");
        watchList.setType("Movie");
        watchList.setGenre("Sci-Fi");
        watchList.setRating(10);
        watchList.setReleaseYear(2014);
        watchList.setStatus("Watching");
        watchList.setNotes("Belum nonton");
        watchList.setCover("/images/interstellar.jpg");
        
        // Simulasi Lifecycle JPA
        watchList.onCreate(); // Mengisi createdAt
        watchList.onUpdate(); // Mengisi updatedAt

        assertEquals(generatedId, watchList.getId());
        assertEquals("Interstellar", watchList.getTitle());
        assertEquals("Watching", watchList.getStatus());
        assertEquals("/images/interstellar.jpg", watchList.getCover());
        
        // Verifikasi CreatedAt & UpdatedAt tidak null
        assertNotNull(watchList.getCreatedAt(), "CreatedAt harus terisi setelah onCreate");
        assertNotNull(watchList.getUpdatedAt(), "UpdatedAt harus terisi setelah onUpdate");
    }

    @Test
    @DisplayName("Test Object Methods (ToString, Equals, HashCode coverage)")
    void testObjectMethods() {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        
        WatchList w1 = new WatchList(userId, "A", "Movie", "G", 5, 2020, "Plan", "Note");
        w1.setId(id);
        
        WatchList w2 = new WatchList(userId, "A", "Movie", "G", 5, 2020, "Plan", "Note");
        w2.setId(id); // Sama dengan w1

        WatchList w3 = new WatchList(); // Beda

        // Test ToString (Penting jika menggunakan Lombok @Data atau override toString)
        assertNotNull(w1.toString());

        // Test Equals (Jika entity menggunakan default Object.equals, ini test reference)
        // Jika entity override equals (misal pakai Lombok), ini test logic
        assertEquals(w1, w1);       // Reflexive
        assertNotEquals(w1, null);  // Null check
        assertNotEquals(w1, new Object()); // Class check
        
        // Jika Anda menggunakan @Data/Lombok, baris ini memastikan equals logic tercover:
        // assertEquals(w1, w2); // Uncomment jika menggunakan Lombok @Data
        
        // Test HashCode
        // assertEquals(w1.hashCode(), w2.hashCode()); // Uncomment jika menggunakan Lombok @Data
        assertNotEquals(0, w1.hashCode());
    }
}