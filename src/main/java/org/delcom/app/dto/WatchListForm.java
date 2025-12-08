package org.delcom.app.dto;

import java.util.UUID;

public class WatchListForm {
    private UUID id;
    private String title;
    private String type;
    private String genre;
    private Integer rating;
    private Integer releaseYear;
    private Boolean isWatched;
    private String notes;
    private String confirmTitle; // Field ini penting untuk Test "delete operation"

    public WatchListForm() {}

    // Getter Setter ID
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    // Getter Setter Title
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    // Getter Setter Type
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    // Getter Setter Genre
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    // Getter Setter Rating
    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    // Getter Setter Release Year
    public Integer getReleaseYear() { return releaseYear; }
    public void setReleaseYear(Integer releaseYear) { this.releaseYear = releaseYear; }

    // Getter Setter Is Watched
    public Boolean getIsWatched() { return isWatched; }
    public void setIsWatched(Boolean isWatched) { this.isWatched = isWatched; }

    // Getter Setter Notes
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    // Getter Setter ConfirmTitle
    public String getConfirmTitle() { return confirmTitle; }
    public void setConfirmTitle(String confirmTitle) { this.confirmTitle = confirmTitle; }
}