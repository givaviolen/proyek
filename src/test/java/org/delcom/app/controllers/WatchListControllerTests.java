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
        // Buat random UUID
        UUID userId = UUID.randomUUID();
        UUID watchListId = UUID.randomUUID();
        UUID nonexistentWatchListId = UUID.randomUUID();

        // Membuat dummy data dasar
        WatchList watchList = new WatchList(
            userId, 
            "The Shawshank Redemption", 
            "Movie", 
            "Drama", 
            10, 
            1994, 
            true, 
            "Classic film tentang harapan"
        );
        watchList.setId(watchListId);

        // Membuat mock Service
        WatchListService watchListService = Mockito.mock(WatchListService.class);

        // Atur perilaku mock default
        when(watchListService.createWatchList(
            any(UUID.class), 
            any(String.class), 
            any(String.class), 
            any(String.class), 
            any(Integer.class), 
            any(Integer.class), 
            any(Boolean.class), 
            any(String.class)
        )).thenReturn(watchList);

        // Membuat instance controller
        WatchListController watchListController = new WatchListController(watchListService);
        assert (watchListController != null);

        // Setup Auth Context
        watchListController.authContext = new AuthContext();
        User authUser = new User("Test User", "testuser@example.com");
        authUser.setId(userId);

        // ==========================================
        // 1. MENGUJI METHOD createWatchList
        // ==========================================
        {
            // --- Skenario Data Tidak Valid ---
            {
                List<WatchList> invalidWatchLists = List.of(
                    new WatchList(userId, null, "Movie", "Drama", 10, 1994, false, "Notes"), // Title Null
                    new WatchList(userId, "", "Movie", "Drama", 10, 1994, false, "Notes"),   // Title Empty
                    new WatchList(userId, "Title", null, "Drama", 10, 1994, false, "Notes"), // Type Null
                    new WatchList(userId, "Title", "", "Drama", 10, 1994, false, "Notes"),   // Type Empty
                    new WatchList(userId, "Title", "InvalidType", "Drama", 10, 1994, false, "Notes"), // Type Invalid
                    new WatchList(userId, "Title", "Movie", null, 10, 1994, false, "Notes"), // Genre Null
                    new WatchList(userId, "Title", "Movie", "", 10, 1994, false, "Notes"),   // Genre Empty
                    new WatchList(userId, "Title", "Movie", "Drama", null, 1994, false, "Notes"), // Rating Null
                    new WatchList(userId, "Title", "Movie", "Drama", 0, 1994, false, "Notes"),    // Rating < 1
                    new WatchList(userId, "Title", "Movie", "Drama", 11, 1994, false, "Notes"),   // Rating > 10
                    new WatchList(userId, "Title", "Movie", "Drama", 10, null, false, "Notes"),   // Year Null
                    new WatchList(userId, "Title", "Movie", "Drama", 10, 1899, false, "Notes")    // Year < 1900
                );

                ResponseEntity<ApiResponse<Map<String, UUID>>> result;
                for (WatchList itemWatchList : invalidWatchLists) {
                    result = watchListController.createWatchList(itemWatchList);
                    assert (result != null);
                    assert (result.getStatusCode().is4xxClientError());
                    assert (result.getBody().getStatus().equals("fail"));
                }
            }

            // --- Skenario Autentikasi Gagal ---
            {
                watchListController.authContext.setAuthUser(null);
                var result = watchListController.createWatchList(watchList);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            // --- Skenario Berhasil (Normal - Movie) ---
            {
                watchListController.authContext.setAuthUser(authUser);
                var result = watchListController.createWatchList(watchList);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }

            // --- [FIX] Skenario Berhasil (Type = "Series") ---
            // Ini untuk menutupi branch logic !equals("Movie") && !equals("Series")
            {
                WatchList seriesWatchList = new WatchList(
                    userId, "Breaking Bad", "Series", "Crime", 10, 2008, true, "Best Series"
                );
                var result = watchListController.createWatchList(seriesWatchList);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }

            // --- [FIX] Skenario Berhasil (isWatched = NULL) ---
            // Ini untuk menutupi branch logic: getIsWatched() != null ? ... : false
            {
                WatchList nullWatchedList = new WatchList(
                    userId, "Unknown Movie", "Movie", "Mystery", 5, 2020, null, "Notes"
                );
                // Pastikan Service dipanggil (mocking sudah diatur di 'any(Boolean.class)')
                var result = watchListController.createWatchList(nullWatchedList);
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

            // Sukses dengan search Null
            {
                watchListController.authContext.setAuthUser(authUser);
                List<WatchList> dummyResponse = List.of(watchList);
                when(watchListService.getAllWatchLists(any(UUID.class), any(String.class)))
                    .thenReturn(dummyResponse);
                
                var result = watchListController.getAllWatchLists(null);
                assert (result.getBody().getStatus().equals("success"));
            }

            // Sukses dengan search String
            {
                var result = watchListController.getAllWatchLists("shawshank");
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

            // ID Ditemukan
            {
                when(watchListService.getWatchListById(any(UUID.class), any(UUID.class)))
                    .thenReturn(watchList);
                
                var result = watchListController.getWatchListById(watchListId);
                assert (result.getBody().getStatus().equals("success"));
                assert (result.getBody().getData().get("watch_list").getId().equals(watchListId));
            }

            // ID Tidak Ditemukan
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
            // Gagal Auth
            {
                watchListController.authContext.setAuthUser(null);
                var result = watchListController.getWatchListGenres();
                assert (result.getStatusCode().is4xxClientError());
            }

            // Sukses
            {
                watchListController.authContext.setAuthUser(authUser);
                List<String> dummyGenres = List.of("Drama", "Action");
                when(watchListService.getAllGenres(any(UUID.class))).thenReturn(dummyGenres);
                
                var result = watchListController.getWatchListGenres();
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // ==========================================
        // 5. MENGUJI METHOD updateWatchList
        // ==========================================
        {
            // --- Skenario Data Tidak Valid ---
            {
                // Kita gunakan list yang sama seperti create
                List<WatchList> invalidWatchLists = List.of(
                    new WatchList(userId, null, "Movie", "Drama", 10, 1994, false, "Notes"),
                    new WatchList(userId, "", "Movie", "Drama", 10, 1994, false, "Notes"),
                    new WatchList(userId, "Title", null, "Drama", 10, 1994, false, "Notes"),
                    new WatchList(userId, "Title", "", "Drama", 10, 1994, false, "Notes"),
                    new WatchList(userId, "Title", "InvalidType", "Drama", 10, 1994, false, "Notes"),
                    new WatchList(userId, "Title", "Movie", null, 10, 1994, false, "Notes"),
                    new WatchList(userId, "Title", "Movie", "", 10, 1994, false, "Notes"),
                    new WatchList(userId, "Title", "Movie", "Drama", null, 1994, false, "Notes"),
                    new WatchList(userId, "Title", "Movie", "Drama", 0, 1994, false, "Notes"),
                    new WatchList(userId, "Title", "Movie", "Drama", 11, 1994, false, "Notes"),
                    new WatchList(userId, "Title", "Movie", "Drama", 10, null, false, "Notes"),
                    new WatchList(userId, "Title", "Movie", "Drama", 10, 1899, false, "Notes")
                );

                for (WatchList itemWatchList : invalidWatchLists) {
                    var result = watchListController.updateWatchList(watchListId, itemWatchList);
                    assert (result.getStatusCode().is4xxClientError());
                    assert (result.getBody().getStatus().equals("fail"));
                }
            }

            // Gagal Auth
            {
                watchListController.authContext.setAuthUser(null);
                var result = watchListController.updateWatchList(watchListId, watchList);
                assert (result.getStatusCode().is4xxClientError());
            }

            watchListController.authContext.setAuthUser(authUser);

            // ID Tidak Ditemukan
            {
                when(watchListService.updateWatchList(
                    any(UUID.class), any(UUID.class), any(String.class), any(String.class), 
                    any(String.class), any(Integer.class), any(Integer.class), 
                    any(Boolean.class), any(String.class)
                )).thenReturn(null);
                
                var result = watchListController.updateWatchList(nonexistentWatchListId, watchList);
                assert (result.getBody().getStatus().equals("fail"));
            }

            // --- Skenario Berhasil (Normal) ---
            {
                WatchList updatedWatchList = new WatchList(
                    userId, "Updated Title", "Movie", "Drama", 10, 1994, true, "Updated"
                );
                updatedWatchList.setId(watchListId);
                
                when(watchListService.updateWatchList(
                    any(UUID.class), eq(watchListId), any(String.class), any(String.class), 
                    any(String.class), any(Integer.class), any(Integer.class), 
                    any(Boolean.class), any(String.class)
                )).thenReturn(updatedWatchList);

                var result = watchListController.updateWatchList(watchListId, updatedWatchList);
                assert (result.getBody().getStatus().equals("success"));
            }

            // --- [FIX] Skenario Berhasil (Type = "Series") untuk Update ---
            {
                WatchList seriesUpdate = new WatchList(
                    userId, "Updated Series", "Series", "Drama", 9, 2021, true, "Notes"
                );
                // Mock return value doesn't strictly matter for coverage unless it returns null
                when(watchListService.updateWatchList(
                    any(UUID.class), eq(watchListId), any(String.class), eq("Series"), 
                    any(String.class), any(Integer.class), any(Integer.class), 
                    any(Boolean.class), any(String.class)
                )).thenReturn(seriesUpdate);

                var result = watchListController.updateWatchList(watchListId, seriesUpdate);
                assert (result.getBody().getStatus().equals("success"));
            }

            // --- [FIX] Skenario Berhasil (isWatched = NULL) untuk Update ---
            {
                WatchList nullWatchedUpdate = new WatchList(
                    userId, "Updated Null", "Movie", "Drama", 9, 2021, null, "Notes"
                );
                // Kita perlu memastikan mock mengembalikan object valid agar tidak 404
                when(watchListService.updateWatchList(
                    any(UUID.class), eq(watchListId), any(String.class), any(String.class), 
                    any(String.class), any(Integer.class), any(Integer.class), 
                    eq(false), // Expect false karena null dikonversi ke false di controller
                    any(String.class)
                )).thenReturn(nullWatchedUpdate);

                var result = watchListController.updateWatchList(watchListId, nullWatchedUpdate);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // ==========================================
        // 6. MENGUJI METHOD deleteWatchList
        // ==========================================
        {
            // Gagal Auth
            {
                watchListController.authContext.setAuthUser(null);
                var result = watchListController.deleteWatchList(watchListId);
                assert (result.getStatusCode().is4xxClientError());
            }

            watchListController.authContext.setAuthUser(authUser);

            // Gagal Hapus (ID Salah)
            {
                when(watchListService.deleteWatchList(any(UUID.class), any(UUID.class))).thenReturn(false);
                var result = watchListController.deleteWatchList(nonexistentWatchListId);
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Berhasil Hapus
            {
                when(watchListService.deleteWatchList(any(UUID.class), any(UUID.class))).thenReturn(true);
                var result = watchListController.deleteWatchList(watchListId);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // ==========================================
        // 7. MENGUJI METHOD toggleWatchedStatus
        // ==========================================
        {
            // Gagal Auth
            {
                watchListController.authContext.setAuthUser(null);
                var result = watchListController.toggleWatchedStatus(watchListId);
                assert (result.getStatusCode().is4xxClientError());
            }

            watchListController.authContext.setAuthUser(authUser);

            // Gagal Toggle (ID Salah)
            {
                when(watchListService.toggleWatchedStatus(any(UUID.class), any(UUID.class))).thenReturn(null);
                var result = watchListController.toggleWatchedStatus(nonexistentWatchListId);
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Berhasil Toggle
            {
                WatchList toggled = new WatchList(userId, "Title", "Movie", "Genre", 5, 2020, false, "Note");
                toggled.setId(watchListId);
                when(watchListService.toggleWatchedStatus(any(UUID.class), any(UUID.class))).thenReturn(toggled);
                
                var result = watchListController.toggleWatchedStatus(watchListId);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // ==========================================
        // 8. MENGUJI METHOD getStatistics
        // ==========================================
        {
            // Gagal Auth
            {
                watchListController.authContext.setAuthUser(null);
                var result = watchListController.getStatistics();
                assert (result.getStatusCode().is4xxClientError());
            }

            // Berhasil Get Stats
            {
                watchListController.authContext.setAuthUser(authUser);

                when(watchListService.getGenreStatistics(any(UUID.class))).thenReturn(List.of());
                when(watchListService.getWatchedStatistics(any(UUID.class))).thenReturn(List.of());
                when(watchListService.getTypeStatistics(any(UUID.class))).thenReturn(List.of());
                
                var result = watchListController.getStatistics();
                assert (result.getBody().getStatus().equals("success"));
            }
        }
    }
}