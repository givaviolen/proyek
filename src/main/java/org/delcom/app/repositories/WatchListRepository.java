package org.delcom.app.repositories;

import org.delcom.app.entities.WatchList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WatchListRepository extends JpaRepository<WatchList, UUID> {

    // Mencari semua watchlist berdasarkan user ID
    List<WatchList> findByUserId(UUID userId);

    // Mencari watchlist berdasarkan ID dan user ID
    Optional<WatchList> findByIdAndUserId(UUID id, UUID userId);

    // Mencari watchlist berdasarkan user ID dengan pencarian
    @Query("SELECT w FROM WatchList w WHERE w.userId = :userId AND " +
           "(LOWER(w.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(w.genre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(w.notes) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<WatchList> findByUserIdWithSearch(@Param("userId") UUID userId, @Param("search") String search);

    // Mencari watchlist berdasarkan type (Movie/Series)
    @Query("SELECT w FROM WatchList w WHERE w.userId = :userId AND w.type = :type")
    List<WatchList> findByUserIdAndType(@Param("userId") UUID userId, @Param("type") String type);

    // Mencari watchlist yang sudah ditonton
    @Query("SELECT w FROM WatchList w WHERE w.userId = :userId AND w.isWatched = true")
    List<WatchList> findWatchedByUserId(@Param("userId") UUID userId);

    // Mencari watchlist yang belum ditonton
    @Query("SELECT w FROM WatchList w WHERE w.userId = :userId AND w.isWatched = false")
    List<WatchList> findUnwatchedByUserId(@Param("userId") UUID userId);

    // Mendapatkan semua genre unik berdasarkan user ID
    @Query("SELECT DISTINCT w.genre FROM WatchList w WHERE w.userId = :userId ORDER BY w.genre")
    List<String> findDistinctGenresByUserId(@Param("userId") UUID userId);

    // Menghitung jumlah watchlist berdasarkan genre
    @Query("SELECT w.genre, COUNT(w) FROM WatchList w WHERE w.userId = :userId GROUP BY w.genre")
    List<Object[]> countByGenre(@Param("userId") UUID userId);

    // Menghitung jumlah yang sudah dan belum ditonton
    @Query("SELECT w.isWatched, COUNT(w) FROM WatchList w WHERE w.userId = :userId GROUP BY w.isWatched")
    List<Object[]> countByWatchedStatus(@Param("userId") UUID userId);

    // Menghitung jumlah berdasarkan type (Movie/Series)
    @Query("SELECT w.type, COUNT(w) FROM WatchList w WHERE w.userId = :userId GROUP BY w.type")
    List<Object[]> countByType(@Param("userId") UUID userId);

    // Menghapus watchlist berdasarkan ID dan user ID
    void deleteByIdAndUserId(UUID id, UUID userId);
}