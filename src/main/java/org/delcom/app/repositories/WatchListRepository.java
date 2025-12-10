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

    List<WatchList> findByUserId(UUID userId);

    Optional<WatchList> findByIdAndUserId(UUID id, UUID userId);

    @Query("SELECT w FROM WatchList w WHERE w.userId = :userId AND " +
           "(LOWER(w.title) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(w.genre) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(w.notes) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<WatchList> findByUserIdWithSearch(@Param("userId") UUID userId, @Param("search") String search);

    @Query("SELECT w FROM WatchList w WHERE w.userId = :userId AND w.type = :type")
    List<WatchList> findByUserIdAndType(@Param("userId") UUID userId, @Param("type") String type);

    // --- PERUBAHAN: Mencari berdasarkan status spesifik ---
    @Query("SELECT w FROM WatchList w WHERE w.userId = :userId AND w.status = :status")
    List<WatchList> findByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") String status);

    @Query("SELECT DISTINCT w.genre FROM WatchList w WHERE w.userId = :userId ORDER BY w.genre")
    List<String> findDistinctGenresByUserId(@Param("userId") UUID userId);

    @Query("SELECT w.genre, COUNT(w) FROM WatchList w WHERE w.userId = :userId GROUP BY w.genre")
    List<Object[]> countByGenre(@Param("userId") UUID userId);

    // --- PERUBAHAN: Grouping berdasarkan status string ---
    @Query("SELECT w.status, COUNT(w) FROM WatchList w WHERE w.userId = :userId GROUP BY w.status")
    List<Object[]> countByStatusGroup(@Param("userId") UUID userId);

    @Query("SELECT w.type, COUNT(w) FROM WatchList w WHERE w.userId = :userId GROUP BY w.type")
    List<Object[]> countByType(@Param("userId") UUID userId);

    void deleteByIdAndUserId(UUID id, UUID userId);
}