package org.delcom.app.dto;

import org.springframework.web.multipart.MultipartFile;
import java.util.UUID;

public class CoverWatchListForm {
    
    private UUID id;
    private MultipartFile coverFile;

    // Constructor
    public CoverWatchListForm() {
    }

    public CoverWatchListForm(UUID id, MultipartFile coverFile) {
        this.id = id;
        this.coverFile = coverFile;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public MultipartFile getCoverFile() {
        return coverFile;
    }

    public void setCoverFile(MultipartFile coverFile) {
        this.coverFile = coverFile;
    }

    // Helper methods untuk validasi
    public boolean isEmpty() {
        return coverFile == null || coverFile.isEmpty();
    }

    public boolean isValidImage() {
        if (isEmpty()) {
            return false;
        }
        
        String contentType = coverFile.getContentType();
        if (contentType == null) {
            return false;
        }
        
        return contentType.equals("image/jpeg") ||
               contentType.equals("image/jpg") ||
               contentType.equals("image/png") ||
               contentType.equals("image/gif") ||
               contentType.equals("image/webp");
    }

    public boolean isSizeValid(long maxSizeInBytes) {
        if (isEmpty()) {
            return false;
        }
        return coverFile.getSize() <= maxSizeInBytes;
    }

    public String getOriginalFilename() {
        return isEmpty() ? null : coverFile.getOriginalFilename();
    }
}