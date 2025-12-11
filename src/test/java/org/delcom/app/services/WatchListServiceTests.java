package org.delcom.app.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.delcom.app.entities.WatchList;
import org.delcom.app.repositories.WatchListRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class WatchListServiceTests {

    @Mock
    private WatchListRepository watchListRepository;

    @InjectMocks
    private WatchListService watchListService;

    private UUID userId;
    private UUID watchListId;
    private WatchList watchList;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userId = UUID.randomUUID();
        watchListId = UUID.randomUUID();

        // Setup Data Dummy
        watchList = new WatchList(
            userId, 
            "Inception", 
            "Movie", 
            "Sci-Fi", 
            9, 
            2010,
            "Watched", 
            "Film bagus"
        );
        watchList.setId(watchListId);
    }

    @Test
    @DisplayName("Test Read Operations (Get All, By ID, Stats)")
    void testReadOperations() {
        // 1. Test getAllWatchLists (Search NULL - Menggunakan OrderByCreatedAtDesc)
        when(watchListRepository.findAllByUserIdOrderByCreatedAtDesc(userId)).thenReturn(List.of(watchList));
        List<WatchList> resultNullSearch = watchListService.getAllWatchLists(userId, null);
        assertFalse(resultNullSearch.isEmpty());

        // [FIX] Test getAllWatchLists (Search KOSONG "" - Harus masuk ke OrderByCreatedAtDesc juga)
        // Ini menutup gap coverage pada !search.trim().isEmpty()
        List<WatchList> resultEmptySearch = watchListService.getAllWatchLists(userId, "");
        assertFalse(resultEmptySearch.isEmpty());
        
        List<WatchList> resultSpaceSearch = watchListService.getAllWatchLists(userId, "   ");
        assertFalse(resultSpaceSearch.isEmpty());

        // Verifikasi metode repository dipanggil 3 kali (1 untuk null, 1 untuk "", 1 untuk "   ")
        verify(watchListRepository, times(3)).findAllByUserIdOrderByCreatedAtDesc(userId);


        // 2. Test getAllWatchLists (Search Ada - Menggunakan findByUserIdWithSearch)
        when(watchListRepository.findByUserIdWithSearch(userId, "Inception")).thenReturn(List.of(watchList));
        List<WatchList> resultSearch = watchListService.getAllWatchLists(userId, "Inception");
        assertFalse(resultSearch.isEmpty());


        // 3. Test getWatchListById
        when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(Optional.of(watchList));
        WatchList resultId = watchListService.getWatchListById(userId, watchListId);
        assertNotNull(resultId);


        // 4. Test getWatchListsByType
        when(watchListRepository.findByUserIdAndType(userId, "Movie")).thenReturn(List.of(watchList));
        List<WatchList> resultType = watchListService.getWatchListsByType(userId, "Movie");
        assertFalse(resultType.isEmpty());


        // 5. Test getWatchListsByStatus
        when(watchListRepository.findByUserIdAndStatus(userId, "Watched")).thenReturn(List.of(watchList));
        List<WatchList> resultStatus = watchListService.getWatchListsByStatus(userId, "Watched");
        assertFalse(resultStatus.isEmpty());


        // 6. Test Statistics & Counts
        when(watchListRepository.findDistinctGenresByUserId(userId)).thenReturn(Arrays.asList("Sci-Fi"));
        assertNotNull(watchListService.getAllGenres(userId));

        when(watchListRepository.countByGenre(userId)).thenReturn(new ArrayList<>());
        assertNotNull(watchListService.getGenreStatistics(userId));

        when(watchListRepository.countByStatusGroup(userId)).thenReturn(new ArrayList<>());
        assertNotNull(watchListService.getStatusStatistics(userId));

        when(watchListRepository.countByType(userId)).thenReturn(new ArrayList<>());
        assertNotNull(watchListService.getTypeStatistics(userId));


        // 7. Test countByStatus (Single count)
        when(watchListRepository.countByUserIdAndStatus(userId, "Watched")).thenReturn(5L);
        long count = watchListService.countByStatus(userId, "Watched");
        assertEquals(5L, count);
    }

    @Test
    @DisplayName("Test Create WatchList")
    void testCreateWatchList() {
        when(watchListRepository.save(any(WatchList.class))).thenAnswer(i -> i.getArgument(0));

        // Case A: Normal
        WatchList created = watchListService.createWatchList(userId, "Title", "Movie", "Action", 8, 2024, "Watching", "Note");
        assertNotNull(created);

        // Case B: Status Null (Default ke Plan to Watch)
        WatchList resultNullStatus = watchListService.createWatchList(userId, "Title", "Movie", "Action", 8, 2024, null, "Note");
        assertEquals("Plan to Watch", resultNullStatus.getStatus());

        // [FIX] Case C: Status Empty String (Default ke Plan to Watch)
        // Ini menutup gap coverage pada status.isEmpty()
        WatchList resultEmptyStatus = watchListService.createWatchList(userId, "Title", "Movie", "Action", 8, 2024, "", "Note");
        assertEquals("Plan to Watch", resultEmptyStatus.getStatus());
    }

    @Test
    @DisplayName("Test Update WatchList")
    void testUpdateWatchList() {
        // Case A: Sukses
        when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(Optional.of(watchList));
        when(watchListRepository.save(any(WatchList.class))).thenReturn(watchList);

        WatchList updated = watchListService.updateWatchList(userId, watchListId, "New Title", "Series", "Drama", 10, 2025, "Plan to Watch", "New Note");
        assertNotNull(updated);
        assertEquals("New Title", updated.getTitle()); 

        // Case B: Tidak Ditemukan
        when(watchListRepository.findByIdAndUserId(eq(UUID.randomUUID()), eq(userId))).thenReturn(Optional.empty());
        WatchList notFound = watchListService.updateWatchList(userId, UUID.randomUUID(), "Title", "Movie", "Genre", 1, 2000, "Status", "Note");
        assertNull(notFound);
    }

    @Test
    @DisplayName("Test Delete WatchList")
    void testDeleteWatchList() {
        // Case A: Sukses
        when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(Optional.of(watchList));
        doNothing().when(watchListRepository).deleteByIdAndUserId(watchListId, userId);
        
        boolean result = watchListService.deleteWatchList(userId, watchListId);
        assertTrue(result);

        // Case B: Gagal (Tidak Ditemukan)
        when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(Optional.empty());
        boolean resultFail = watchListService.deleteWatchList(userId, watchListId);
        assertFalse(resultFail);
    }

    @Test
    @DisplayName("Test Cycle Status")
    void testCycleStatus() {
        when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(Optional.of(watchList));
        when(watchListRepository.save(any(WatchList.class))).thenReturn(watchList);

        // 1. Plan to Watch -> Watching
        watchList.setStatus("Plan to Watch");
        watchListService.cycleStatus(userId, watchListId);
        assertEquals("Watching", watchList.getStatus());

        // 2. Watching -> Watched
        watchList.setStatus("Watching");
        watchListService.cycleStatus(userId, watchListId);
        assertEquals("Watched", watchList.getStatus());

        // 3. Watched -> Plan to Watch
        watchList.setStatus("Watched");
        watchListService.cycleStatus(userId, watchListId);
        assertEquals("Plan to Watch", watchList.getStatus());

        // 4. Random Status -> Plan to Watch (Else case coverage)
        watchList.setStatus("Unknown Status");
        watchListService.cycleStatus(userId, watchListId);
        assertEquals("Plan to Watch", watchList.getStatus());

        // 5. Data Not Found
        when(watchListRepository.findByIdAndUserId(eq(UUID.randomUUID()), eq(userId))).thenReturn(Optional.empty());
        WatchList nullResult = watchListService.cycleStatus(userId, UUID.randomUUID());
        assertNull(nullResult);
    }

    @Test
    @DisplayName("Test Update Cover")
    void testUpdateCover() {
        // Case A: Sukses Found
        when(watchListRepository.findById(watchListId)).thenReturn(Optional.of(watchList));
        when(watchListRepository.save(any(WatchList.class))).thenReturn(watchList);

        watchListService.updateCover(watchListId, "new-cover.jpg");
        assertEquals("new-cover.jpg", watchList.getCover());
        verify(watchListRepository, times(1)).save(watchList);

        // [FIX] Case B: Not Found (Null)
        // Ini menutup gap coverage pada if(watchList != null)
        UUID randomId = UUID.randomUUID();
        when(watchListRepository.findById(randomId)).thenReturn(Optional.empty());
        
        watchListService.updateCover(randomId, "fail.jpg");
        // Verifikasi bahwa save TIDAK dipanggil lagi (total tetap 1 dari case A)
        verify(watchListRepository, times(1)).save(any());
    }
}