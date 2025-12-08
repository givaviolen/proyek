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
        assertNull(watchListForm.getType());         // Field Baru
        assertNull(watchListForm.getGenre());
        assertNull(watchListForm.getRating());
        assertNull(watchListForm.getReleaseYear());  // Field Baru
        assertNull(watchListForm.getIsWatched());    // Pengganti getStatus
        assertNull(watchListForm.getNotes());        // Pengganti getComment
        assertNull(watchListForm.getConfirmTitle());
    }

    @Test
    @DisplayName("Setter dan Getter untuk id bekerja dengan benar")
    void setterAndGetter_Id_WorksCorrectly() {
        UUID id = UUID.randomUUID();
        watchListForm.setId(id);
        assertEquals(id, watchListForm.getId());
    }

    @Test
    @DisplayName("Setter dan Getter untuk title bekerja dengan benar")
    void setterAndGetter_Title_WorksCorrectly() {
        String title = "Inception";
        watchListForm.setTitle(title);
        assertEquals(title, watchListForm.getTitle());
    }

    @Test
    @DisplayName("Setter dan Getter untuk type bekerja dengan benar")
    void setterAndGetter_Type_WorksCorrectly() {
        String type = "Movie";
        watchListForm.setType(type);
        assertEquals(type, watchListForm.getType());
    }

    @Test
    @DisplayName("Setter dan Getter untuk isWatched bekerja dengan benar (Boolean)")
    void setterAndGetter_IsWatched_WorksCorrectly() {
        // Test True
        watchListForm.setIsWatched(true);
        assertTrue(watchListForm.getIsWatched());

        // Test False
        watchListForm.setIsWatched(false);
        assertFalse(watchListForm.getIsWatched());
    }

    @Test
    @DisplayName("Setter dan Getter untuk genre bekerja dengan benar")
    void setterAndGetter_Genre_WorksCorrectly() {
        String genre = "Sci-Fi";
        watchListForm.setGenre(genre);
        assertEquals(genre, watchListForm.getGenre());
    }

    @Test
    @DisplayName("Setter dan Getter untuk rating bekerja dengan benar")
    void setterAndGetter_Rating_WorksCorrectly() {
        Integer rating = 9;
        watchListForm.setRating(rating);
        assertEquals(rating, watchListForm.getRating());
    }

    @Test
    @DisplayName("Setter dan Getter untuk releaseYear bekerja dengan benar")
    void setterAndGetter_ReleaseYear_WorksCorrectly() {
        Integer year = 2024;
        watchListForm.setReleaseYear(year);
        assertEquals(year, watchListForm.getReleaseYear());
    }

    @Test
    @DisplayName("Setter dan Getter untuk notes bekerja dengan benar")
    void setterAndGetter_Notes_WorksCorrectly() {
        String notes = "Film yang sangat membingungkan tapi seru";
        watchListForm.setNotes(notes);
        assertEquals(notes, watchListForm.getNotes());
    }

    @Test
    @DisplayName("Setter dan Getter untuk confirmTitle bekerja dengan benar")
    void setterAndGetter_ConfirmTitle_WorksCorrectly() {
        String confirmTitle = "Inception";
        watchListForm.setConfirmTitle(confirmTitle);
        assertEquals(confirmTitle, watchListForm.getConfirmTitle());
    }

    @Test
    @DisplayName("Rating dapat diset dengan nilai 0 (Sangat Buruk)")
    void rating_CanBeSet_WithZeroValue() {
        watchListForm.setRating(0);
        assertEquals(0, watchListForm.getRating());
    }

    @Test
    @DisplayName("Rating dapat diset dengan nilai 10 (Sempurna)")
    void rating_CanBeSet_WithMaxValue() {
        watchListForm.setRating(10);
        assertEquals(10, watchListForm.getRating());
    }

    @Test
    @DisplayName("Semua field dapat diset dan diget dengan nilai berbagai tipe")
    void allFields_CanBeSetAndGet_WithVariousValues() {
        // Arrange
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        String title = "The Dark Knight";
        String type = "Movie";
        String genre = "Action";
        Integer rating = 10;
        Integer releaseYear = 2008;
        Boolean isWatched = true;
        String notes = "Best movie ever";
        String confirmTitle = "The Dark Knight";

        // Act
        watchListForm.setId(id);
        watchListForm.setTitle(title);
        watchListForm.setType(type);
        watchListForm.setGenre(genre);
        watchListForm.setRating(rating);
        watchListForm.setReleaseYear(releaseYear);
        watchListForm.setIsWatched(isWatched);
        watchListForm.setNotes(notes);
        watchListForm.setConfirmTitle(confirmTitle);

        // Assert
        assertEquals(id, watchListForm.getId());
        assertEquals(title, watchListForm.getTitle());
        assertEquals(type, watchListForm.getType());
        assertEquals(genre, watchListForm.getGenre());
        assertEquals(rating, watchListForm.getRating());
        assertEquals(releaseYear, watchListForm.getReleaseYear());
        assertEquals(isWatched, watchListForm.getIsWatched());
        assertEquals(notes, watchListForm.getNotes());
        assertEquals(confirmTitle, watchListForm.getConfirmTitle());
    }

    @Test
    @DisplayName("Status isWatched dapat diubah dari False ke True")
    void isWatched_CanBeChanged_FromFalseToTrue() {
        // Arrange
        watchListForm.setIsWatched(false);
        assertFalse(watchListForm.getIsWatched());

        // Act
        watchListForm.setIsWatched(true);

        // Assert
        assertTrue(watchListForm.getIsWatched());
    }

    @Test
    @DisplayName("ConfirmTitle untuk delete operation")
    void confirmTitle_ForDeleteOperation() {
        // Arrange
        String title = "Titanic";
        watchListForm.setTitle(title);

        // Act
        watchListForm.setConfirmTitle(title);

        // Assert
        assertEquals(title, watchListForm.getTitle());
        assertEquals(title, watchListForm.getConfirmTitle());
        assertEquals(watchListForm.getTitle(), watchListForm.getConfirmTitle());
    }

    @Test
    @DisplayName("ConfirmTitle berbeda dengan title")
    void confirmTitle_DifferentFromTitle() {
        // Arrange
        watchListForm.setTitle("Titanic");
        watchListForm.setConfirmTitle("Avatar");

        // Assert
        assertNotEquals(watchListForm.getTitle(), watchListForm.getConfirmTitle());
    }
}