package org.delcom.app.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.WatchList;
import org.delcom.app.entities.User;
import org.delcom.app.services.WatchListService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

public class WatchListControllerTests {
    
    @Test
    @DisplayName("Pengujian untuk controller WatchList - Full Coverage 100%")
    void testWatchListController() throws Exception {
        UUID userId = UUID.randomUUID();
        UUID watchListId = UUID.randomUUID();
        UUID nonexistentWatchListId = UUID.randomUUID();

        // Object User Login
        User authUser = new User("Test User", "testuser@example.com");
        authUser.setId(userId);

        // Object WatchList Standar (MOVIE)
        WatchList watchList = new WatchList(
            userId, "The Shawshank Redemption", "Movie", "Drama", 10, 1994, "Watched", "Classic film"
        );
        watchList.setId(watchListId);

        // --- DEFINISI DATA INVALID ---
        List<WatchList> invalidWatchLists = List.of(
            new WatchList(userId, null, "Movie", "Drama", 10, 1994, "Plan to Watch", "Notes"),       // Title Null
            new WatchList(userId, "", "Movie", "Drama", 10, 1994, "Plan to Watch", "Notes"),         // Title Empty
            new WatchList(userId, "   ", "Movie", "Drama", 10, 1994, "Plan to Watch", "Notes"),      // Title Blank
            new WatchList(userId, "Title", null, "Drama", 10, 1994, "Plan to Watch", "Notes"),       // Type Null
            new WatchList(userId, "Title", "InvalidType", "Drama", 10, 1994, "Plan to Watch", "Notes"), // Type Invalid
            new WatchList(userId, "Title", "Movie", null, 10, 1994, "Plan to Watch", "Notes"),       // Genre Null
            new WatchList(userId, "Title", "Movie", "", 10, 1994, "Plan to Watch", "Notes"),         // Genre Empty
            new WatchList(userId, "Title", "Movie", "   ", 10, 1994, "Plan to Watch", "Notes"),      // Genre Blank (Trim check)
            new WatchList(userId, "Title", "Movie", "Drama", null, 1994, "Plan to Watch", "Notes"),  // Rating Null
            new WatchList(userId, "Title", "Movie", "Drama", 0, 1994, "Plan to Watch", "Notes"),     // Rating < 1
            new WatchList(userId, "Title", "Movie", "Drama", 11, 1994, "Plan to Watch", "Notes"),    // Rating > 10
            new WatchList(userId, "Title", "Movie", "Drama", 10, null, "Plan to Watch", "Notes"),    // Year Null
            new WatchList(userId, "Title", "Movie", "Drama", 10, 1899, "Plan to Watch", "Notes")     // Year Invalid
        );

        // Mock Service
        WatchListService watchListService = Mockito.mock(WatchListService.class);
        
        // Setup Controller & Context
        WatchListController watchListController = new WatchListController(watchListService);
        watchListController.authContext = new AuthContext();

        // -------------------------------------------------------------
        // 1. CREATE WATCHLIST
        // -------------------------------------------------------------
        
        // A. Create - Unauthenticated
        watchListController.authContext.setAuthUser(null);
        var resCreateUnauth = watchListController.createWatchList(watchList);
        assert (resCreateUnauth.getStatusCode().value() == 403);

        // B. Create - Invalid Inputs
        watchListController.authContext.setAuthUser(authUser);
        for (WatchList item : invalidWatchLists) {
            var res = watchListController.createWatchList(item);
            assert (res.getStatusCode().is4xxClientError()); 
        }

        // C. Create - Success (MOVIE)
        when(watchListService.createWatchList(any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(watchList);
        var resCreateSuccess = watchListController.createWatchList(watchList);
        assert (resCreateSuccess.getStatusCode().is2xxSuccessful());

        // D. Create - Success (SERIES) -> PENTING UNTUK COVERAGE LOGIC TYPE
        WatchList seriesList = new WatchList(userId, "Series Title", "Series", "Genre", 8, 2020, "Watching", "Note");
        seriesList.setId(UUID.randomUUID());
        when(watchListService.createWatchList(
            any(), any(), eq("Series"), any(), any(), any(), any(), any()
        )).thenReturn(seriesList);
        
        var resCreateSeries = watchListController.createWatchList(seriesList);
        assert (resCreateSeries.getStatusCode().is2xxSuccessful());

        // E. Create - Status NULL Handling
        // Jika input NULL, Controller mengubahnya jadi "Plan to Watch"
        WatchList nullStatus = new WatchList(userId, "M", "Movie", "G", 5, 2020, null, "N");
        nullStatus.setId(UUID.randomUUID());
        
        Mockito.reset(watchListService); // Reset mock
        when(watchListService.createWatchList(any(), any(), any(), any(), any(), any(), eq("Plan to Watch"), any()))
            .thenReturn(nullStatus);
        
        var resCreateNullStat = watchListController.createWatchList(nullStatus);
        assert (resCreateNullStat.getStatusCode().is2xxSuccessful());

        // F. Create - Status EMPTY Handling (INI YANG MEMBUAT MERAH JADI HIJAU)
        // Jika input "", Controller mengubahnya jadi "Plan to Watch"
        WatchList emptyStatus = new WatchList(userId, "M", "Movie", "G", 5, 2020, "", "N");
        emptyStatus.setId(UUID.randomUUID());

        Mockito.reset(watchListService); // Reset mock
        // Perhatikan eq("Plan to Watch"), karena controller sudah mengubah string kosong menjadi default
        when(watchListService.createWatchList(any(), any(), any(), any(), any(), any(), eq("Plan to Watch"), any()))
            .thenReturn(emptyStatus);

        var resCreateEmptyStat = watchListController.createWatchList(emptyStatus);
        assert (resCreateEmptyStat.getStatusCode().is2xxSuccessful());

        // -------------------------------------------------------------
        // 2. GET ALL
        // -------------------------------------------------------------

        // A. Get All - Unauthenticated
        watchListController.authContext.setAuthUser(null);
        var resGetAllUnauth = watchListController.getAllWatchLists(null);
        assert (resGetAllUnauth.getStatusCode().value() == 403);

        // B. Get All - Success (With Search)
        watchListController.authContext.setAuthUser(authUser);
        when(watchListService.getAllWatchLists(eq(userId), any())).thenReturn(List.of(watchList));
        
        var resGetAllSuccess = watchListController.getAllWatchLists("search");
        assert (resGetAllSuccess.getStatusCode().is2xxSuccessful());

        // C. Get All - Success (With Null Search) -> PENTING UNTUK COVERAGE TERNARY
        var resGetAllNullSearch = watchListController.getAllWatchLists(null);
        assert (resGetAllNullSearch.getStatusCode().is2xxSuccessful());

        // -------------------------------------------------------------
        // 3. GET BY ID
        // -------------------------------------------------------------

        // A. Get By ID - Unauthenticated
        watchListController.authContext.setAuthUser(null);
        var resGetIdUnauth = watchListController.getWatchListById(watchListId);
        assert (resGetIdUnauth.getStatusCode().value() == 403);

        // B. Get By ID - Not Found
        watchListController.authContext.setAuthUser(authUser);
        when(watchListService.getWatchListById(eq(userId), eq(nonexistentWatchListId))).thenReturn(null);
        var resGetIdNotFound = watchListController.getWatchListById(nonexistentWatchListId);
        assert (resGetIdNotFound.getStatusCode().value() == 404);

        // C. Get By ID - Success
        when(watchListService.getWatchListById(eq(userId), eq(watchListId))).thenReturn(watchList);
        var resGetIdSuccess = watchListController.getWatchListById(watchListId);
        assert (resGetIdSuccess.getStatusCode().is2xxSuccessful());

        // -------------------------------------------------------------
        // 4. GET GENRES
        // -------------------------------------------------------------

        watchListController.authContext.setAuthUser(null);
        var resGenresUnauth = watchListController.getWatchListGenres();
        assert (resGenresUnauth.getStatusCode().value() == 403);

        watchListController.authContext.setAuthUser(authUser);
        when(watchListService.getAllGenres(eq(userId))).thenReturn(List.of("Drama"));
        var resGenresSuccess = watchListController.getWatchListGenres();
        assert (resGenresSuccess.getStatusCode().is2xxSuccessful());

        // -------------------------------------------------------------
        // 5. UPDATE
        // -------------------------------------------------------------

        // A. Update - Unauthenticated
        watchListController.authContext.setAuthUser(null);
        var resUpdateUnauth = watchListController.updateWatchList(watchListId, watchList);
        assert (resUpdateUnauth.getStatusCode().value() == 403);

        // B. Update - Invalid Inputs
        watchListController.authContext.setAuthUser(authUser);
        for (WatchList item : invalidWatchLists) {
            var res = watchListController.updateWatchList(watchListId, item);
            assert (res.getStatusCode().is4xxClientError()); 
        }

        // C. Update - Not Found
        when(watchListService.updateWatchList(any(), eq(nonexistentWatchListId), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(null);
        var resUpdateNotFound = watchListController.updateWatchList(nonexistentWatchListId, watchList);
        assert (resUpdateNotFound.getStatusCode().value() == 404);

        // D. Update - Success
        WatchList updateData = new WatchList(userId, "Upd", "Movie", "G", 8, 2021, null, "N");
        when(watchListService.updateWatchList(any(), eq(watchListId), any(), any(), any(), any(), any(), eq("Plan to Watch"), any()))
            .thenReturn(watchList);
        
        var resUpdateSuccess = watchListController.updateWatchList(watchListId, updateData);
        assert (resUpdateSuccess.getStatusCode().is2xxSuccessful());

        // -------------------------------------------------------------
        // 6. DELETE
        // -------------------------------------------------------------

        watchListController.authContext.setAuthUser(null);
        var resDeleteUnauth = watchListController.deleteWatchList(watchListId);
        assert (resDeleteUnauth.getStatusCode().value() == 403);

        watchListController.authContext.setAuthUser(authUser);
        when(watchListService.deleteWatchList(eq(userId), eq(nonexistentWatchListId))).thenReturn(false);
        var resDeleteNotFound = watchListController.deleteWatchList(nonexistentWatchListId);
        assert (resDeleteNotFound.getStatusCode().value() == 404);

        when(watchListService.deleteWatchList(eq(userId), eq(watchListId))).thenReturn(true);
        var resDeleteSuccess = watchListController.deleteWatchList(watchListId);
        assert (resDeleteSuccess.getStatusCode().is2xxSuccessful());

        // -------------------------------------------------------------
        // 7. TOGGLE STATUS
        // -------------------------------------------------------------

        watchListController.authContext.setAuthUser(null);
        var resToggleUnauth = watchListController.toggleWatchedStatus(watchListId);
        assert (resToggleUnauth.getStatusCode().value() == 403);

        watchListController.authContext.setAuthUser(authUser);
        when(watchListService.cycleStatus(eq(userId), eq(nonexistentWatchListId))).thenReturn(null);
        var resToggleNotFound = watchListController.toggleWatchedStatus(nonexistentWatchListId);
        assert (resToggleNotFound.getStatusCode().value() == 404);

        when(watchListService.cycleStatus(eq(userId), eq(watchListId))).thenReturn(watchList);
        var resToggleSuccess = watchListController.toggleWatchedStatus(watchListId);
        assert (resToggleSuccess.getStatusCode().is2xxSuccessful());

        // -------------------------------------------------------------
        // 8. STATISTICS
        // -------------------------------------------------------------

        watchListController.authContext.setAuthUser(null);
        var resStatsUnauth = watchListController.getStatistics();
        assert (resStatsUnauth.getStatusCode().value() == 403);

        watchListController.authContext.setAuthUser(authUser);
        when(watchListService.getGenreStatistics(eq(userId))).thenReturn(List.of());
        when(watchListService.getStatusStatistics(eq(userId))).thenReturn(List.of());
        when(watchListService.getTypeStatistics(eq(userId))).thenReturn(List.of());

        var resStatsSuccess = watchListController.getStatistics();
        assert (resStatsSuccess.getStatusCode().is2xxSuccessful());
    }
}