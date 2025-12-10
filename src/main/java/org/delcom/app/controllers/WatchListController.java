
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

    // 1. CREATE
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, UUID>>> createWatchList(@RequestBody WatchList reqWatchList) {
        
        // --- VALIDASI INPUT (Agar Unit Test invalidWatchLists Pass) ---
        if (reqWatchList.getTitle() == null || reqWatchList.getTitle().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Title tidak boleh kosong", null));
        } 
        if (reqWatchList.getType() == null || (!reqWatchList.getType().equals("Movie") && !reqWatchList.getType().equals("Series"))) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Type harus Movie atau Series", null));
        }
        if (reqWatchList.getGenre() == null || reqWatchList.getGenre().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Genre tidak boleh kosong", null));
        }
        if (reqWatchList.getRating() == null || reqWatchList.getRating() < 1 || reqWatchList.getRating() > 10) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Rating harus antara 1 sampai 10", null));
        }
        if (reqWatchList.getReleaseYear() == null || reqWatchList.getReleaseYear() < 1900) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Tahun rilis tidak valid", null));
        }

        // Default Status jika null
        String status = reqWatchList.getStatus();
        if (status == null || status.isEmpty()) {
            status = "Plan to Watch"; 
        }

        // Cek Auth
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
            status, 
            reqWatchList.getNotes()
        );

        return ResponseEntity.ok(new ApiResponse<>("success", "Berhasil menambahkan data", Map.of("id", newWatchList.getId())));
    }

    // 2. GET ALL
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, List<WatchList>>>> getAllWatchLists(@RequestParam(required = false) String search) {
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();
        List<WatchList> watchLists = watchListService.getAllWatchLists(authUser.getId(), search != null ? search : "");
        return ResponseEntity.ok(new ApiResponse<>("success", "Berhasil mengambil data", Map.of("watch_lists", watchLists)));
    }

    // 3. GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Map<String, WatchList>>> getWatchListById(@PathVariable UUID id) {
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();
        WatchList watchList = watchListService.getWatchListById(authUser.getId(), id);
        
        if (watchList == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data watchlist tidak ditemukan", null));
        }
        return ResponseEntity.ok(new ApiResponse<>("success", "Berhasil mengambil data", Map.of("watch_list", watchList)));
    }

    // 4. GET GENRES
    @GetMapping("/genres")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> getWatchListGenres() {
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();
        List<String> genres = watchListService.getAllGenres(authUser.getId());
        return ResponseEntity.ok(new ApiResponse<>("success", "Berhasil mengambil data", Map.of("genres", genres)));
    }

    // 5. UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<WatchList>> updateWatchList(@PathVariable UUID id, @RequestBody WatchList reqWatchList) {
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        
        // Validasi Update (Harus sama ketatnya dengan Create agar data konsisten)
        if (reqWatchList.getTitle() == null || reqWatchList.getTitle().isEmpty()) return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Title invalid", null));
        if (reqWatchList.getType() == null) return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Type invalid", null));
        if (reqWatchList.getGenre() == null) return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Genre invalid", null));
        if (reqWatchList.getRating() == null || reqWatchList.getRating() < 1 || reqWatchList.getRating() > 10) return ResponseEntity.badRequest().body(new ApiResponse<>("fail", "Rating invalid", null));

        User authUser = authContext.getAuthUser();
        
        String status = reqWatchList.getStatus();
        if (status == null) status = "Plan to Watch";

        WatchList updatedWatchList = watchListService.updateWatchList(
            authUser.getId(), id, 
            reqWatchList.getTitle(), 
            reqWatchList.getType(), 
            reqWatchList.getGenre(), 
            reqWatchList.getRating(), 
            reqWatchList.getReleaseYear(), 
            status, 
            reqWatchList.getNotes()
        );

        if (updatedWatchList == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data watchlist tidak ditemukan", null));
        }
        return ResponseEntity.ok(new ApiResponse<>("success", "Berhasil memperbarui data", null));
    }

    // 6. DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteWatchList(@PathVariable UUID id) {
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();
        boolean status = watchListService.deleteWatchList(authUser.getId(), id);
        if (!status) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data watchlist tidak ditemukan", null));
        }
        return ResponseEntity.ok(new ApiResponse<>("success", "Data watchlist berhasil dihapus", null));
    }

    // 7. TOGGLE STATUS (Menggunakan Cycle Status)
    @PatchMapping("/{id}/toggle-watched")
    public ResponseEntity<ApiResponse<WatchList>> toggleWatchedStatus(@PathVariable UUID id) {
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();

        // Menggunakan method cycleStatus untuk mendukung 3 status (Plan -> Watching -> Watched)
        WatchList updated = watchListService.cycleStatus(authUser.getId(), id);
        
        if (updated == null) {
            return ResponseEntity.status(404).body(new ApiResponse<>("fail", "Data watchlist tidak ditemukan", null));
        }
        // Mengembalikan object watchlist yang sudah terupdate statusnya
        return ResponseEntity.ok(new ApiResponse<>("success", "Status berhasil diubah", updated)); 
    }

    // 8. STATISTICS
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStatistics() {
        if (!authContext.isAuthenticated()) {
            return ResponseEntity.status(403).body(new ApiResponse<>("fail", "User tidak terautentikasi", null));
        }
        User authUser = authContext.getAuthUser();
        
        List<Object[]> genreStats = watchListService.getGenreStatistics(authUser.getId());
        List<Object[]> statusStats = watchListService.getStatusStatistics(authUser.getId());
        List<Object[]> typeStats = watchListService.getTypeStatistics(authUser.getId());
        
        Map<String, Object> statistics = Map.of(
            "genreStatistics", genreStats, 
            "statusStatistics", statusStats, 
            "typeStatistics", typeStats
        );
        return ResponseEntity.ok(new ApiResponse<>("success", "Berhasil mengambil data statistik", statistics));
    }
}