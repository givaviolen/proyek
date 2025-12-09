package org.delcom.app.dto;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CoverWatchListFormTests {

    private CoverWatchListForm coverWatchListForm;
    private MultipartFile mockMultipartFile;

    @BeforeEach
    void setup() {
        coverWatchListForm = new CoverWatchListForm();
        mockMultipartFile = mock(MultipartFile.class);
    }

    // ----------------------------------------------------------------
    // PERBAIKAN UTAMA: Menambahkan Test untuk Constructor Parameter
    // ----------------------------------------------------------------
    @Test
    @DisplayName("Constructor dengan parameter mengatur id dan coverFile dengan benar")
    void constructor_dengan_parameter_bekerja_dengan_benar() {
        // Arrange
        UUID expectedId = UUID.randomUUID();
        
        // Act
        // Ini akan menutupi baris merah pada Jacoco (Constructor parameter)
        CoverWatchListForm form = new CoverWatchListForm(expectedId, mockMultipartFile);

        // Assert
        assertEquals(expectedId, form.getId());
        assertEquals(mockMultipartFile, form.getCoverFile());
    }

    @Test
    @DisplayName("Constructor default membuat objek kosong")
    void constructor_default_membuat_objek_kosong() {
        // Act
        CoverWatchListForm form = new CoverWatchListForm();

        // Assert
        assertNull(form.getId());
        assertNull(form.getCoverFile());
    }

    @Test
    @DisplayName("Setter dan Getter untuk ID bekerja dengan benar")
    void setter_dan_getter_untuk_id_bekerja_dengan_benar() {
        // Arrange
        UUID expectedId = UUID.randomUUID();

        // Act
        coverWatchListForm.setId(expectedId);
        UUID actualId = coverWatchListForm.getId();

        // Assert
        assertEquals(expectedId, actualId);
    }

    @Test
    @DisplayName("Setter dan Getter untuk coverFile bekerja dengan benar")
    void setter_dan_getter_untuk_coverFile_bekerja_dengan_benar() {
        // Act
        coverWatchListForm.setCoverFile(mockMultipartFile);
        MultipartFile actualFile = coverWatchListForm.getCoverFile();

        // Assert
        assertEquals(mockMultipartFile, actualFile);
    }

    @Test
    @DisplayName("isEmpty return true ketika coverFile null")
    void isEmpty_return_true_ketika_coverFile_null() {
        // Arrange
        coverWatchListForm.setCoverFile(null);

        // Act
        boolean result = coverWatchListForm.isEmpty();

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isEmpty return true ketika coverFile empty")
    void isEmpty_return_true_ketika_coverFile_empty() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(true);
        coverWatchListForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverWatchListForm.isEmpty();

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("isEmpty return false ketika coverFile tidak empty")
    void isEmpty_return_false_ketika_coverFile_tidak_empty() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        coverWatchListForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverWatchListForm.isEmpty();

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("getOriginalFilename return null ketika coverFile null")
    void getOriginalFilename_return_null_ketika_coverFile_null() {
        // Arrange
        coverWatchListForm.setCoverFile(null);

        // Act
        String result = coverWatchListForm.getOriginalFilename();

        // Assert
        assertNull(result);
    }

    @Test
    @DisplayName("getOriginalFilename return filename ketika coverFile ada")
    void getOriginalFilename_return_filename_ketika_coverFile_ada() {
        // Arrange
        String expectedFilename = "movie-poster.jpg";
        when(mockMultipartFile.getOriginalFilename()).thenReturn(expectedFilename);
        coverWatchListForm.setCoverFile(mockMultipartFile);

        // Act
        String result = coverWatchListForm.getOriginalFilename();

        // Assert
        assertEquals(expectedFilename, result);
    }

    @Test
    @DisplayName("isValidImage return false ketika coverFile null")
    void isValidImage_return_false_ketika_coverFile_null() {
        // Arrange
        coverWatchListForm.setCoverFile(null);

        // Act
        boolean result = coverWatchListForm.isValidImage();

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isValidImage return false ketika coverFile empty")
    void isValidImage_return_false_ketika_coverFile_empty() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(true);
        coverWatchListForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverWatchListForm.isValidImage();

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isValidImage return false ketika contentType null")
    void isValidImage_return_false_ketika_contentType_null() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn(null);
        coverWatchListForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverWatchListForm.isValidImage();

        // Assert
        assertFalse(result);
    }

    // ----------------------------------------------------------------
    // UPDATE: Menambahkan variasi content type untuk Branch Coverage 100%
    // ----------------------------------------------------------------

    @Test
    @DisplayName("isValidImage return true untuk image/jpeg")
    void isValidImage_return_true_untuk_image_jpeg() {
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/jpeg");
        coverWatchListForm.setCoverFile(mockMultipartFile);
        assertTrue(coverWatchListForm.isValidImage());
    }

    @Test
    @DisplayName("isValidImage return true untuk image/jpg")
    void isValidImage_return_true_untuk_image_jpg() {
        // Ini ditambahkan karena di kode ada check .equals("image/jpg")
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/jpg");
        coverWatchListForm.setCoverFile(mockMultipartFile);
        assertTrue(coverWatchListForm.isValidImage());
    }

    @Test
    @DisplayName("isValidImage return true untuk image/png")
    void isValidImage_return_true_untuk_image_png() {
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/png");
        coverWatchListForm.setCoverFile(mockMultipartFile);
        assertTrue(coverWatchListForm.isValidImage());
    }

    @Test
    @DisplayName("isValidImage return true untuk image/gif")
    void isValidImage_return_true_untuk_image_gif() {
        // Ini ditambahkan karena di kode ada check .equals("image/gif")
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/gif");
        coverWatchListForm.setCoverFile(mockMultipartFile);
        assertTrue(coverWatchListForm.isValidImage());
    }

    @Test
    @DisplayName("isValidImage return true untuk image/webp")
    void isValidImage_return_true_untuk_image_webp() {
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/webp");
        coverWatchListForm.setCoverFile(mockMultipartFile);
        assertTrue(coverWatchListForm.isValidImage());
    }

    @Test
    @DisplayName("isValidImage return false untuk content type non-image")
    void isValidImage_return_false_untuk_content_type_non_image() {
        // Arrange
        String[] invalidContentTypes = {
                "text/plain",
                "application/pdf",
                "application/octet-stream",
                "video/mp4"
        };

        for (String contentType : invalidContentTypes) {
            when(mockMultipartFile.isEmpty()).thenReturn(false);
            when(mockMultipartFile.getContentType()).thenReturn(contentType);
            coverWatchListForm.setCoverFile(mockMultipartFile);

            // Act
            boolean result = coverWatchListForm.isValidImage();

            // Assert
            assertFalse(result, "Should return false for content type: " + contentType);
        }
    }

    @Test
    @DisplayName("isSizeValid return false ketika coverFile null")
    void isSizeValid_return_false_ketika_coverFile_null() {
        // Arrange
        coverWatchListForm.setCoverFile(null);
        long maxSize = 1024 * 1024; // 1MB

        // Act
        boolean result = coverWatchListForm.isSizeValid(maxSize);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("isSizeValid return true ketika file size sama dengan maxSize")
    void isSizeValid_return_true_ketika_file_size_sama_dengan_maxSize() {
        // Arrange
        long maxSize = 2 * 1024 * 1024; // 2MB for poster
        when(mockMultipartFile.getSize()).thenReturn(maxSize);
        coverWatchListForm.setCoverFile(mockMultipartFile);

        // Act
        boolean result = coverWatchListForm.isSizeValid(maxSize);

        // Assert
        assertTrue(result);
    }

    @Test
    @DisplayName("Integration test - form valid untuk poster film JPEG")
    void integration_test_form_valid_untuk_poster_film_JPEG() {
        // Arrange
        UUID id = UUID.randomUUID();
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/jpeg");
        when(mockMultipartFile.getSize()).thenReturn(800 * 1024L); // 800KB Poster
        when(mockMultipartFile.getOriginalFilename()).thenReturn("inception-poster.jpg");

        coverWatchListForm.setId(id);
        coverWatchListForm.setCoverFile(mockMultipartFile);

        // Assert semua kondisi
        assertFalse(coverWatchListForm.isEmpty());
        assertEquals("inception-poster.jpg", coverWatchListForm.getOriginalFilename());
        assertTrue(coverWatchListForm.isValidImage());
        assertTrue(coverWatchListForm.isSizeValid(1024 * 1024)); // 1MB max
        assertEquals(id, coverWatchListForm.getId());
    }

    @Test
    @DisplayName("Integration test - form invalid untuk poster High Quality (Too Big)")
    void integration_test_form_invalid_untuk_poster_high_quality() {
        // Arrange
        when(mockMultipartFile.isEmpty()).thenReturn(false);
        when(mockMultipartFile.getContentType()).thenReturn("image/png");
        when(mockMultipartFile.getSize()).thenReturn(10 * 1024 * 1024L); // 10MB High Res Poster
        when(mockMultipartFile.getOriginalFilename()).thenReturn("4k-poster.png");

        coverWatchListForm.setCoverFile(mockMultipartFile);

        // Assert
        assertFalse(coverWatchListForm.isEmpty());
        assertTrue(coverWatchListForm.isValidImage()); // Masih valid sebagai image
        assertFalse(coverWatchListForm.isSizeValid(2 * 1024 * 1024)); // Size melebihi 2MB limit
    }

    @Test
    @DisplayName("Edge case - contentType case insensitive")
    void edge_case_contentType_case_insensitive() {
        // Arrange
        String[] caseVariations = {
                "IMAGE/JPEG",
                "Image/Png"
        };

        for (String contentType : caseVariations) {
            when(mockMultipartFile.isEmpty()).thenReturn(false);
            when(mockMultipartFile.getContentType()).thenReturn(contentType);
            coverWatchListForm.setCoverFile(mockMultipartFile);

            // Act
            boolean result = coverWatchListForm.isValidImage();

            // Assert
            assertFalse(result, "Should return false for case variation: " + contentType);
        }
    }
}