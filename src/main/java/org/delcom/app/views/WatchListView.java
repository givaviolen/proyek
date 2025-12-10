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
import org.springframework.validation.BindingResult; // <--- WAJIB DI-IMPORT
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/watchlists")
public class WatchListView {

    private final WatchListService watchListService;
    private final FileStorageService fileStorageService;

    public WatchListView(WatchListService watchListService, FileStorageService fileStorageService) {
        this.watchListService = watchListService;
        this.fileStorageService = fileStorageService;
    }

    // ===========================
    // ADD WATCHLIST
    // ===========================
    @PostMapping("/add")
    public String postAddWatchList(
            @Valid @ModelAttribute("watchListForm") WatchListForm watchListForm,
            BindingResult bindingResult, // <--- TAMBAHAN: Menangkap error validasi
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            RedirectAttributes redirectAttributes) {

        User authUser = getAuthUser();
        if (authUser == null) return "redirect:/auth/logout";

        // 1. Cek Error Validasi (Agar tidak White Label Error Page / 400 Bad Request)
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError() != null 
                    ? bindingResult.getFieldError().getDefaultMessage() 
                    : "Input tidak valid";
            redirectAttributes.addFlashAttribute("error", "Gagal: " + errorMessage);
            return "redirect:/watchlists";
        }

        if (watchListForm.getTitle() == null || watchListForm.getTitle().isBlank()) {
            redirectAttributes.addFlashAttribute("error", "Judul tidak boleh kosong");
            return "redirect:/watchlists";
        }

        String statusInput = "Plan to Watch";
        if (watchListForm.getStatus() != null && !watchListForm.getStatus().isEmpty()) {
            statusInput = watchListForm.getStatus();
        }

        var entity = watchListService.createWatchList(
                authUser.getId(),
                watchListForm.getTitle(),
                watchListForm.getType(),
                watchListForm.getGenre(),
                watchListForm.getRating(),
                watchListForm.getReleaseYear(),
                statusInput, 
                watchListForm.getNotes());

        if (entity == null) {
            redirectAttributes.addFlashAttribute("error", "Gagal menambahkan watchlist");
            return "redirect:/watchlists";
        }

        if (coverImage != null && !coverImage.isEmpty()) {
            try {
                String fileName = fileStorageService.storeFile(coverImage, entity.getId());
                watchListService.updateCover(entity.getId(), fileName);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Data tersimpan, tapi gagal upload poster: " + e.getMessage());
                return "redirect:/watchlists";
            }
        }

        redirectAttributes.addFlashAttribute("success", "Watchlist berhasil ditambahkan.");
        return "redirect:/watchlists";
    }

    // ===========================
    // EDIT WATCHLIST
    // ===========================
    @PostMapping("/edit")
    public String postEditWatchList(
            @Valid @ModelAttribute("watchListForm") WatchListForm watchListForm,
            BindingResult bindingResult, // <--- TAMBAHAN
            @RequestParam(value = "coverImage", required = false) MultipartFile coverImage,
            RedirectAttributes redirectAttributes) {

        User authUser = getAuthUser();
        if (authUser == null) return "redirect:/auth/logout";

        // 1. Cek Validasi
        if (bindingResult.hasErrors()) {
            String errorMessage = bindingResult.getFieldError() != null 
                    ? bindingResult.getFieldError().getDefaultMessage() 
                    : "Input tidak valid";
            redirectAttributes.addFlashAttribute("error", "Gagal Update: " + errorMessage);
            return "redirect:/watchlists";
        }

        if (watchListForm.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "ID watchlist tidak valid");
            return "redirect:/watchlists";
        }

        String statusInput = "Plan to Watch";
        if (watchListForm.getStatus() != null && !watchListForm.getStatus().isEmpty()) {
            statusInput = watchListForm.getStatus();
        }

        var updated = watchListService.updateWatchList(
                authUser.getId(),
                watchListForm.getId(),
                watchListForm.getTitle(),
                watchListForm.getType(),
                watchListForm.getGenre(),
                watchListForm.getRating(),
                watchListForm.getReleaseYear(),
                statusInput, 
                watchListForm.getNotes());

        if (updated == null) {
            redirectAttributes.addFlashAttribute("error", "Gagal memperbarui watchlist");
            return "redirect:/watchlists";
        }

        if (coverImage != null && !coverImage.isEmpty()) {
            try {
                String fileName = fileStorageService.storeFile(coverImage, updated.getId());
                watchListService.updateCover(updated.getId(), fileName);
            } catch (IOException e) {
                redirectAttributes.addFlashAttribute("error", "Data diupdate, tapi gagal upload poster baru");
                return "redirect:/watchlists";
            }
        }

        redirectAttributes.addFlashAttribute("success", "Watchlist berhasil diperbarui.");
        return "redirect:/watchlists";
    }

    // ===========================
    // DELETE WATCHLIST
    // ===========================
    @PostMapping("/delete")
    public String postDeleteWatchList(
            @Valid @ModelAttribute("watchListForm") WatchListForm watchListForm,
            BindingResult bindingResult, // <--- TAMBAHAN (Best Practice)
            RedirectAttributes redirectAttributes) {
        
        User authUser = getAuthUser();
        if (authUser == null) return "redirect:/auth/logout";

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Data tidak valid untuk dihapus");
            return "redirect:/watchlists";
        }

        if (watchListForm.getId() == null) {
            redirectAttributes.addFlashAttribute("error", "ID watchlist tidak valid");
            return "redirect:/watchlists";
        }

        boolean deleted = watchListService.deleteWatchList(authUser.getId(), watchListForm.getId());

        if (!deleted) {
            redirectAttributes.addFlashAttribute("error", "Gagal menghapus watchlist atau data tidak ditemukan");
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
        User authUser = getAuthUser();
        if (authUser == null) return "redirect:/auth/logout";

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

    // ===========================
    // EDIT COVER
    // ===========================
    @PostMapping("/edit-cover")
    public String postEditCoverWatchList(
            @Valid @ModelAttribute("coverWatchListForm") CoverWatchListForm coverWatchListForm,
            BindingResult bindingResult, // <--- TAMBAHAN
            RedirectAttributes redirectAttributes) {

        User authUser = getAuthUser();
        if (authUser == null) return "redirect:/auth/logout";

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Data cover tidak valid");
            return "redirect:/watchlists/" + coverWatchListForm.getId();
        }

        if (coverWatchListForm.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "File poster tidak boleh kosong");
            return "redirect:/watchlists/" + coverWatchListForm.getId();
        }

        try {
            String fileName = fileStorageService.storeFile(coverWatchListForm.getCoverFile(), coverWatchListForm.getId());
            watchListService.updateCover(coverWatchListForm.getId(), fileName);
            redirectAttributes.addFlashAttribute("success", "Poster berhasil diupload");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Gagal mengupload poster");
        }
        return "redirect:/watchlists/" + coverWatchListForm.getId();
    }

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
    // TOGGLE/CYCLE STATUS
    // =====================================================
    @PostMapping("/toggle-status")
    public String toggleWatchedStatus(@RequestParam UUID id, RedirectAttributes redirectAttributes) {
        User authUser = getAuthUser();
        if (authUser == null) return "redirect:/auth/logout";

        WatchList updated = watchListService.cycleStatus(authUser.getId(), id);
        
        if (updated == null) {
            redirectAttributes.addFlashAttribute("error", "Gagal mengubah status");
            return "redirect:/watchlists";
        }

        String status = updated.getStatus();
        redirectAttributes.addFlashAttribute("success", "Status diubah menjadi " + status);
        
        return "redirect:/watchlists/" + id;
    }

    // Helper Authentication
    private User getAuthUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof AnonymousAuthenticationToken || !(authentication.getPrincipal() instanceof User)) {
            return null;
        }
        return (User) authentication.getPrincipal();
    }
}