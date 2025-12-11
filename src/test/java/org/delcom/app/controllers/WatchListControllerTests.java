package org.delcom.app.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.delcom.app.configs.ApiResponse;
import org.delcom.app.configs.AuthContext;
import org.delcom.app.entities.User;
import org.delcom.app.entities.WatchList;
import org.delcom.app.services.WatchListService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class WatchListControllerTests {

    private MockMvc mockMvc;

    @Mock
    private WatchListService watchListService;

    @Mock
    private AuthContext authContext;

    @InjectMocks
    private WatchListController controller;

    private ObjectMapper objectMapper;
    private User user;
    private WatchList watchList;
    private UUID userId;
    private UUID watchListId;

    @BeforeEach
    void setUp() {
        // Inject AuthContext secara manual karena field injection @Autowired
        ReflectionTestUtils.setField(controller, "authContext", authContext);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();

        userId = UUID.randomUUID();
        watchListId = UUID.randomUUID();

        user = mock(User.class);
        lenient().when(user.getId()).thenReturn(userId);

        watchList = new WatchList();
        watchList.setId(watchListId);
        watchList.setTitle("Inception");
        watchList.setType("Movie");
        watchList.setGenre("Sci-Fi");
        watchList.setRating(9);
        watchList.setReleaseYear(2010);
        watchList.setStatus("Plan to Watch");
        watchList.setNotes("Mind blowing");
    }

    // ==================================================================================
    // 1. CREATE WATCHLIST TESTS
    // ==================================================================================

    @Test
    void testCreate_Success() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(user);
        when(watchListService.createWatchList(any(), anyString(), anyString(), anyString(), anyInt(), anyInt(), anyString(), anyString()))
                .thenReturn(watchList);

        mockMvc.perform(post("/api/watchlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.id").value(watchListId.toString()));
    }

    @Test
    void testCreate_Success_DefaultStatus() throws Exception {
        // Set status null agar masuk ke logika "if (status == null || status.isEmpty())"
        watchList.setStatus(null);

        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(user);
        
        // Mock return object harus punya ID
        WatchList created = new WatchList();
        created.setId(watchListId);
        when(watchListService.createWatchList(any(), any(), any(), any(), any(), any(), eq("Plan to Watch"), any()))
                .thenReturn(created);

        mockMvc.perform(post("/api/watchlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
        
        // Verifikasi bahwa service dipanggil dengan "Plan to Watch"
        verify(watchListService).createWatchList(any(), any(), any(), any(), any(), any(), eq("Plan to Watch"), any());
    }

    @Test
    void testCreate_Unauthenticated() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(false);

        mockMvc.perform(post("/api/watchlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("User tidak terautentikasi"));
    }

    // --- Validation Tests for Create ---

    @Test
    void testCreate_TitleInvalid() throws Exception {
        watchList.setTitle(""); // Empty
        mockMvc.perform(post("/api/watchlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Title tidak boleh kosong"));
        
        watchList.setTitle(null); // Null
        mockMvc.perform(post("/api/watchlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Title tidak boleh kosong"));
    }

    @Test
    void testCreate_TypeInvalid() throws Exception {
        watchList.setType("Cartoon"); // Invalid String
        mockMvc.perform(post("/api/watchlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Type harus Movie atau Series"));

        watchList.setType(null); // Null
        mockMvc.perform(post("/api/watchlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Type harus Movie atau Series"));
    }

    @Test
    void testCreate_GenreInvalid() throws Exception {
        watchList.setGenre(""); // Empty
        mockMvc.perform(post("/api/watchlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Genre tidak boleh kosong"));

        watchList.setGenre(null); // Null
        mockMvc.perform(post("/api/watchlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Genre tidak boleh kosong"));
    }

    @Test
    void testCreate_RatingInvalid() throws Exception {
        watchList.setRating(0); // < 1
        mockMvc.perform(post("/api/watchlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Rating harus antara 1 sampai 10"));

        watchList.setRating(11); // > 10
        mockMvc.perform(post("/api/watchlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Rating harus antara 1 sampai 10"));

        watchList.setRating(null); // Null
        mockMvc.perform(post("/api/watchlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Rating harus antara 1 sampai 10"));
    }

    @Test
    void testCreate_YearInvalid() throws Exception {
        watchList.setReleaseYear(1899); // < 1900
        mockMvc.perform(post("/api/watchlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Tahun rilis tidak valid"));

        watchList.setReleaseYear(null); // Null
        mockMvc.perform(post("/api/watchlists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Tahun rilis tidak valid"));
    }

    // ==================================================================================
    // 2. GET ALL TESTS
    // ==================================================================================

    @Test
    void testGetAll_Success_NoSearch() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(user);
        when(watchListService.getAllWatchLists(userId, "")).thenReturn(List.of(watchList));

        mockMvc.perform(get("/api/watchlists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.watch_lists[0].title").value("Inception"));
    }

    @Test
    void testGetAll_Success_WithSearch() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(user);
        when(watchListService.getAllWatchLists(userId, "Incep")).thenReturn(List.of(watchList));

        mockMvc.perform(get("/api/watchlists").param("search", "Incep"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
        
        verify(watchListService).getAllWatchLists(userId, "Incep");
    }

    @Test
    void testGetAll_Unauthenticated() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(false);

        mockMvc.perform(get("/api/watchlists"))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("User tidak terautentikasi"));
    }

    // ==================================================================================
    // 3. GET BY ID TESTS
    // ==================================================================================

    @Test
    void testGetById_Success() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(user);
        when(watchListService.getWatchListById(userId, watchListId)).thenReturn(watchList);

        mockMvc.perform(get("/api/watchlists/" + watchListId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.watch_list.title").value("Inception"));
    }

    @Test
    void testGetById_NotFound() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(user);
        when(watchListService.getWatchListById(userId, watchListId)).thenReturn(null);

        mockMvc.perform(get("/api/watchlists/" + watchListId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Data watchlist tidak ditemukan"));
    }

    @Test
    void testGetById_Unauthenticated() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(false);

        mockMvc.perform(get("/api/watchlists/" + watchListId))
                .andExpect(status().isForbidden());
    }

    // ==================================================================================
    // 4. GET GENRES TESTS
    // ==================================================================================

    @Test
    void testGetGenres_Success() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(user);
        when(watchListService.getAllGenres(userId)).thenReturn(List.of("Action", "Sci-Fi"));

        mockMvc.perform(get("/api/watchlists/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.genres[0]").value("Action"));
    }

    @Test
    void testGetGenres_Unauthenticated() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(false);

        mockMvc.perform(get("/api/watchlists/genres"))
                .andExpect(status().isForbidden());
    }

    // ==================================================================================
    // 5. UPDATE TESTS
    // ==================================================================================

    @Test
    void testUpdate_Success() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(user);
        when(watchListService.updateWatchList(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(watchList);

        mockMvc.perform(put("/api/watchlists/" + watchListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));
    }

    @Test
    void testUpdate_Success_DefaultStatus() throws Exception {
        watchList.setStatus(null); // Set null to trigger default logic in update
        
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(user);
        when(watchListService.updateWatchList(any(), any(), any(), any(), any(), any(), any(), eq("Plan to Watch"), any()))
                .thenReturn(watchList);

        mockMvc.perform(put("/api/watchlists/" + watchListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isOk());
        
        verify(watchListService).updateWatchList(any(), any(), any(), any(), any(), any(), any(), eq("Plan to Watch"), any());
    }

    @Test
    void testUpdate_NotFound() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(user);
        when(watchListService.updateWatchList(any(), any(), any(), any(), any(), any(), any(), any(), any()))
                .thenReturn(null);

        mockMvc.perform(put("/api/watchlists/" + watchListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Data watchlist tidak ditemukan"));
    }

    @Test
    void testUpdate_Unauthenticated() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(false);

        mockMvc.perform(put("/api/watchlists/" + watchListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isForbidden());
    }

    // --- Validation Tests for Update (Strict checks) ---

    @Test
    void testUpdate_TitleInvalid() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(true);
        
        watchList.setTitle(null);
        mockMvc.perform(put("/api/watchlists/" + watchListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Title invalid"));

        watchList.setTitle("");
        mockMvc.perform(put("/api/watchlists/" + watchListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Title invalid"));
    }

    @Test
    void testUpdate_TypeInvalid() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(true);
        
        watchList.setType(null);
        mockMvc.perform(put("/api/watchlists/" + watchListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Type invalid"));
    }

    @Test
    void testUpdate_GenreInvalid() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(true);
        
        watchList.setGenre(null);
        mockMvc.perform(put("/api/watchlists/" + watchListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Genre invalid"));
    }

    @Test
    void testUpdate_RatingInvalid() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(true);
        
        watchList.setRating(null);
        mockMvc.perform(put("/api/watchlists/" + watchListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Rating invalid"));

        watchList.setRating(11);
        mockMvc.perform(put("/api/watchlists/" + watchListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Rating invalid"));
        
        watchList.setRating(0);
        mockMvc.perform(put("/api/watchlists/" + watchListId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(watchList)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Rating invalid"));
    }

    // ==================================================================================
    // 6. DELETE TESTS
    // ==================================================================================

    @Test
    void testDelete_Success() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(user);
        when(watchListService.deleteWatchList(userId, watchListId)).thenReturn(true);

        mockMvc.perform(delete("/api/watchlists/" + watchListId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.message").value("Data watchlist berhasil dihapus"));
    }

    @Test
    void testDelete_NotFound() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(user);
        when(watchListService.deleteWatchList(userId, watchListId)).thenReturn(false);

        mockMvc.perform(delete("/api/watchlists/" + watchListId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Data watchlist tidak ditemukan"));
    }

    @Test
    void testDelete_Unauthenticated() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(false);

        mockMvc.perform(delete("/api/watchlists/" + watchListId))
                .andExpect(status().isForbidden());
    }

    // ==================================================================================
    // 7. TOGGLE STATUS TESTS
    // ==================================================================================

    @Test
    void testToggleWatched_Success() throws Exception {
        WatchList updated = new WatchList();
        updated.setStatus("Watching");

        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(user);
        when(watchListService.cycleStatus(userId, watchListId)).thenReturn(updated);

        mockMvc.perform(patch("/api/watchlists/" + watchListId + "/toggle-watched"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.status").value("Watching"));
    }

    @Test
    void testToggleWatched_NotFound() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(user);
        when(watchListService.cycleStatus(userId, watchListId)).thenReturn(null);

        mockMvc.perform(patch("/api/watchlists/" + watchListId + "/toggle-watched"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Data watchlist tidak ditemukan"));
    }

    @Test
    void testToggleWatched_Unauthenticated() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(false);

        mockMvc.perform(patch("/api/watchlists/" + watchListId + "/toggle-watched"))
                .andExpect(status().isForbidden());
    }

    // ==================================================================================
    // 8. STATISTICS TESTS
    // ==================================================================================

    @Test
    void testGetStatistics_Success() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(true);
        when(authContext.getAuthUser()).thenReturn(user);
        
        when(watchListService.getGenreStatistics(userId)).thenReturn(Collections.emptyList());
        when(watchListService.getStatusStatistics(userId)).thenReturn(Collections.emptyList());
        when(watchListService.getTypeStatistics(userId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/watchlists/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"))
                .andExpect(jsonPath("$.data.genreStatistics").exists())
                .andExpect(jsonPath("$.data.statusStatistics").exists())
                .andExpect(jsonPath("$.data.typeStatistics").exists());
    }

    @Test
    void testGetStatistics_Unauthenticated() throws Exception {
        when(authContext.isAuthenticated()).thenReturn(false);

        mockMvc.perform(get("/api/watchlists/statistics"))
                .andExpect(status().isForbidden());
    }
}