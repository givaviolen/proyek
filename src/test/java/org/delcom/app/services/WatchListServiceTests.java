package org.delcom.app.services;

import org.delcom.app.entities.WatchList;
import org.delcom.app.repositories.WatchListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WatchListServiceTests{

    @Mock
    private WatchListRepository watchListRepository;

    @InjectMocks
    private WatchListService watchListService;

    private UUID userId;
    private UUID watchListId;
    private WatchList watchList;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        watchListId = UUID.randomUUID();
        
        watchList = new WatchList();
        watchList.setId(watchListId);
        watchList.setUserId(userId);
        watchList.setTitle("Test Movie");
        watchList.setStatus("Plan to Watch");
    }

    // ========================================================================
    // READ OPERATIONS TESTS
    // ========================================================================

    @Test
    void testGetAllWatchLists_WithSearch() {
        String search = "Test";
        when(watchListRepository.findByUserIdWithSearch(userId, search)).thenReturn(List.of(watchList));

        List<WatchList> result = watchListService.getAllWatchLists(userId, search);

        assertEquals(1, result.size());
        verify(watchListRepository).findByUserIdWithSearch(userId, search);
        // Pastikan method findAll...TIDAK dipanggil
        verify(watchListRepository, never()).findAllByUserIdOrderByCreatedAtDesc(any());
    }

    @Test
    void testGetAllWatchLists_NoSearch_Null() {
        when(watchListRepository.findAllByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(watchList));

        List<WatchList> result = watchListService.getAllWatchLists(userId, null);

        assertEquals(1, result.size());
        verify(watchListRepository).findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    void testGetAllWatchLists_NoSearch_EmptyString() {
        when(watchListRepository.findAllByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(watchList));

        List<WatchList> result = watchListService.getAllWatchLists(userId, "   "); // Whitespace only

        assertEquals(1, result.size());
        verify(watchListRepository).findAllByUserIdOrderByCreatedAtDesc(userId);
    }

    @Test
    void testGetWatchListById_Found() {
        when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(Optional.of(watchList));

        WatchList result = watchListService.getWatchListById(userId, watchListId);

        assertNotNull(result);
        assertEquals(watchListId, result.getId());
    }

    @Test
    void testGetWatchListById_NotFound() {
        when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(Optional.empty());

        WatchList result = watchListService.getWatchListById(userId, watchListId);

        assertNull(result);
    }

    @Test
    void testGetWatchListsByType() {
        String type = "Movie";
        when(watchListRepository.findByUserIdAndType(userId, type)).thenReturn(List.of(watchList));

        List<WatchList> result = watchListService.getWatchListsByType(userId, type);

        assertFalse(result.isEmpty());
        verify(watchListRepository).findByUserIdAndType(userId, type);
    }

    @Test
    void testGetWatchListsByStatus() {
        String status = "Watched";
        when(watchListRepository.findByUserIdAndStatus(userId, status)).thenReturn(List.of(watchList));

        List<WatchList> result = watchListService.getWatchListsByStatus(userId, status);

        assertFalse(result.isEmpty());
        verify(watchListRepository).findByUserIdAndStatus(userId, status);
    }

    @Test
    void testGetAllGenres() {
        when(watchListRepository.findDistinctGenresByUserId(userId)).thenReturn(List.of("Action", "Drama"));

        List<String> result = watchListService.getAllGenres(userId);

        assertEquals(2, result.size());
        verify(watchListRepository).findDistinctGenresByUserId(userId);
    }

    @Test
    void testGetGenreStatistics() {
        when(watchListRepository.countByGenre(userId)).thenReturn(Collections.emptyList());
        watchListService.getGenreStatistics(userId);
        verify(watchListRepository).countByGenre(userId);
    }

    @Test
    void testGetStatusStatistics() {
        when(watchListRepository.countByStatusGroup(userId)).thenReturn(Collections.emptyList());
        watchListService.getStatusStatistics(userId);
        verify(watchListRepository).countByStatusGroup(userId);
    }

    @Test
    void testGetTypeStatistics() {
        when(watchListRepository.countByType(userId)).thenReturn(Collections.emptyList());
        watchListService.getTypeStatistics(userId);
        verify(watchListRepository).countByType(userId);
    }

    @Test
    void testCountByStatus() {
        when(watchListRepository.countByUserIdAndStatus(userId, "Watched")).thenReturn(5L);
        long count = watchListService.countByStatus(userId, "Watched");
        assertEquals(5L, count);
    }

    // ========================================================================
    // WRITE OPERATIONS TESTS
    // ========================================================================

    @Test
    void testCreateWatchList_WithStatus() {
        when(watchListRepository.save(any(WatchList.class))).thenAnswer(i -> i.getArguments()[0]);

        WatchList created = watchListService.createWatchList(userId, "Title", "Movie", "Action", 8, 2020, "Watched", "Note");

        assertNotNull(created);
        assertEquals("Watched", created.getStatus());
        verify(watchListRepository).save(any(WatchList.class));
    }

    @Test
    void testCreateWatchList_StatusNull_Default() {
        when(watchListRepository.save(any(WatchList.class))).thenAnswer(i -> i.getArguments()[0]);

        // Status passed is NULL
        WatchList created = watchListService.createWatchList(userId, "Title", "Movie", "Action", 8, 2020, null, "Note");

        assertNotNull(created);
        assertEquals("Plan to Watch", created.getStatus()); // Verify logic default value
    }

    @Test
    void testCreateWatchList_StatusEmpty_Default() {
        when(watchListRepository.save(any(WatchList.class))).thenAnswer(i -> i.getArguments()[0]);

        // Status passed is Empty
        WatchList created = watchListService.createWatchList(userId, "Title", "Movie", "Action", 8, 2020, "", "Note");

        assertEquals("Plan to Watch", created.getStatus());
    }

    @Test
    void testUpdateWatchList_Success() {
        when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(Optional.of(watchList));
        when(watchListRepository.save(any(WatchList.class))).thenAnswer(i -> i.getArguments()[0]);

        WatchList updated = watchListService.updateWatchList(
            userId, watchListId, "New Title", "Series", "Comedy", 9, 2021, "Watching", "New Note"
        );

        assertNotNull(updated);
        assertEquals("New Title", updated.getTitle());
        assertEquals("Watching", updated.getStatus());
    }

    @Test
    void testUpdateWatchList_NotFound() {
        when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(Optional.empty());

        WatchList updated = watchListService.updateWatchList(
            userId, watchListId, "Title", "Type", "Genre", 1, 2000, "Status", "Note"
        );

        assertNull(updated);
        verify(watchListRepository, never()).save(any());
    }

    @Test
    void testUpdateCover_Success() {
        // Perhatikan: updateCover menggunakan findById (generic), bukan findByIdAndUserId
        when(watchListRepository.findById(watchListId)).thenReturn(Optional.of(watchList));

        watchListService.updateCover(watchListId, "new_cover.jpg");

        assertEquals("new_cover.jpg", watchList.getCover());
        verify(watchListRepository).save(watchList);
    }

    @Test
    void testUpdateCover_NotFound() {
        when(watchListRepository.findById(watchListId)).thenReturn(Optional.empty());

        watchListService.updateCover(watchListId, "new_cover.jpg");

        verify(watchListRepository, never()).save(any());
    }

    @Test
    void testDeleteWatchList_Success() {
        when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(Optional.of(watchList));

        boolean result = watchListService.deleteWatchList(userId, watchListId);

        assertTrue(result);
        verify(watchListRepository).deleteByIdAndUserId(watchListId, userId);
    }

    @Test
    void testDeleteWatchList_NotFound() {
        when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(Optional.empty());

        boolean result = watchListService.deleteWatchList(userId, watchListId);

        assertFalse(result);
        verify(watchListRepository, never()).deleteByIdAndUserId(any(), any());
    }

    @Test
    void testCycleStatus_NotFound() {
        when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(Optional.empty());

        WatchList result = watchListService.cycleStatus(userId, watchListId);

        assertNull(result);
    }

    @Test
    void testCycleStatus_PlanToWatching() {
        watchList.setStatus("Plan to Watch");
        when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(Optional.of(watchList));
        when(watchListRepository.save(any(WatchList.class))).thenAnswer(i -> i.getArguments()[0]);

        WatchList result = watchListService.cycleStatus(userId, watchListId);

        assertEquals("Watching", result.getStatus());
    }

    @Test
    void testCycleStatus_WatchingToWatched() {
        watchList.setStatus("Watching");
        when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(Optional.of(watchList));
        when(watchListRepository.save(any(WatchList.class))).thenAnswer(i -> i.getArguments()[0]);

        WatchList result = watchListService.cycleStatus(userId, watchListId);

        assertEquals("Watched", result.getStatus());
    }

    @Test
    void testCycleStatus_WatchedToPlan() {
        watchList.setStatus("Watched");
        when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(Optional.of(watchList));
        when(watchListRepository.save(any(WatchList.class))).thenAnswer(i -> i.getArguments()[0]);

        WatchList result = watchListService.cycleStatus(userId, watchListId);

        assertEquals("Plan to Watch", result.getStatus());
    }

    @Test
    void testCycleStatus_UnknownToPlan() {
        watchList.setStatus("Unknown Status"); // Masuk ke "else" terakhir
        when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(Optional.of(watchList));
        when(watchListRepository.save(any(WatchList.class))).thenAnswer(i -> i.getArguments()[0]);

        WatchList result = watchListService.cycleStatus(userId, watchListId);

        assertEquals("Plan to Watch", result.getStatus());
    }
}