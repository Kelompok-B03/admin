package id.ac.ui.cs.gatherlove.admin.service;

import id.ac.ui.cs.gatherlove.admin.dto.AnnouncementDTO;
import id.ac.ui.cs.gatherlove.admin.model.Announcement;
import id.ac.ui.cs.gatherlove.admin.repository.AnnouncementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AnnouncementServiceTest {

    @Mock
    private AnnouncementRepository announcementRepository;

    private AnnouncementService announcementService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        announcementService = new AnnouncementServiceImpl(announcementRepository);
    }

    @Test
    void getAllAnnouncements() {
        Announcement announcement1 = new Announcement();
        announcement1.setId(UUID.randomUUID());
        announcement1.setTitle("Pengumuman 1");
        announcement1.setContent("Konten 1");
        announcement1.setCreatedAt(LocalDateTime.now().minusDays(1));
        announcement1.setStatus(Announcement.AnnouncementStatus.ACTIVE);

        Announcement announcement2 = new Announcement();
        announcement2.setId(UUID.randomUUID());
        announcement2.setTitle("Pengumuman 2");
        announcement2.setContent("Konten 2");
        announcement2.setCreatedAt(LocalDateTime.now());
        announcement2.setStatus(Announcement.AnnouncementStatus.ACTIVE);

        List<Announcement> announcements = Arrays.asList(announcement1, announcement2);
        when(announcementRepository.findAllByOrderByCreatedAtDesc()).thenReturn(announcements);

        List<AnnouncementDTO> result = announcementService.getAllAnnouncements();

        assertEquals(2, result.size());
        assertEquals("Pengumuman 1", result.get(0).getTitle());
        assertEquals("Pengumuman 2", result.get(1).getTitle());
    }

    @Test
    void createAnnouncement() {
        AnnouncementDTO announcementDTO = new AnnouncementDTO();
        announcementDTO.setTitle("Pengumuman Baru");
        announcementDTO.setContent("Konten pengumuman baru");

        Announcement savedAnnouncement = new Announcement();
        savedAnnouncement.setId(UUID.randomUUID());
        savedAnnouncement.setTitle("Pengumuman Baru");
        savedAnnouncement.setContent("Konten pengumuman baru");
        savedAnnouncement.setCreatedAt(LocalDateTime.now());
        savedAnnouncement.setStatus(Announcement.AnnouncementStatus.ACTIVE);

        when(announcementRepository.save(any(Announcement.class))).thenReturn(savedAnnouncement);

        AnnouncementDTO result = announcementService.createAnnouncement(announcementDTO);

        assertNotNull(result);
        assertEquals("Pengumuman Baru", result.getTitle());
        assertEquals("Konten pengumuman baru", result.getContent());
        verify(announcementRepository, times(1)).save(any(Announcement.class));
    }

    @Test
    void deleteAnnouncement() {
        UUID id = UUID.randomUUID();
        Announcement announcement = new Announcement();
        announcement.setId(id);
        announcement.setTitle("Pengumuman untuk Dihapus");

        when(announcementRepository.findById(id)).thenReturn(Optional.of(announcement));

        boolean result = announcementService.deleteAnnouncement(id);

        assertTrue(result);
        verify(announcementRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteAnnouncementNotFound() {
        UUID id = UUID.randomUUID();
        when(announcementRepository.findById(id)).thenReturn(Optional.empty());

        boolean result = announcementService.deleteAnnouncement(id);

        assertFalse(result);
        verify(announcementRepository, never()).deleteById(any());
    }
}