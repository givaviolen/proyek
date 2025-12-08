package org.delcom.app.views;

import java.io.IOException;
import java.nio.file.Path;
import java.util.UUID;

import org.delcom.app.dto.WatchListForm;
import org.delcom.app.dto.CoverWatchListForm;
import org.delcom.app.entities.WatchList;
import org.delcom.app.entities.User;
import org.delcom.app.services.WatchListService;
import org.delcom.app.services.FileStorageService;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.ResponseBody;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequestMapping("/watchlists")
public class WatchListView {

    private final WatchListService watchListService;
    private final FileStorageService fileStorageService;

    // Constructor lengkap
    public WatchListView(WatchListService watchListService, FileStorageService fileStorageService) {
        this.watchListService = watchListService;
        this.fileStorageService = fileStorageService;
    }

    // ===========================
    // ADD WATCHLIST
    // ===========================
    @PostMapping("/add")
    public String postAddWatchList(@Valid @ModelAttribute("watchListForm") WatchListForm watchListForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }
        User authUser = (User) principal;

        // Validasi
        if (watchListForm.getTitle() == null || watchListForm.getTitle().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Judul tidak boleh kosong");
            redirectAttributes.addFlashAttribute("addWatchListModalOpen", true);
            return "redirect:/watchlists";
        }

        if (watchListForm.getType() == null || watchListForm.getType().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Tipe tidak boleh kosong");
            redirectAttributes.addFlashAttribute("addWatchListModalOpen", true);
            return "redirect:/watchlists";
        }

        if (!watchListForm.getType().equals("Movie") && !watchListForm.getType().equals("Series")) {
            redirectAttributes.addFlashAttribute("error", "Tipe harus Movie atau Series");
            redirectAttributes.addFlashAttribute("addWatchListModalOpen", true);
            return "redirect:/watchlists";
        }

        if (watchListForm.getGenre() == null || watchListForm.getGenre().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Genre tidak boleh kosong");
            redirectAttributes.addFlashAttribute("addWatchListModalOpen", true);
            return "redirect:/watchlists";
        }

        if (watchListForm.getRating() == null || watchListForm.getRating() < 1 || watchListForm.getRating() > 10) {
            redirectAttributes.addFlashAttribute("error", "Rating harus antara 1-10");
            redirectAttributes.addFlashAttribute("addWatchListModalOpen", true);
            return "redirect:/watchlists";
        }

        if (watchListForm.getReleaseYear() == null || watchListForm.getReleaseYear() < 1900) {
            redirectAttributes.addFlashAttribute("error", "Tahun rilis tidak valid");
            redirectAttributes.addFlashAttribute("addWatchListModalOpen", true);
            return "redirect:/watchlists";
        }

        var entity = watchListService.createWatchList(
                authUser.getId(),
                watchListForm.getTitle(),
                watchListForm.getType(),
                watchListForm.getGenre(),
                watchListForm.getRating(),
                watchListForm.getReleaseYear(),
                watchListForm.getIsWatched() != null ? watchListForm.getIsWatched() : false,
                watchListForm.getNotes());

        if (entity == null) {
            redirectAttributes.addFlashAttribute("error", "Gagal menambahkan watchlist");
            redirectAttributes.addFlashAttribute("addWatchListModalOpen", true);
            return "redirect:/watchlists";
        }

        redirectAttributes.addFlashAttribute("success", "Watchlist berhasil ditambahkan.");
        return "redirect:/watchlists";
    }

    // ===========================
    // EDIT WATCHLIST
    // ===========================
    @PostMapping("/edit")
    public String postEditWatchList(@Valid @ModelAttribute("watchListForm") WatchListForm watchListForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }

        User authUser = (User) principal;

        if (watchListForm.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "ID watchlist tidak valid");
            redirectAttributes.addFlashAttribute("editWatchListModalOpen", true);
            return "redirect:/watchlists";
        }

        var updated = watchListService.updateWatchList(
                authUser.getId(),
                watchListForm.getId(),
                watchListForm.getTitle(),
                watchListForm.getType(),
                watchListForm.getGenre(),
                watchListForm.getRating(),
                watchListForm.getReleaseYear(),
                watchListForm.getIsWatched() != null ? watchListForm.getIsWatched() : false,
                watchListForm.getNotes());

        if (updated == null) {
            redirectAttributes.addFlashAttribute("error", "Gagal memperbarui watchlist");
            redirectAttributes.addFlashAttribute("editWatchListModalOpen", true);
            return "redirect:/watchlists";
        }

        redirectAttributes.addFlashAttribute("success", "Watchlist berhasil diperbarui.");
        return "redirect:/watchlists";
    }

    // ===========================
    // DELETE WATCHLIST
    // ===========================
    @PostMapping("/delete")
    public String postDeleteWatchList(@Valid @ModelAttribute("watchListForm") WatchListForm watchListForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }

        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }

        User authUser = (User) principal;

        if (watchListForm.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "ID watchlist tidak valid");
            redirectAttributes.addFlashAttribute("deleteWatchListModalOpen", true);
            return "redirect:/watchlists";
        }

        WatchList existingWatchList = watchListService.getWatchListById(authUser.getId(), watchListForm.getId());
        if (existingWatchList == null) {
            redirectAttributes.addFlashAttribute("error", "Watchlist tidak ditemukan");
            redirectAttributes.addFlashAttribute("deleteWatchListModalOpen", true);
            return "redirect:/watchlists";
        }

        boolean deleted = watchListService.deleteWatchList(
                authUser.getId(),
                watchListForm.getId());

        if (!deleted) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus watchlist");
            redirectAttributes.addFlashAttribute("deleteWatchListModalOpen", true);
            return "redirect:/watchlists";
        }

        redirectAttributes.addFlashAttribute("success", "Watchlist berhasil dihapus.");
        return "redirect:/watchlists";
    }

    // ===========================
    // DETAIL WATCHLIST
    // ===========================
    @GetMapping("/{watchListId}")
    public String getDetailWatchList(@PathVariable UUID watchListId, Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }
        User authUser = (User) principal;

        model.addAttribute("auth", authUser);

        WatchList watchList = watchListService.getWatchListById(authUser.getId(), watchListId);
        if (watchList == null) {
            return "redirect:/watchlists";
        }

        model.addAttribute("watchList", watchList);

        CoverWatchListForm coverWatchListForm = new CoverWatchListForm();
        coverWatchListForm.setId(watchListId);
        model.addAttribute("coverWatchListForm", coverWatchListForm);

        return "pages/watchlists/detail";
    }

    // =====================================================
    // UPLOAD COVER/POSTER
    // =====================================================
    @PostMapping("/edit-cover")
    public String postEditCoverWatchList(
            @Valid @ModelAttribute("coverWatchListForm") CoverWatchListForm coverWatchListForm,
            RedirectAttributes redirectAttributes,
            HttpSession session,
            Model model) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }

        User authUser = (User) principal;

        if (coverWatchListForm.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "File poster tidak boleh kosong");
            redirectAttributes.addFlashAttribute("editCoverWatchListModalOpen", true);
            return "redirect:/watchlists/" + coverWatchListForm.getId();
        }

        WatchList watchList = watchListService.getWatchListById(authUser.getId(), coverWatchListForm.getId());
        if (watchList == null) {
            redirectAttributes.addFlashAttribute("error", "Watchlist tidak ditemukan");
            return "redirect:/watchlists";
        }

        if (!coverWatchListForm.isValidImage()) {
            redirectAttributes.addFlashAttribute("error", "Format file tidak didukung. Gunakan JPG, PNG, GIF, atau WEBP");
            redirectAttributes.addFlashAttribute("editCoverWatchListModalOpen", true);
            return "redirect:/watchlists/" + coverWatchListForm.getId();
        }

        if (!coverWatchListForm.isSizeValid(5 * 1024 * 1024)) {
            redirectAttributes.addFlashAttribute("error", "Ukuran file terlalu besar. Maksimal 5MB");
            redirectAttributes.addFlashAttribute("editCoverWatchListModalOpen", true);
            return "redirect:/watchlists/" + coverWatchListForm.getId();
        }

        try {
            String fileName = fileStorageService.storeFile(
                    coverWatchListForm.getCoverFile(),
                    coverWatchListForm.getId()
            );

            watchListService.updateCover(coverWatchListForm.getId(), fileName);

            redirectAttributes.addFlashAttribute("success", "Poster berhasil diupload");
            return "redirect:/watchlists/" + coverWatchListForm.getId();
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Gagal mengupload poster");
            redirectAttributes.addFlashAttribute("editCoverWatchListModalOpen", true);
            return "redirect:/watchlists/" + coverWatchListForm.getId();
        }
    }

    // =====================================================
    // SERVE COVER FILE
    // =====================================================
    @GetMapping("/cover/{filename:.+}")
    @ResponseBody
    public Resource getCoverByFilename(@PathVariable String filename) {
        try {
            Path file = fileStorageService.loadFile(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
        } catch (Exception ignored) {}

        return null;
    }

    // =====================================================
    // TOGGLE WATCHED STATUS (BONUS FEATURE)
    // =====================================================
    @PostMapping("/toggle-watched/{watchListId}")
    public String toggleWatchedStatus(@PathVariable UUID watchListId,
            RedirectAttributes redirectAttributes) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if ((authentication instanceof AnonymousAuthenticationToken)) {
            return "redirect:/auth/logout";
        }
        Object principal = authentication.getPrincipal();
        if (!(principal instanceof User)) {
            return "redirect:/auth/logout";
        }
        User authUser = (User) principal;

        WatchList updated = watchListService.toggleWatchedStatus(authUser.getId(), watchListId);
        if (updated == null) {
            redirectAttributes.addFlashAttribute("error", "Gagal mengubah status");
            return "redirect:/watchlists";
        }

        String status = updated.getIsWatched() ? "sudah ditonton" : "belum ditonton";
        redirectAttributes.addFlashAttribute("success", "Status diubah menjadi " + status);
        return "redirect:/watchlists/" + watchListId;
    }
}