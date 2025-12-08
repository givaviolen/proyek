package org.delcom.app.controllers;

import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.User;
import org.delcom.app.entities.WatchList;
import org.delcom.app.services.WatchListService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/watchlists")
public class WatchListController {

    private final WatchListService watchListService;

    @Autowired
    protected AuthContext authContext;

    public WatchListController(WatchListService watchListService) {
        this.watchListService = watchListService;
    }

    // Menambahkan watchlist baru
    // -------------------------------
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, UUID>>> createWatchList(@RequestBody WatchList reqWatchList) {

        // Validasi input
        if (reqWatchList.getTitle() == null || reqWatchList.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data title tidak valid", null));
        } else if (reqWatchList.getType() == null || reqWatchList.getType().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data type tidak valid", null));
        } else if (!reqWatchList.getType().equals("Movie") && !reqWatchList.getType().equals("Series")) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Type harus Movie atau Series", null));
        } else if (reqWatchList.getGenre() == null || reqWatchList.getGenre().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data genre tidak valid", null));
        } else if (reqWatchList.getRating() == null || reqWatchList.getRating() < 1 || reqWatchList.getRating() > 10) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Rating harus antara 1-10", null));
        } else if (reqWatchList.getReleaseYear() == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data tahun rilis tidak valid", null));
        } else if (reqWatchList.getReleaseYear() < 1900) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Tahun rilis tidak valid", null));
        }

        // Notes boleh null/kosong

        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        WatchList newWatchList = watchListService.createWatchList(
            authUser.getId(), 
            reqWatchList.getTitle(), 
            reqWatchList.getType(), 
            reqWatchList.getGenre(), 
            reqWatchList.getRating(), 
            reqWatchList.getReleaseYear(), 
            reqWatchList.getIsWatched() != null ? reqWatchList.getIsWatched() : false,
            reqWatchList.getNotes()
        );

        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Berhasil menambahkan data",
                Map.of("id", newWatchList.getId())));
    }

    // Mendapatkan semua watchlist dengan opsi pencarian
    // -------------------------------
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<WatchList>>>> getAllWatchLists(
            @RequestParam(required = false) String search) {
        
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<WatchList> watchLists = watchListService.getAllWatchLists(authUser.getId(), search != null ? search : "");
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Berhasil mengambil data",
                Map.of("watch_lists", watchLists)));
    }

    // Mendapatkan watchlist berdasarkan ID
    // -------------------------------
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, WatchList>>> getWatchListById(@PathVariable UUID id) {
        
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        WatchList watchList = watchListService.getWatchListById(authUser.getId(), id);
        if (watchList == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data watchlist tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Berhasil mengambil data",
                Map.of("watch_list", watchList)));
    }

    // Mendapatkan semua genre
    // -------------------------------
    @GetMapping("/genres")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> getWatchListGenres() {
        
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<String> genres = watchListService.getAllGenres(authUser.getId());
        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Berhasil mengambil data",
                Map.of("genres", genres)));
    }

    // Memperbarui watchlist berdasarkan ID
    // -------------------------------
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WatchList>> updateWatchList(@PathVariable UUID id, @RequestBody WatchList reqWatchList) {

        // Validasi input
        if (reqWatchList.getTitle() == null || reqWatchList.getTitle().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data title tidak valid", null));
        } else if (reqWatchList.getType() == null || reqWatchList.getType().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data type tidak valid", null));
        } else if (!reqWatchList.getType().equals("Movie") && !reqWatchList.getType().equals("Series")) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Type harus Movie atau Series", null));
        } else if (reqWatchList.getGenre() == null || reqWatchList.getGenre().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data genre tidak valid", null));
        } else if (reqWatchList.getRating() == null || reqWatchList.getRating() < 1 || reqWatchList.getRating() > 10) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Rating harus antara 1-10", null));
        } else if (reqWatchList.getReleaseYear() == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Data tahun rilis tidak valid", null));
        } else if (reqWatchList.getReleaseYear() < 1900) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Tahun rilis tidak valid", null));
        }

        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        WatchList updatedWatchList = watchListService.updateWatchList(
            authUser.getId(), 
            id, 
            reqWatchList.getTitle(), 
            reqWatchList.getType(), 
            reqWatchList.getGenre(), 
            reqWatchList.getRating(), 
            reqWatchList.getReleaseYear(), 
            reqWatchList.getIsWatched() != null ? reqWatchList.getIsWatched() : false,
            reqWatchList.getNotes()
        );

        if (updatedWatchList == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data watchlist tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>("success", "Berhasil memperbarui data", null));
    }

    // Menghapus watchlist berdasarkan ID
    // -------------------------------
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteWatchList(@PathVariable UUID id) {
        
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        boolean status = watchListService.deleteWatchList(authUser.getId(), id);
        if (!status) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data watchlist tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Data watchlist berhasil dihapus",
                null));
    }

    // Toggle status watched/unwatched
    // -------------------------------
    @PatchMapping("/{id}/toggle-watched")
    public ResponseEntity<ApiResponse<WatchList>> toggleWatchedStatus(@PathVariable UUID id) {
        
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        WatchList updated = watchListService.toggleWatchedStatus(authUser.getId(), id);
        if (updated == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data watchlist tidak ditemukan", null));
        }

        return ResponseEntity.ok(new ApiResponse<>("success", "Status berhasil diubah", null));
    }

    // Mendapatkan statistik untuk chart
    // -------------------------------
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics() {
        
        // Validasi autentikasi
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        List<Object[]> genreStats = watchListService.getGenreStatistics(authUser.getId());
        List<Object[]> watchedStats = watchListService.getWatchedStatistics(authUser.getId());
        List<Object[]> typeStats = watchListService.getTypeStatistics(authUser.getId());

        Map<String, Object> statistics = Map.of(
            "genreStatistics", genreStats,
            "watchedStatistics", watchedStats,
            "typeStatistics", typeStats
        );

        return ResponseEntity.ok(new ApiResponse<>(
                "success",
                "Berhasil mengambil data statistik",
                statistics));
    }
}