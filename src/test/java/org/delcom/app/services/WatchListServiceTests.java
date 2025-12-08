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
        // Buat random UUID
        UUID userId = UUID.randomUUID();
        UUID watchListId = UUID.randomUUID();
        UUID nonexistentWatchListId = UUID.randomUUID();

        // Membuat dummy data
        WatchList watchList = new WatchList(
            userId, 
            "Inception", 
            "Movie", 
            "Sci-Fi", 
            9, 
            2010,
            true, // isWatched
            "Film sangat bagus"
        );
        watchList.setId(watchListId);

        // Membuat mock WatchListRepository
        WatchListRepository watchListRepository = Mockito.mock(WatchListRepository.class);

        // Atur perilaku mock
        when(watchListRepository.save(any(WatchList.class))).thenReturn(watchList);
        
        // Mocking pencarian umum
        when(watchListRepository.findByUserId(userId)).thenReturn(List.of(watchList));
        when(watchListRepository.findByUserIdWithSearch(userId, "Inception")).thenReturn(List.of(watchList));
        
        // Mocking pencarian spesifik
        when(watchListRepository.findByUserIdAndType(userId, "Movie")).thenReturn(List.of(watchList));
        when(watchListRepository.findWatchedByUserId(userId)).thenReturn(List.of(watchList));
        when(watchListRepository.findUnwatchedByUserId(userId)).thenReturn(new ArrayList<>()); // Asumsi list kosong
        
        // Mocking findById
        when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(java.util.Optional.of(watchList));
        when(watchListRepository.findByIdAndUserId(nonexistentWatchListId, userId)).thenReturn(java.util.Optional.empty());
        when(watchListRepository.findById(watchListId)).thenReturn(java.util.Optional.of(watchList));
        when(watchListRepository.findById(nonexistentWatchListId)).thenReturn(java.util.Optional.empty());
        
        // Mocking fitur lain
        when(watchListRepository.findDistinctGenresByUserId(userId)).thenReturn(Arrays.asList("Sci-Fi", "Action", "Drama"));
        doNothing().when(watchListRepository).deleteByIdAndUserId(any(UUID.class), any(UUID.class));

        // Mocking Statistik (Optional, untuk coverage)
        when(watchListRepository.countByGenre(userId)).thenReturn(new ArrayList<>());
        when(watchListRepository.countByWatchedStatus(userId)).thenReturn(new ArrayList<>());
        when(watchListRepository.countByType(userId)).thenReturn(new ArrayList<>());

        // Membuat instance service
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
                watchList.getIsWatched(),
                watchList.getNotes()
            );
            assert (createdWatchList != null);
            assert (createdWatchList.getId().equals(watchListId));
            assert (createdWatchList.getTitle().equals(watchList.getTitle()));
            assert (createdWatchList.getType().equals("Movie"));
            assert (createdWatchList.getRating().equals(9));
        }

        // Menguji getAllWatchLists tanpa pencarian
        {
            List<WatchList> result = watchListService.getAllWatchLists(userId, null);
            assert (result.size() == 1);
            assert (result.get(0).getId().equals(watchListId));
        }

        // Menguji getAllWatchLists dengan pencarian
        {
            List<WatchList> result = watchListService.getAllWatchLists(userId, "Inception");
            assert (result.size() == 1);
            assert (result.get(0).getTitle().equals("Inception"));
        }

        // Menguji getAllWatchLists dengan pencarian kosong
        {
            List<WatchList> result = watchListService.getAllWatchLists(userId, "");
            assert (result.size() == 1);
        }

        // Menguji getWatchListById
        {
            WatchList fetched = watchListService.getWatchListById(userId, watchListId);
            assert (fetched != null);
            assert (fetched.getId().equals(watchListId));
            assert (fetched.getTitle().equals(watchList.getTitle()));
        }

        // Menguji getWatchListById dengan ID yang tidak ada
        {
            WatchList fetched = watchListService.getWatchListById(userId, nonexistentWatchListId);
            assert (fetched == null);
        }

        // Menguji getWatchListsByType
        {
            List<WatchList> result = watchListService.getWatchListsByType(userId, "Movie");
            assert (result.size() == 1);
            assert (result.get(0).getType().equals("Movie"));
        }

        // Menguji getWatchedWatchLists
        {
            List<WatchList> result = watchListService.getWatchedWatchLists(userId);
            assert (result.size() == 1);
            assert (result.get(0).getIsWatched() == true);
        }

        // Menguji getUnwatchedWatchLists
        {
            List<WatchList> result = watchListService.getUnwatchedWatchLists(userId);
            assert (result.isEmpty());
        }

        // Menguji getAllGenres
        {
            List<String> genres = watchListService.getAllGenres(userId);
            assert (genres.size() == 3);
            assert (genres.contains("Sci-Fi"));
            assert (genres.contains("Action"));
        }

        // Menguji updateWatchList
        {
            String updatedTitle = "Interstellar";
            String updatedType = "Movie";
            String updatedGenre = "Sci-Fi";
            Integer updatedRating = 10;
            Integer updatedYear = 2014;
            Boolean updatedWatched = true;
            String updatedNotes = "Masterpiece";

            WatchList updated = watchListService.updateWatchList(
                userId, 
                watchListId, 
                updatedTitle, 
                updatedType, 
                updatedGenre, 
                updatedRating,
                updatedYear,
                updatedWatched,
                updatedNotes
            );
            assert (updated != null);
            assert (updated.getTitle().equals(updatedTitle));
            assert (updated.getRating().equals(updatedRating));
            assert (updated.getReleaseYear().equals(updatedYear));
            assert (updated.getNotes().equals(updatedNotes));
        }

        // Menguji updateWatchList dengan ID yang tidak ada
        {
            WatchList updated = watchListService.updateWatchList(
                userId, 
                nonexistentWatchListId, 
                "Title", "Movie", "Genre", 5, 2020, false, "Note"
            );
            assert (updated == null);
        }

        // Menguji method updateCover dengan watchlist yang tidak ada
        {
            UUID newId = UUID.randomUUID();
            when(watchListRepository.findById(newId)).thenReturn(java.util.Optional.empty());
            
            watchListService.updateCover(newId, "poster.jpg");
            // Tidak error
        }

        // Menguji method updateCover dengan watchlist yang ada
        {
            String newCover = "poster-inception.jpg";
            
            when(watchListRepository.findById(watchListId)).thenReturn(java.util.Optional.of(watchList));
            // Mock behavior save tidak perlu spesifik karena void return type pada setCover di entity, 
            // tapi kita mock save repository
            
            watchList.setCover(null);
            watchListService.updateCover(watchListId, newCover);
            assert (watchList.getCover().equals(newCover));
        }

        // Menguji toggleWatchedStatus
        {
            // Awalnya true (dari dummy data)
            watchList.setIsWatched(true);
            when(watchListRepository.findByIdAndUserId(watchListId, userId)).thenReturn(java.util.Optional.of(watchList));
            
            WatchList toggled = watchListService.toggleWatchedStatus(userId, watchListId);
            assert (toggled != null);
            assert (toggled.getIsWatched() == false); // Harus berubah jadi false
            
            // Toggle lagi
            toggled = watchListService.toggleWatchedStatus(userId, watchListId);
            assert (toggled.getIsWatched() == true); // Harus kembali jadi true
        }

        // Menguji toggleWatchedStatus ID tidak ada
        {
            WatchList toggled = watchListService.toggleWatchedStatus(userId, nonexistentWatchListId);
            assert (toggled == null);
        }

        // Menguji deleteWatchList
        {
            boolean deleted = watchListService.deleteWatchList(userId, watchListId);
            assert (deleted == true);
        }

        // Menguji deleteWatchList dengan ID yang tidak ada
        {
            boolean deleted = watchListService.deleteWatchList(userId, nonexistentWatchListId);
            assert (deleted == false);
        }
        
        // Menguji method statistics (memastikan tidak error saat dipanggil)
        {
            assert(watchListService.getGenreStatistics(userId) != null);
            assert(watchListService.getWatchedStatistics(userId) != null);
            assert(watchListService.getTypeStatistics(userId) != null);
        }
    }
}