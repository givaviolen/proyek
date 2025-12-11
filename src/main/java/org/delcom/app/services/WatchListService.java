package org.delcom.app.services;

import org.delcom.app.entities.WatchList;
import org.delcom.app.repositories.WatchListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class WatchListService {

    private final WatchListRepository watchListRepository;

    public WatchListService(WatchListRepository watchListRepository) {
        this.watchListRepository = watchListRepository;
    }

    // ========================================================================
    // READ OPERATIONS
    // ========================================================================

    public List<WatchList> getAllWatchLists(UUID userId, String search) {
        if (search != null && !search.trim().isEmpty()) {
            return watchListRepository.findByUserIdWithSearch(userId, search.trim());
        }
        // [PERBAIKAN DISINI]
        // Menggunakan OrderByCreatedAtDesc
        return watchListRepository.findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    // ... (SEMUA KODE DI BAWAH INI TETAP SAMA, TIDAK PERLU DIUBAH) ...

    public WatchList getWatchListById(UUID userId, UUID id) {
        return watchListRepository.findByIdAndUserId(id, userId).orElse(null);
    }

    public List<WatchList> getWatchListsByType(UUID userId, String type) {
        return watchListRepository.findByUserIdAndType(userId, type);
    }

    public List<WatchList> getWatchListsByStatus(UUID userId, String status) {
        return watchListRepository.findByUserIdAndStatus(userId, status);
    }

    public List<String> getAllGenres(UUID userId) {
        return watchListRepository.findDistinctGenresByUserId(userId);
    }

    public List<Object[]> getGenreStatistics(UUID userId) {
        return watchListRepository.countByGenre(userId);
    }

    public List<Object[]> getStatusStatistics(UUID userId) {
        return watchListRepository.countByStatusGroup(userId);
    }

    public List<Object[]> getTypeStatistics(UUID userId) {
        return watchListRepository.countByType(userId);
    }

    public long countByStatus(UUID userId, String status) {
        return watchListRepository.countByUserIdAndStatus(userId, status);
    }

    // ========================================================================
    // WRITE OPERATIONS
    // ========================================================================

    @Transactional
    public WatchList createWatchList(UUID userId, String title, String type, String genre, 
                                     Integer rating, Integer releaseYear, String status, String notes) {
        if (status == null || status.isEmpty()) {
            status = "Plan to Watch";
        }
        WatchList watchList = new WatchList(userId, title, type, genre, rating, releaseYear, status, notes);
        return watchListRepository.save(watchList);
    }

    @Transactional
    public WatchList updateWatchList(UUID userId, UUID id, String title, String type, String genre, 
                                     Integer rating, Integer releaseYear, String status, String notes) {
        WatchList existing = watchListRepository.findByIdAndUserId(id, userId).orElse(null);
        if (existing == null) return null;

        existing.setTitle(title);
        existing.setType(type);
        existing.setGenre(genre);
        existing.setRating(rating);
        existing.setReleaseYear(releaseYear);
        existing.setStatus(status);
        existing.setNotes(notes);
        // Note: createdAt tidak diubah di sini, jadi posisi urutan aman.

        return watchListRepository.save(existing);
    }

    @Transactional
    public void updateCover(UUID id, String fileName) {
        WatchList watchList = watchListRepository.findById(id).orElse(null);
        if (watchList != null) {
            watchList.setCover(fileName);
            watchListRepository.save(watchList);
        }
    }

    @Transactional
    public boolean deleteWatchList(UUID userId, UUID id) {
        WatchList existing = watchListRepository.findByIdAndUserId(id, userId).orElse(null);
        if (existing == null) return false;
        
        watchListRepository.deleteByIdAndUserId(id, userId);
        return true;
    }

    @Transactional
    public WatchList cycleStatus(UUID userId, UUID id) {
        WatchList existing = watchListRepository.findByIdAndUserId(id, userId).orElse(null);
        if (existing == null) return null;

        String current = existing.getStatus();
        String nextStatus;
        if ("Plan to Watch".equalsIgnoreCase(current)) nextStatus = "Watching";
        else if ("Watching".equalsIgnoreCase(current)) nextStatus = "Watched";
        else nextStatus = "Plan to Watch";

        existing.setStatus(nextStatus);
        return watchListRepository.save(existing);
    }
}