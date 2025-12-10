package org.delcom.app.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.WatchList;
import org.delcom.app.entities.User;
import org.delcom.app.services.WatchListService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

public class WatchListControllerTests {
    
    @Test
    @DisplayName("Pengujian untuk controller WatchList")
    void testWatchListController() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID watchListId = UUID.randomUUID();
        UUID nonexistentWatchListId = UUID.randomUUID();

        // [FIX] Menggunakan String Status "Watched"
        WatchList watchList = new WatchList(
            userId, 
            "The Shawshank Redemption", 
            "Movie", 
            "Drama", 
            10, 
            1994, 
            "Watched", 
            "Classic film tentang harapan"
        );
        watchList.setId(watchListId);

        WatchListService watchListService = Mockito.mock(WatchListService.class);

        // [FIX] Mocking create dengan parameter Status berupa String
        when(watchListService.createWatchList(
            any(UUID.class), 
            any(String.class), 
            any(String.class), 
            any(String.class), 
            any(Integer.class), 
            any(Integer.class), 
            any(String.class), // Status is now String
            any(String.class)
        )).thenReturn(watchList);

        WatchListController watchListController = new WatchListController(watchListService);
        
        // Setup Auth Context
        watchListController.authContext = new AuthContext();
        User authUser = new User("Test User", "testuser@example.com");
        authUser.setId(userId);

        // ==========================================
        // 1. MENGUJI METHOD createWatchList
        // ==========================================
        {
            {
                // [FIX] List Invalid Inputs (Constructor disesuaikan dengan status String)
                List<WatchList> invalidWatchLists = List.of(
                    new WatchList(userId, null, "Movie", "Drama", 10, 1994, "Plan to Watch", "Notes"),
                    new WatchList(userId, "", "Movie", "Drama", 10, 1994, "Plan to Watch", "Notes"),
                    new WatchList(userId, "Title", null, "Drama", 10, 1994, "Plan to Watch", "Notes"),
                    new WatchList(userId, "Title", "", "Drama", 10, 1994, "Plan to Watch", "Notes"),
                    new WatchList(userId, "Title", "InvalidType", "Drama", 10, 1994, "Plan to Watch", "Notes"),
                    new WatchList(userId, "Title", "Movie", null, 10, 1994, "Plan to Watch", "Notes"),
                    new WatchList(userId, "Title", "Movie", "", 10, 1994, "Plan to Watch", "Notes"),
                    new WatchList(userId, "Title", "Movie", "Drama", null, 1994, "Plan to Watch", "Notes"),
                    new WatchList(userId, "Title", "Movie", "Drama", 0, 1994, "Plan to Watch", "Notes"),
                    new WatchList(userId, "Title", "Movie", "Drama", 11, 1994, "Plan to Watch", "Notes"),
                    new WatchList(userId, "Title", "Movie", "Drama", 10, null, "Plan to Watch", "Notes"),
                    new WatchList(userId, "Title", "Movie", "Drama", 10, 1899, "Plan to Watch", "Notes")
                );

                ResponseEntity<ApiResponse<Map<String, UUID>>> result;
                for (WatchList itemWatchList : invalidWatchLists) {
                    result = watchListController.createWatchList(itemWatchList);
                    assert (result != null);
                    assert (result.getStatusCode().is4xxClientError());
                    assert (result.getBody().getStatus().equals("fail"));
                }
            }

            // Auth Gagal
            {
                watchListController.authContext.setAuthUser(null);
                var result = watchListController.createWatchList(watchList);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
            }

            // Sukses Normal
            {
                watchListController.authContext.setAuthUser(authUser);
                var result = watchListController.createWatchList(watchList);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }

            // Sukses Series dengan Status "Watching"
            {
                WatchList seriesWatchList = new WatchList(
                    userId, "Breaking Bad", "Series", "Crime", 10, 2008, "Watching", "Best Series"
                );
                // Mock return value for this specific call if necessary, or rely on generic any()
                when(watchListService.createWatchList(
                    eq(userId), eq("Breaking Bad"), eq("Series"), eq("Crime"), 
                    eq(10), eq(2008), eq("Watching"), eq("Best Series")
                )).thenReturn(seriesWatchList);

                var result = watchListController.createWatchList(seriesWatchList);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }

            // [FIX] Status NULL (Controller harus handle default value ke "Plan to Watch")
            {
                WatchList nullStatusList = new WatchList(
                    userId, "Unknown Movie", "Movie", "Mystery", 5, 2020, null, "Notes"
                );
                var result = watchListController.createWatchList(nullStatusList);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // ==========================================
        // 2. MENGUJI METHOD getAllWatchLists
        // ==========================================
        {
            // Gagal Auth
            {
                watchListController.authContext.setAuthUser(null);
                var result = watchListController.getAllWatchLists(null);
                assert (result.getStatusCode().is4xxClientError());
            }
            // Sukses
            {
                watchListController.authContext.setAuthUser(authUser);
                List<WatchList> dummyResponse = List.of(watchList);
                when(watchListService.getAllWatchLists(any(UUID.class), any()))
                    .thenReturn(dummyResponse);
                
                var result = watchListController.getAllWatchLists(null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // ==========================================
        // 3. MENGUJI METHOD getWatchListById
        // ==========================================
        {
            // Gagal Auth
            {
                watchListController.authContext.setAuthUser(null);
                var result = watchListController.getWatchListById(watchListId);
                assert (result.getStatusCode().is4xxClientError());
            }
            watchListController.authContext.setAuthUser(authUser);
            // Sukses
            {
                when(watchListService.getWatchListById(any(UUID.class), any(UUID.class)))
                    .thenReturn(watchList);
                var result = watchListController.getWatchListById(watchListId);
                assert (result.getBody().getStatus().equals("success"));
            }
            // Gagal Found
            {
                when(watchListService.getWatchListById(any(UUID.class), any(UUID.class)))
                    .thenReturn(null);
                var result = watchListController.getWatchListById(nonexistentWatchListId);
                assert (result.getBody().getStatus().equals("fail"));
            }
        }

        // ==========================================
        // 4. MENGUJI METHOD getWatchListGenres
        // ==========================================
        {
            watchListController.authContext.setAuthUser(authUser);
            List<String> dummyGenres = List.of("Drama", "Action");
            when(watchListService.getAllGenres(any(UUID.class))).thenReturn(dummyGenres);
            var result = watchListController.getWatchListGenres();
            assert (result.getBody().getStatus().equals("success"));
        }

        // ==========================================
        // 5. MENGUJI METHOD updateWatchList
        // ==========================================
        {
            watchListController.authContext.setAuthUser(authUser);

            // Mock Gagal (ID Tidak Ditemukan)
            {
                // [FIX] Update matcher signatures to expect String for status
                when(watchListService.updateWatchList(
                    any(UUID.class), any(UUID.class), any(String.class), any(String.class), 
                    any(String.class), any(Integer.class), any(Integer.class), 
                    any(String.class), any(String.class)
                )).thenReturn(null);
                
                var result = watchListController.updateWatchList(nonexistentWatchListId, watchList);
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Mock Sukses
            {
                WatchList updatedWatchList = new WatchList(
                    userId, "Updated Title", "Movie", "Drama", 10, 1994, "Watched", "Updated"
                );
                updatedWatchList.setId(watchListId);
                
                // [FIX] Update matcher signatures to expect String for status
                when(watchListService.updateWatchList(
                    any(UUID.class), eq(watchListId), any(String.class), any(String.class), 
                    any(String.class), any(Integer.class), any(Integer.class), 
                    any(String.class), any(String.class)
                )).thenReturn(updatedWatchList);

                var result = watchListController.updateWatchList(watchListId, updatedWatchList);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // ==========================================
        // 6. MENGUJI METHOD deleteWatchList
        // ==========================================
        {
            watchListController.authContext.setAuthUser(authUser);
            when(watchListService.deleteWatchList(any(UUID.class), any(UUID.class))).thenReturn(true);
            var result = watchListController.deleteWatchList(watchListId);
            assert (result.getBody().getStatus().equals("success"));
        }

        // ==========================================
        // 7. MENGUJI METHOD toggleWatchedStatus (Diganti ke cycleStatus)
        // ==========================================
        {
            watchListController.authContext.setAuthUser(authUser);

            // Gagal Toggle
            {
                // [FIX] Menggunakan cycleStatus untuk perpindahan status
                when(watchListService.cycleStatus(any(UUID.class), any(UUID.class))).thenReturn(null);
                var result = watchListController.toggleWatchedStatus(nonexistentWatchListId);
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Berhasil Toggle
            {
                WatchList toggled = new WatchList(userId, "Title", "Movie", "Genre", 5, 2020, "Watching", "Note");
                toggled.setId(watchListId);
                
                when(watchListService.cycleStatus(any(UUID.class), any(UUID.class))).thenReturn(toggled);
                
                var result = watchListController.toggleWatchedStatus(watchListId);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // ==========================================
        // 8. MENGUJI METHOD getStatistics
        // ==========================================
        {
            watchListController.authContext.setAuthUser(authUser);
            
            // Mocking semua statistik
            when(watchListService.getGenreStatistics(any(UUID.class))).thenReturn(List.of());
            // [FIX] Menggunakan getStatusStatistics untuk support 3 status
            when(watchListService.getStatusStatistics(any(UUID.class))).thenReturn(List.of());
            when(watchListService.getTypeStatistics(any(UUID.class))).thenReturn(List.of());
            
            var result = watchListController.getStatistics();
            assert (result.getBody().getStatus().equals("success"));
        }
    }
}