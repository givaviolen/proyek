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
    public String index(Model model, @RequestParam(required = false) String search) {
        // Autentikasi
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

        // Data watchlists
        List<WatchList> watchLists = watchListService.getAllWatchLists(authUser.getId(), search != null ? search : "");
        model.addAttribute("watchLists", watchLists);

        // Hitung statistik summary
        long totalMovies = watchLists.stream()
                .filter(w -> "Movie".equals(w.getType()))
                .count();
        
        long totalSeries = watchLists.stream()
                .filter(w -> "Series".equals(w.getType()))
                .count();

        long totalWatched = watchLists.stream()
                .filter(WatchList::getIsWatched)
                .count();

        long totalUnwatched = watchLists.stream()
                .filter(w -> !w.getIsWatched())
                .count();

        // Hitung rata-rata rating
        double averageRating = watchLists.stream()
                .mapToInt(WatchList::getRating)
                .average()
                .orElse(0.0);

        model.addAttribute("totalMovies", totalMovies);
        model.addAttribute("totalSeries", totalSeries);
        model.addAttribute("totalWatched", totalWatched);
        model.addAttribute("totalUnwatched", totalUnwatched);
        model.addAttribute("totalItems", watchLists.size());
        model.addAttribute("averageRating", String.format("%.1f", averageRating));

        // Data untuk chart - Genre distribution
        Map<String, Long> genreStats = watchLists.stream()
                .collect(Collectors.groupingBy(WatchList::getGenre, Collectors.counting()));
        model.addAttribute("genreStats", genreStats);

        // Data untuk chart - Type distribution
        model.addAttribute("movieCount", totalMovies);
        model.addAttribute("seriesCount", totalSeries);

        // Data untuk chart - Watched status
        model.addAttribute("watchedCount", totalWatched);
        model.addAttribute("unwatchedCount", totalUnwatched);

        // Form untuk modal
        model.addAttribute("watchListForm", new WatchListForm());

        return ConstUtil.TEMPLATE_PAGES_WATCHLISTS_HOME;
    }
}