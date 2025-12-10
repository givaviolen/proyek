package org.delcom.app.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WatchListFormTests {

    private WatchListForm watchListForm;

    @BeforeEach
    void setUp() {
        watchListForm = new WatchListForm();
    }

    @Test
    @DisplayName("Default constructor membuat objek dengan nilai default")
    void defaultConstructor_CreatesObjectWithDefaultValues() {
        assertNull(watchListForm.getId());
        assertNull(watchListForm.getTitle());
        assertNull(watchListForm.getType());
        assertNull(watchListForm.getGenre());
        assertNull(watchListForm.getRating());
        assertNull(watchListForm.getReleaseYear());
        // [FIX] Menggunakan getStatus
        assertNull(watchListForm.getStatus());
        assertNull(watchListForm.getNotes());
    }

    @Test
    @DisplayName("Setter dan Getter untuk id bekerja dengan benar")
    void setterAndGetter_Id_WorksCorrectly() {
        UUID id = UUID.randomUUID();
        watchListForm.setId(id);
        assertEquals(id, watchListForm.getId());
    }

    // ... (Test Title, Type, Genre, Rating, Year, Notes sama seperti sebelumnya) ...

    @Test
    @DisplayName("Setter dan Getter untuk Status bekerja dengan benar (String)")
    void setterAndGetter_Status_WorksCorrectly() {
        // [FIX] Test menggunakan String
        String status = "Watching";
        watchListForm.setStatus(status);
        assertEquals(status, watchListForm.getStatus());

        watchListForm.setStatus("Plan to Watch");
        assertEquals("Plan to Watch", watchListForm.getStatus());
    }

    @Test
    @DisplayName("Semua field dapat diset dan diget dengan nilai berbagai tipe")
    void allFields_CanBeSetAndGet_WithVariousValues() {
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        String title = "The Dark Knight";
        String type = "Movie";
        String genre = "Action";
        Integer rating = 10;
        Integer releaseYear = 2008;
        // [FIX] String status
        String status = "Watched";
        String notes = "Best movie ever";

        watchListForm.setId(id);
        watchListForm.setTitle(title);
        watchListForm.setType(type);
        watchListForm.setGenre(genre);
        watchListForm.setRating(rating);
        watchListForm.setReleaseYear(releaseYear);
        watchListForm.setStatus(status);
        watchListForm.setNotes(notes);

        assertEquals(id, watchListForm.getId());
        assertEquals(title, watchListForm.getTitle());
        assertEquals(type, watchListForm.getType());
        assertEquals(genre, watchListForm.getGenre());
        assertEquals(rating, watchListForm.getRating());
        assertEquals(releaseYear, watchListForm.getReleaseYear());
        // [FIX]
        assertEquals(status, watchListForm.getStatus());
        assertEquals(notes, watchListForm.getNotes());
    }
}