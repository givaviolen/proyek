package org.delcom.app.dto;

import java.util.UUID;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class WatchListForm {

    private UUID id;

    @NotBlank(message = "Judul tidak boleh kosong")
    private String title;

    @NotBlank(message = "Tipe tidak boleh kosong")
    private String type; // Movie / Series

    @NotBlank(message = "Genre tidak boleh kosong")
    private String genre;

    @NotNull(message = "Rating tidak boleh kosong")
    @Min(value = 1, message = "Rating minimal 1")
    @Max(value = 10, message = "Rating maksimal 10")
    private Integer rating;

    @NotNull(message = "Tahun rilis tidak boleh kosong")
    @Min(value = 1900, message = "Tahun rilis tidak valid")
    private Integer releaseYear;

    // --- PERUBAHAN DI SINI ---
    // Hapus Boolean isWatched, ganti dengan String status
    private String status; 
    // -------------------------

    private String notes;

    // Default Constructor
    public WatchListForm() {
    }

    // Constructor lengkap (Opsional)
    public WatchListForm(UUID id, String title, String type, String genre, Integer rating, Integer releaseYear, String status, String notes) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.genre = genre;
        this.rating = rating;
        this.releaseYear = releaseYear;
        this.status = status;
        this.notes = notes;
    }

    // Getter & Setter

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    // --- GETTER SETTER BARU UNTUK STATUS ---
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    // ---------------------------------------

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}