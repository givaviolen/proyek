package org.delcom.app.services;

import org.delcom.app.entities.WatchList;
import org.delcom.app.repositories.WatchListRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class WatchListService {

    private final WatchListRepository watchListRepository;

    public WatchListService(WatchListRepository watchListRepository) {
        this.watchListRepository = watchListRepository;
    }

    // Membuat watchlist baru
    public WatchList createWatchList(UUID userId, String title, String type, String genre, 
                                     Integer rating, Integer releaseYear, Boolean isWatched, String notes) {
        WatchList watchList = new WatchList(userId, title, type, genre, rating, releaseYear, isWatched, notes);
        return watchListRepository.save(watchList);
    }

    // Mendapatkan semua watchlist berdasarkan user ID dengan opsi pencarian
    public List<WatchList> getAllWatchLists(UUID userId, String search) {
        if (search != null && !search.isEmpty()) {
            return watchListRepository.findByUserIdWithSearch(userId, search);
        }
        return watchListRepository.findByUserId(userId);
    }

    // Mendapatkan watchlist berdasarkan ID
    public WatchList getWatchListById(UUID userId, UUID id) {
        return watchListRepository.findByIdAndUserId(id, userId).orElse(null);
    }

    // Mendapatkan watchlist berdasarkan type (Movie/Series)
    public List<WatchList> getWatchListsByType(UUID userId, String type) {
        return watchListRepository.findByUserIdAndType(userId, type);
    }

    // Mendapatkan watchlist yang sudah ditonton
    public List<WatchList> getWatchedWatchLists(UUID userId) {
        return watchListRepository.findWatchedByUserId(userId);
    }

    // Mendapatkan watchlist yang belum ditonton
    public List<WatchList> getUnwatchedWatchLists(UUID userId) {
        return watchListRepository.findUnwatchedByUserId(userId);
    }

    // Mendapatkan semua genre unik
    public List<String> getAllGenres(UUID userId) {
        return watchListRepository.findDistinctGenresByUserId(userId);
    }

    // Mendapatkan statistik berdasarkan genre untuk chart
    public List<Object[]> getGenreStatistics(UUID userId) {
        return watchListRepository.countByGenre(userId);
    }

    // Mendapatkan statistik watched vs unwatched untuk chart
    public List<Object[]> getWatchedStatistics(UUID userId) {
        return watchListRepository.countByWatchedStatus(userId);
    }

    // Mendapatkan statistik Movie vs Series untuk chart
    public List<Object[]> getTypeStatistics(UUID userId) {
        return watchListRepository.countByType(userId);
    }

    // Memperbarui watchlist
    public WatchList updateWatchList(UUID userId, UUID id, String title, String type, String genre, 
                                     Integer rating, Integer releaseYear, Boolean isWatched, String notes) {
        WatchList existingWatchList = watchListRepository.findByIdAndUserId(id, userId).orElse(null);
        if (existingWatchList == null) {
            return null;
        }

        existingWatchList.setTitle(title);
        existingWatchList.setType(type);
        existingWatchList.setGenre(genre);
        existingWatchList.setRating(rating);
        existingWatchList.setReleaseYear(releaseYear);
        existingWatchList.setIsWatched(isWatched);
        existingWatchList.setNotes(notes);

        return watchListRepository.save(existingWatchList);
    }

    // Method untuk update cover/poster
    public void updateCover(UUID id, String fileName) {
        WatchList watchList = watchListRepository.findById(id).orElse(null);
        if (watchList != null) {
            watchList.setCover(fileName);
            watchListRepository.save(watchList);
        }
    }

    // Toggle status watched/unwatched
    public WatchList toggleWatchedStatus(UUID userId, UUID id) {
        WatchList existingWatchList = watchListRepository.findByIdAndUserId(id, userId).orElse(null);
        if (existingWatchList == null) {
            return null;
        }

        existingWatchList.setIsWatched(!existingWatchList.getIsWatched());
        return watchListRepository.save(existingWatchList);
    }

    // Menghapus watchlist
    @Transactional
    public boolean deleteWatchList(UUID userId, UUID id) {
        WatchList existingWatchList = watchListRepository.findByIdAndUserId(id, userId).orElse(null);
        if (existingWatchList == null) {
            return false;
        }

        watchListRepository.deleteByIdAndUserId(id, userId);
        return true;
    }
}