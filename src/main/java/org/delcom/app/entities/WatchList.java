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
    private String title;

    @Column(name = "type", nullable = false)
    private String type; // Movie / Series

    @Column(name = "genre", nullable = false)
    private String genre;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "release_year", nullable = false)
    private Integer releaseYear;

    // --- PERUBAHAN UTAMA ---
    @Column(name = "status", nullable = false)
    private String status; // Values: "Watched", "Watching", "Plan to Watch"

    @Column(name = "notes", nullable = true, columnDefinition = "TEXT")
    private String notes;

    @Column(name = "cover", nullable = true)
    private String cover;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public WatchList() {
    }

    public WatchList(UUID userId, String title, String type, String genre, Integer rating, 
                     Integer releaseYear, String status, String notes) {
        this.userId = userId;
        this.title = title;
        this.type = type;
        this.genre = genre;
        this.rating = rating;
        this.releaseYear = releaseYear;
        this.status = status;
        this.notes = notes;
    }

    // Getter & Setter
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }

    // --- GETTER SETTER BARU UNTUK STATUS ---
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    @PrePersist
    protected void onCreate() {
    LocalDateTime now = LocalDateTime.now(); // Ambil waktu satu kali saja
    this.createdAt = now;
    this.updatedAt = now; // Gunakan variabel yang sama
}
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}