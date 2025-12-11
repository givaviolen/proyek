package org.delcom.app.views;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.delcom.app.dto.WatchListForm;
import org.delcom.app.entities.WatchList;
import org.delcom.app.entities.User;
import org.delcom.app.services.WatchListService;
import org.delcom.app.utils.ConstUtil;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/watchlists")
public class WatchListHomeView {

    private final WatchListService watchListService;

    public WatchListHomeView(WatchListService watchListService) {
        this.watchListService = watchListService;
    }

    @GetMapping
    public String index(Model model, 
                        @RequestParam(required = false) String search,
                        @RequestParam(required = false) String type) { // <--- TAMBAHAN PARAMETER TYPE
        
        // 1. Auth Check
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/login";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/login";
        }
        User authUser = (User) principal;
        model.addAttribute("auth", authUser);

        // 2. Logika Pengambilan Data (Filter vs Search vs All)
        List<WatchList> watchLists;

        if (search != null && !search.trim().isEmpty()) {
            // Jika ada search, prioritaskan search
            watchLists = watchListService.getAllWatchLists(authUser.getId(), search.trim());
        } else if (type != null && !type.trim().isEmpty()) {
            // Jika ada filter type (Movie/Series), panggil service getByType
            watchLists = watchListService.getWatchListsByType(authUser.getId(), type);
        } else {
            // Default: Ambil semua
            watchLists = watchListService.getAllWatchLists(authUser.getId(), "");
        }

        model.addAttribute("watchLists", watchLists);

        // --- STATISTIK SUMMARY & CHART (Logic Tetap Sama) ---
        // Kita hitung ulang berdasarkan data total user (bukan data yang difilter di tabel)
        // Agar chart tetap menunjukkan statistik keseluruhan user meskipun tabel difilter
        
        // Ambil semua data raw untuk statistik
        List<WatchList> allDataForStats = watchListService.getAllWatchLists(authUser.getId(), "");

        long totalMovies = allDataForStats.stream().filter(w -> "Movie".equals(w.getType())).count();
        long totalSeries = allDataForStats.stream().filter(w -> "Series".equals(w.getType())).count();
        long totalItems = allDataForStats.size();
        
        long countWatched = watchListService.countByStatus(authUser.getId(), "Watched");
        long countWatching = watchListService.countByStatus(authUser.getId(), "Watching");
        long countPlan = watchListService.countByStatus(authUser.getId(), "Plan to Watch");
        long totalUnwatched = countPlan + countWatching; 

        double averageRating = allDataForStats.stream().mapToInt(WatchList::getRating).average().orElse(0.0);

        model.addAttribute("totalMovies", totalMovies);
        model.addAttribute("totalSeries", totalSeries);
        model.addAttribute("totalItems", totalItems);
        model.addAttribute("averageRating", String.format("%.1f", averageRating));
        model.addAttribute("totalWatched", countWatched);
        model.addAttribute("totalUnwatched", totalUnwatched);

        // Data Chart
        model.addAttribute("watchedCount", countWatched);
        model.addAttribute("watchingCount", countWatching);
        model.addAttribute("planToWatchCount", countPlan);
        model.addAttribute("unwatchedCount", countPlan); 

        // Genre Stats
        Map<String, Long> genreStats = allDataForStats.stream()
                .collect(Collectors.groupingBy(WatchList::getGenre, Collectors.counting()));
        model.addAttribute("genreStats", genreStats);
        model.addAttribute("movieCount", totalMovies);
        model.addAttribute("seriesCount", totalSeries);

        // Kirim kembali parameter type agar dropdown tetap terpilih
        model.addAttribute("currentType", type);

        model.addAttribute("watchListForm", new WatchListForm());

        return ConstUtil.TEMPLATE_PAGES_WATCHLISTS_HOME;
    }
}