package id.ac.ui.cs.gatherlove.admin.controller;

import id.ac.ui.cs.gatherlove.admin.dto.AnnouncementDTO;
import id.ac.ui.cs.gatherlove.admin.service.AnnouncementService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AnnouncementControllerTest {

    private MockMvc mockMvc;

    @Mock
    private AnnouncementService announcementService;

    private AnnouncementController announcementController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        announcementController = new AnnouncementController(announcementService);
        mockMvc = MockMvcBuilders.standaloneSetup(announcementController).build();
    }

    @Test
    void getAllAnnouncements() throws Exception {
        AnnouncementDTO announcement1 = new AnnouncementDTO();
        announcement1.setId(UUID.randomUUID());
        announcement1.setTitle("Pengumuman 1");
        announcement1.setContent("Konten 1");
        announcement1.setCreatedAt(LocalDateTime.now().minusDays(1));
        announcement1.setStatus("ACTIVE");

        AnnouncementDTO announcement2 = new AnnouncementDTO();
        announcement2.setId(UUID.randomUUID());
        announcement2.setTitle("Pengumuman 2");
        announcement2.setContent("Konten 2");
        announcement2.setCreatedAt(LocalDateTime.now());
        announcement2.setStatus("ACTIVE");

        List<AnnouncementDTO> announcements = Arrays.asList(announcement1, announcement2);

        when(announcementService.getAllAnnouncements()).thenReturn(announcements);

        // Perbaikan URL - sesuaikan dengan endpoint di controller
        mockMvc.perform(get("/api/admin/announcements/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Pengumuman 1"))
                .andExpect(jsonPath("$[1].title").value("Pengumuman 2"));
    }

    @Test
    void createAnnouncement() throws Exception {
        AnnouncementDTO requestDTO = new AnnouncementDTO();
        requestDTO.setTitle("Pengumuman Baru");
        requestDTO.setContent("Konten pengumuman baru");

        AnnouncementDTO responseDTO = new AnnouncementDTO();
        responseDTO.setId(UUID.randomUUID());
        responseDTO.setTitle("Pengumuman Baru");
        responseDTO.setContent("Konten pengumuman baru");
        responseDTO.setCreatedAt(LocalDateTime.now());
        responseDTO.setStatus("ACTIVE");

        when(announcementService.createAnnouncement(any(AnnouncementDTO.class))).thenReturn(responseDTO);

        // Perbaikan URL - sesuaikan dengan endpoint di controller
        mockMvc.perform(post("/api/admin/announcements/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Pengumuman Baru\",\"content\":\"Konten pengumuman baru\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Pengumuman Baru"))
                .andExpect(jsonPath("$.content").value("Konten pengumuman baru"));
    }

    @Test
    void deleteAnnouncement() throws Exception {
        UUID id = UUID.randomUUID();
        when(announcementService.deleteAnnouncement(id)).thenReturn(true);

        mockMvc.perform(delete("/api/admin/announcements/" + id))
                .andExpect(status().isOk());

        verify(announcementService, times(1)).deleteAnnouncement(id);
    }

    @Test
    void deleteAnnouncementNotFound() throws Exception {
        UUID id = UUID.randomUUID();
        when(announcementService.deleteAnnouncement(id)).thenReturn(false);

        mockMvc.perform(delete("/api/admin/announcements/" + id))
                .andExpect(status().isNotFound());

        verify(announcementService, times(1)).deleteAnnouncement(id);
    }
}