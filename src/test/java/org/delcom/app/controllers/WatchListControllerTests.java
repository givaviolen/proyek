package org.delcom.app.controllers;

import static org.mockito.ArgumentMatchers.any;
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

        // Membuat dummy data
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

        // Atur perilaku mock
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

        watchListController.authContext = new AuthContext();
        User authUser = new User("Test User", "testuser@example.com");
        authUser.setId(userId);

        // Menguji method createWatchList
        {
            // Data tidak valid
            {
                List<WatchList> invalidWatchLists = List.of(
                    // Title Null
                    new WatchList(userId, null, "Movie", "Drama", 10, 1994, false, "Notes"),
                    // Title Kosong
                    new WatchList(userId, "", "Movie", "Drama", 10, 1994, false, "Notes"),
                    // Type Null
                    new WatchList(userId, "Title", null, "Drama", 10, 1994, false, "Notes"),
                    // Type Kosong
                    new WatchList(userId, "Title", "", "Drama", 10, 1994, false, "Notes"),
                    // Type Invalid (bukan Movie/Series)
                    new WatchList(userId, "Title", "InvalidType", "Drama", 10, 1994, false, "Notes"),
                    // Genre Null
                    new WatchList(userId, "Title", "Movie", null, 10, 1994, false, "Notes"),
                    // Genre Kosong
                    new WatchList(userId, "Title", "Movie", "", 10, 1994, false, "Notes"),
                    // Rating Null
                    new WatchList(userId, "Title", "Movie", "Drama", null, 1994, false, "Notes"),
                    // Rating kurang dari 1
                    new WatchList(userId, "Title", "Movie", "Drama", 0, 1994, false, "Notes"),
                    // Rating lebih dari 10
                    new WatchList(userId, "Title", "Movie", "Drama", 11, 1994, false, "Notes"),
                    // ReleaseYear Null
                    new WatchList(userId, "Title", "Movie", "Drama", 10, null, false, "Notes"),
                    // ReleaseYear invalid (kurang dari 1900)
                    new WatchList(userId, "Title", "Movie", "Drama", 10, 1899, false, "Notes")
                );

                ResponseEntity<ApiResponse<Map<String, UUID>>> result;
                for (WatchList itemWatchList : invalidWatchLists) {
                    result = watchListController.createWatchList(itemWatchList);
                    assert (result != null);
                    assert (result.getStatusCode().is4xxClientError());
                    assert (result.getBody().getStatus().equals("fail"));
                }
            }

            // Tidak terautentikasi untuk menambahkan watchlist
            {
                watchListController.authContext.setAuthUser(null);

                var result = watchListController.createWatchList(watchList);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Berhasil menambahkan watchlist
            {
                watchListController.authContext.setAuthUser(authUser);
                var result = watchListController.createWatchList(watchList);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // Menguji method getAllWatchLists
        {
            // Tidak terautentikasi untuk getAllWatchLists
            {
                watchListController.authContext.setAuthUser(null);

                var result = watchListController.getAllWatchLists(null);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Menguji getAllWatchLists dengan search null
            {
                watchListController.authContext.setAuthUser(authUser);

                List<WatchList> dummyResponse = List.of(watchList);
                when(watchListService.getAllWatchLists(any(UUID.class), any(String.class)))
                    .thenReturn(dummyResponse);
                
                var result = watchListController.getAllWatchLists(null);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }

            // Menguji getAllWatchLists dengan search parameter
            {
                watchListController.authContext.setAuthUser(authUser);

                List<WatchList> dummyResponse = List.of(watchList);
                when(watchListService.getAllWatchLists(any(UUID.class), any(String.class)))
                    .thenReturn(dummyResponse);
                
                var result = watchListController.getAllWatchLists("shawshank");
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // Menguji method getWatchListById
        {
            // Tidak terautentikasi untuk getWatchListById
            {
                watchListController.authContext.setAuthUser(null);

                var result = watchListController.getWatchListById(watchListId);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            watchListController.authContext.setAuthUser(authUser);

            // Menguji getWatchListById dengan ID yang ada
            {
                when(watchListService.getWatchListById(any(UUID.class), any(UUID.class)))
                    .thenReturn(watchList);
                
                var result = watchListController.getWatchListById(watchListId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
                assert (result.getBody().getData().get("watch_list").getId().equals(watchListId));
            }

            // Menguji getWatchListById dengan ID yang tidak ada
            {
                when(watchListService.getWatchListById(any(UUID.class), any(UUID.class)))
                    .thenReturn(null);
                
                var result = watchListController.getWatchListById(nonexistentWatchListId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("fail"));
            }
        }

        // Menguji method getWatchListGenres
        {
            // Tidak terautentikasi untuk getWatchListGenres
            {
                watchListController.authContext.setAuthUser(null);

                var result = watchListController.getWatchListGenres();
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Berhasil mendapatkan genres
            {
                watchListController.authContext.setAuthUser(authUser);

                List<String> dummyGenres = List.of("Drama", "Action", "Sci-Fi");
                when(watchListService.getAllGenres(any(UUID.class)))
                    .thenReturn(dummyGenres);
                
                var result = watchListController.getWatchListGenres();
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
                assert (result.getBody().getData().get("genres").size() == 3);
            }
        }

        // Menguji method updateWatchList
        {
            // Data tidak valid
            {
                List<WatchList> invalidWatchLists = List.of(
                    // Title Null
                    new WatchList(userId, null, "Movie", "Drama", 10, 1994, false, "Notes"),
                    // Title Kosong
                    new WatchList(userId, "", "Movie", "Drama", 10, 1994, false, "Notes"),
                    // Type Null
                    new WatchList(userId, "Title", null, "Drama", 10, 1994, false, "Notes"),
                    // Type Kosong
                    new WatchList(userId, "Title", "", "Drama", 10, 1994, false, "Notes"),
                    // Type Invalid
                    new WatchList(userId, "Title", "InvalidType", "Drama", 10, 1994, false, "Notes"),
                    // Genre Null
                    new WatchList(userId, "Title", "Movie", null, 10, 1994, false, "Notes"),
                    // Genre Kosong
                    new WatchList(userId, "Title", "Movie", "", 10, 1994, false, "Notes"),
                    // Rating Null
                    new WatchList(userId, "Title", "Movie", "Drama", null, 1994, false, "Notes"),
                    // Rating < 1
                    new WatchList(userId, "Title", "Movie", "Drama", 0, 1994, false, "Notes"),
                    // Rating > 10
                    new WatchList(userId, "Title", "Movie", "Drama", 11, 1994, false, "Notes"),
                    // ReleaseYear Null
                    new WatchList(userId, "Title", "Movie", "Drama", 10, null, false, "Notes"),
                    // ReleaseYear invalid
                    new WatchList(userId, "Title", "Movie", "Drama", 10, 1899, false, "Notes")
                );

                for (WatchList itemWatchList : invalidWatchLists) {
                    var result = watchListController.updateWatchList(watchListId, itemWatchList);
                    assert (result != null);
                    assert (result.getStatusCode().is4xxClientError());
                    assert (result.getBody().getStatus().equals("fail"));
                }
            }

            // Tidak terautentikasi untuk updateWatchList
            {
                watchListController.authContext.setAuthUser(null);

                var result = watchListController.updateWatchList(watchListId, watchList);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            watchListController.authContext.setAuthUser(authUser);

            // Memperbarui watchlist dengan ID tidak ada
            {
                when(watchListService.updateWatchList(
                    any(UUID.class), 
                    any(UUID.class), 
                    any(String.class), 
                    any(String.class), 
                    any(String.class), 
                    any(Integer.class), 
                    any(Integer.class), 
                    any(Boolean.class), 
                    any(String.class)
                )).thenReturn(null);
                
                WatchList updatedWatchList = new WatchList(
                    userId, 
                    "The Dark Knight", 
                    "Movie", 
                    "Action", 
                    9, 
                    2008, 
                    false, 
                    "Batman film updated"
                );
                updatedWatchList.setId(nonexistentWatchListId);

                var result = watchListController.updateWatchList(nonexistentWatchListId, updatedWatchList);
                assert (result != null);
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Memperbarui watchlist dengan ID ada
            {
                WatchList updatedWatchList = new WatchList(
                    userId, 
                    "The Shawshank Redemption", 
                    "Movie", 
                    "Drama", 
                    10, 
                    1994, 
                    true, 
                    "Already watched - amazing!"
                );
                updatedWatchList.setId(watchListId);
                
                when(watchListService.updateWatchList(
                    any(UUID.class), 
                    any(UUID.class), 
                    any(String.class), 
                    any(String.class), 
                    any(String.class), 
                    any(Integer.class), 
                    any(Integer.class), 
                    any(Boolean.class), 
                    any(String.class)
                )).thenReturn(updatedWatchList);

                var result = watchListController.updateWatchList(watchListId, updatedWatchList);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // Menguji method deleteWatchList
        {
            // Tidak terautentikasi untuk deleteWatchList
            {
                watchListController.authContext.setAuthUser(null);

                var result = watchListController.deleteWatchList(watchListId);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            watchListController.authContext.setAuthUser(authUser);

            // Menguji deleteWatchList dengan ID yang tidak ada
            {
                when(watchListService.deleteWatchList(any(UUID.class), any(UUID.class)))
                    .thenReturn(false);
                
                var result = watchListController.deleteWatchList(nonexistentWatchListId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Menguji deleteWatchList dengan ID yang ada
            {
                when(watchListService.deleteWatchList(any(UUID.class), any(UUID.class)))
                    .thenReturn(true);
                
                var result = watchListController.deleteWatchList(watchListId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // Menguji method toggleWatchedStatus
        {
            // Tidak terautentikasi untuk toggleWatchedStatus
            {
                watchListController.authContext.setAuthUser(null);

                var result = watchListController.toggleWatchedStatus(watchListId);
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            watchListController.authContext.setAuthUser(authUser);

            // Toggle status dengan ID tidak ada
            {
                when(watchListService.toggleWatchedStatus(any(UUID.class), any(UUID.class)))
                    .thenReturn(null);
                
                var result = watchListController.toggleWatchedStatus(nonexistentWatchListId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Toggle status dengan ID ada
            {
                WatchList toggledWatchList = new WatchList(
                    userId, 
                    "The Shawshank Redemption", 
                    "Movie", 
                    "Drama", 
                    10, 
                    1994, 
                    false, // Status berubah dari true ke false
                    "Classic film"
                );
                toggledWatchList.setId(watchListId);
                
                when(watchListService.toggleWatchedStatus(any(UUID.class), any(UUID.class)))
                    .thenReturn(toggledWatchList);
                
                var result = watchListController.toggleWatchedStatus(watchListId);
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
            }
        }

        // Menguji method getStatistics
        {
            // Tidak terautentikasi untuk getStatistics
            {
                watchListController.authContext.setAuthUser(null);

                var result = watchListController.getStatistics();
                assert (result != null);
                assert (result.getStatusCode().is4xxClientError());
                assert (result.getBody().getStatus().equals("fail"));
            }

            // Berhasil mendapatkan statistik
            {
                watchListController.authContext.setAuthUser(authUser);

                List<Object[]> genreStats = List.of(
                    new Object[]{"Drama", 5L},
                    new Object[]{"Action", 3L}
                );
                List<Object[]> watchedStats = List.of(
                    new Object[]{true, 4L},
                    new Object[]{false, 4L}
                );
                List<Object[]> typeStats = List.of(
                    new Object[]{"Movie", 5L},
                    new Object[]{"Series", 3L}
                );

                when(watchListService.getGenreStatistics(any(UUID.class)))
                    .thenReturn(genreStats);
                when(watchListService.getWatchedStatistics(any(UUID.class)))
                    .thenReturn(watchedStats);
                when(watchListService.getTypeStatistics(any(UUID.class)))
                    .thenReturn(typeStats);
                
                var result = watchListController.getStatistics();
                assert (result != null);
                assert (result.getBody().getStatus().equals("success"));
                assert (result.getBody().getData().containsKey("genreStatistics"));
                assert (result.getBody().getData().containsKey("watchedStatistics"));
                assert (result.getBody().getData().containsKey("typeStatistics"));
            }
        }
    }
}