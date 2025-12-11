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

    // --- PERBAIKAN UTAMA: TEST FULL CONSTRUCTOR ---
    @Test
    @DisplayName("Constructor dengan parameter lengkap bekerja dengan benar")
    void testFullConstructor() {
        UUID id = UUID.randomUUID();
        String title = "Inception";
        String type = "Movie";
        String genre = "Sci-Fi";
        Integer rating = 9;
        Integer releaseYear = 2010;
        String status = "Watched";
        String notes = "Mind blowing";

        // Memanggil Constructor Lengkap (Baris ini yang membuat Jacoco menjadi Hijau)
        WatchListForm form = new WatchListForm(id, title, type, genre, rating, releaseYear, status, notes);

        assertNotNull(form);
        assertEquals(id, form.getId());
        assertEquals(title, form.getTitle());
        assertEquals(type, form.getType());
        assertEquals(genre, form.getGenre());
        assertEquals(rating, form.getRating());
        assertEquals(releaseYear, form.getReleaseYear());
        assertEquals(status, form.getStatus());
        assertEquals(notes, form.getNotes());
    }
    // ----------------------------------------------

    @Test
    @DisplayName("Default constructor membuat objek dengan nilai default")
    void defaultConstructor_CreatesObjectWithDefaultValues() {
        assertNull(watchListForm.getId());
        assertNull(watchListForm.getTitle());
        assertNull(watchListForm.getType());
        assertNull(watchListForm.getGenre());
        assertNull(watchListForm.getRating());
        assertNull(watchListForm.getReleaseYear());
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

    @Test
    @DisplayName("Setter dan Getter untuk Title bekerja dengan benar")
    void setterAndGetter_Title_WorksCorrectly() {
        String title = "Testing Title";
        watchListForm.setTitle(title);
        assertEquals(title, watchListForm.getTitle());
    }

    @Test
    @DisplayName("Setter dan Getter untuk Type bekerja dengan benar")
    void setterAndGetter_Type_WorksCorrectly() {
        String type = "Series";
        watchListForm.setType(type);
        assertEquals(type, watchListForm.getType());
    }

    @Test
    @DisplayName("Setter dan Getter untuk Genre bekerja dengan benar")
    void setterAndGetter_Genre_WorksCorrectly() {
        String genre = "Horror";
        watchListForm.setGenre(genre);
        assertEquals(genre, watchListForm.getGenre());
    }

    @Test
    @DisplayName("Setter dan Getter untuk Rating bekerja dengan benar")
    void setterAndGetter_Rating_WorksCorrectly() {
        Integer rating = 8;
        watchListForm.setRating(rating);
        assertEquals(rating, watchListForm.getRating());
    }

    @Test
    @DisplayName("Setter dan Getter untuk ReleaseYear bekerja dengan benar")
    void setterAndGetter_ReleaseYear_WorksCorrectly() {
        Integer year = 2022;
        watchListForm.setReleaseYear(year);
        assertEquals(year, watchListForm.getReleaseYear());
    }

    @Test
    @DisplayName("Setter dan Getter untuk Notes bekerja dengan benar")
    void setterAndGetter_Notes_WorksCorrectly() {
        String notes = "Some notes";
        watchListForm.setNotes(notes);
        assertEquals(notes, watchListForm.getNotes());
    }

    @Test
    @DisplayName("Setter dan Getter untuk Status bekerja dengan benar (String)")
    void setterAndGetter_Status_WorksCorrectly() {
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
        assertEquals(status, watchListForm.getStatus());
        assertEquals(notes, watchListForm.getNotes());
    }
}