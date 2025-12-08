package org.delcom.app.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "watchlists")
public class WatchList {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "title", nullable = false)
    private String title; // Judul film/series

    @Column(name = "type", nullable = false)
    private String type; // "Movie" atau "Series"

    @Column(name = "genre", nullable = false)
    private String genre; // Genre (Action, Drama, Comedy, Horror, dll)

    @Column(name = "rating", nullable = false)
    private Integer rating; // Rating 1-10

    @Column(name = "release_year", nullable = false)
    private Integer releaseYear; // Tahun rilis

    @Column(name = "is_watched", nullable = false)
    private Boolean isWatched; // Sudah ditonton atau belum

    @Column(name = "notes", nullable = true, columnDefinition = "TEXT")
    private String notes; // Catatan pribadi

    @Column(name = "cover", nullable = true)
    private String cover; // Nama file gambar poster

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Constructor
    public WatchList() {
    }

    public WatchList(UUID userId, String title, String type, String genre, Integer rating, 
                     Integer releaseYear, Boolean isWatched, String notes) {
        this.userId = userId;
        this.title = title;
        this.type = type;
        this.genre = genre;
        this.rating = rating;
        this.releaseYear = releaseYear;
        this.isWatched = isWatched;
        this.notes = notes;
    }

    // Getter & Setter
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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

    public Boolean getIsWatched() {
        return isWatched;
    }

    public void setIsWatched(Boolean isWatched) {
        this.isWatched = isWatched;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // ======= @PrePersist & @PreUpdate =======
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

}