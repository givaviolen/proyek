package org.delcom.app.services;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.delcom.app.entities.WatchList;
import org.delcom.app.repositories.WatchListRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class WatchListServiceTests {
    
    @Test
    @DisplayName("Pengujian untuk service WatchList")
    void testWatchListService() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID watchListId = UUID.randomUUID();
        UUID nonexistentWatchListId = UUID.randomUUID();

        // [FIX] Gunakan String "Watched"
        WatchList watchList = new WatchList(
            userId, 
            "Inception", 
            "Movie", 
            "Sci-Fi", 
            9, 
            2010,
            "Watched", 
            "Film sangat bagus"
        );
        watchList.setId(watchListId);

        WatchListRepository watchListRepository = Mockito.mock(WatchListRepository.class);

        when(watchListRepository.save(any(WatchList.class))).thenReturn(watchList);
        when(watchListRepository.findByUserId(userId)).thenReturn(List.of(watchList));
        when(watchListRepository.findByUserIdWithSearch(userId, "Inception")).thenReturn(List.of(watchList));
        when(watchListRepository.findByUserIdAndType(userId, "Movie")).thenReturn(List.of(watchList));
        
        // [FIX] Mengganti findWatchedByUserId dengan findByUserIdAndStatus
        when(watchListRepository.findByUserIdAndStatus(userId, "Watched")).thenReturn(List.of(watchList));
        when(watchListRepository.findByUserIdAndStatus(userId, "Plan to Watch")).thenReturn(new ArrayList<>());
        
        when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(java.util.Optional.of(watchList));
        when(watchListRepository.findByIdAndUserId(nonexistentWatchListId, userId)).thenReturn(java.util.Optional.empty());
        when(watchListRepository.findById(watchListId)).thenReturn(java.util.Optional.of(watchList));
        
        when(watchListRepository.findDistinctGenresByUserId(userId)).thenReturn(Arrays.asList("Sci-Fi", "Action"));
        doNothing().when(watchListRepository).deleteByIdAndUserId(any(UUID.class), any(UUID.class));

        when(watchListRepository.countByGenre(userId)).thenReturn(new ArrayList<>());
        // [FIX] Mengganti countByWatchedStatus dengan countByStatusGroup
        when(watchListRepository.countByStatusGroup(userId)).thenReturn(new ArrayList<>());
        when(watchListRepository.countByType(userId)).thenReturn(new ArrayList<>());

        WatchListService watchListService = new WatchListService(watchListRepository);
        assert (watchListService != null);

        // Menguji createWatchList
        {
            WatchList createdWatchList = watchListService.createWatchList(
                userId, 
                watchList.getTitle(), 
                watchList.getType(), 
                watchList.getGenre(), 
                watchList.getRating(), 
                watchList.getReleaseYear(),
                watchList.getStatus(), // [FIX] getStatus
                watchList.getNotes()
            );
            assert (createdWatchList != null);
            assert (createdWatchList.getStatus().equals("Watched"));
        }

        // Menguji getAllWatchLists (Logic sama)
        {
            List<WatchList> result = watchListService.getAllWatchLists(userId, null);
            assert (result.size() == 1);
        }

        // [FIX] Menguji getWatchListsByStatus (pengganti getWatchedWatchLists)
        {
            List<WatchList> result = watchListService.getWatchListsByStatus(userId, "Watched");
            assert (result.size() == 1);
            assert (result.get(0).getStatus().equals("Watched"));
        }

        // Menguji updateWatchList
        {
            // [FIX] Param String
            WatchList updated = watchListService.updateWatchList(
                userId, 
                watchListId, 
                "Interstellar", 
                "Movie", 
                "Sci-Fi", 
                10,
                2014,
                "Watching", 
                "Masterpiece"
            );
            assert (updated != null);
            assert (updated.getTitle().equals("Interstellar"));
        }

        // Menguji cycleStatus (Pengganti Toggle)
        {
            // Awalnya "Watched"
            watchList.setStatus("Watched");
            when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(java.util.Optional.of(watchList));
            
            // [FIX] Panggil cycleStatus
            WatchList cycled = watchListService.cycleStatus(userId, watchListId);
            assert (cycled != null);
            // Logika cycle: Watched -> Plan to Watch
            assert (cycled.getStatus().equals("Plan to Watch")); 
            
            // Cycle lagi: Plan to Watch -> Watching
            cycled = watchListService.cycleStatus(userId, watchListId);
            assert (cycled.getStatus().equals("Watching"));
        }

        // Menguji statistics
        {
            assert(watchListService.getStatusStatistics(userId) != null); // [FIX]
        }
    }
}